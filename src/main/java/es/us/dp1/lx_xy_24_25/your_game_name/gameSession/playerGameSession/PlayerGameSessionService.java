package es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardState;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardTemplate;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Color;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.EnergyEffectType;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.playerCard.PlayerCard;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.playerCard.PlayerCardRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.BadRequestException;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameMode;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSessionRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameState;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class PlayerGameSessionService {

    private static final Logger logger = LoggerFactory.getLogger(PlayerGameSessionService.class);
    private final PlayerGameSessionRepository repository;
    private final PlayerCardRepository playerCardRepository;
    private final GameSessionRepository gameSessionRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public PlayerGameSessionService(PlayerGameSessionRepository repository, PlayerCardRepository playerCardRepository,
            GameSessionRepository gameSessionRepository) {
        this.repository = repository;
        this.playerCardRepository = playerCardRepository;
        this.gameSessionRepository = gameSessionRepository;
    }


    public boolean playerHasActiveGame(Player player) {
    return repository.existsActiveGameForPlayer(
        player.getId(),
        List.of(GameState.PENDING, GameState.ACTIVE)
    );
}


    @Transactional
    public PlayerGameSession save(PlayerGameSession playerGameSession) {
        return repository.save(playerGameSession);
    }

    @Transactional
    public PlayerGameSession createPlayerForGame(GameSession session, Player player, Color color) {
        PlayerGameSession pgs = new PlayerGameSession();
        pgs.setPlayer(player);
        pgs.setGameSession(session);
        pgs.setPlayerColor(color);
        pgs.setEnergy(3);
        session.getPlayers().add(pgs);
        return pgs;
    }

    @Transactional
    public void delete(PlayerGameSession playerGameSession) {
        logger.debug("Attempting to delete PlayerGameSession with ID: {}", playerGameSession.getId());
        try {
            if (entityManager.contains(playerGameSession)) {
                logger.debug("Entity is managed, proceeding with delete");
                repository.delete(playerGameSession);
            } else {
                logger.debug("Entity is detached, merging before delete");
                PlayerGameSession managedEntity = entityManager.merge(playerGameSession);
                repository.delete(managedEntity);
            }
            entityManager.flush();
            logger.debug("Successfully deleted PlayerGameSession with ID: {}", playerGameSession.getId());
        } catch (Exception e) {
            logger.error("Error during delete/flush for PlayerGameSession ID: {}", playerGameSession.getId(), e);
            throw e;
        }
    }

    @Transactional
    public void deleteById(Integer id) {
        logger.debug("Attempting to delete PlayerGameSession by ID: {}", id);
        try {
            repository.deleteById(id);
            entityManager.flush();
            logger.debug("Successfully deleted PlayerGameSession by ID: {}", id);
        } catch (Exception e) {
            logger.error("Error during deleteById or flush for ID: {}", id, e);
            throw e;
        }
    }

    @Transactional
    public PlayerGameSession consumeEnergy(Integer gameSessionId, String username, String actionId) {
        PlayerGameSession pgs = repository
                .findByGameSessionIdAndPlayerUsername(gameSessionId, username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "The player does not belong to the game session."));

        if (pgs.getEnergy() == null || pgs.getEnergy() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No energy left.");
        }
        if (pgs.getCardsPlacedThisRound() != null && pgs.getCardsPlacedThisRound() > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can only use energy before placing cards.");
        }

        GameSession gameSession = pgs.getGameSession();
        if (gameSession.getRound() < 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Energy is only available from round 3 onwards.");
        }
        if (pgs.getLastEnergyRoundUsed() != null &&
                pgs.getLastEnergyRoundUsed().equals(gameSession.getRound())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have already used energy this round.");
        }

        EnergyEffectType effect;
        try {
            effect = EnergyEffectType.valueOf(actionId.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid energy action.");
        }

        pgs.setActiveEnergyEffect(effect);
        pgs.setEnergy(pgs.getEnergy() - 1);
        pgs.setLastEnergyRoundUsed(gameSession.getRound());
        pgs.setEnergyCardsToPlaceOverride(null);
        pgs.setEnergyAllowPenultimateStart(false);
        pgs.setEnergyPendingExtraDraw(false);

        switch (effect) {
            case BOOST -> pgs.setEnergyCardsToPlaceOverride(3);
            case BRAKE -> pgs.setEnergyCardsToPlaceOverride(1);
            case REVERSE -> pgs.setEnergyAllowPenultimateStart(true);
            case EXTRA_FUEL -> {
                drawExtraCard(pgs);
                pgs.setEnergyPendingExtraDraw(false);
            }
            case JUMP_LINE -> {
                if (gameSession.getGameMode() != GameMode.TEAMBATTLE) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Jump Line only available in Team Battle.");
                }
            }
        }

        return repository.save(pgs);

    }

    @Transactional
    public CardTemplate drawFirstCardForInitiative(Integer gameSessionId, String username) {
        PlayerGameSession pgs = repository
                .findByGameSessionIdAndPlayerUsername(gameSessionId, username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found"));

        if (pgs.getInitiativeCard() != null) {
            return pgs.getInitiativeCard();
        }

        if (pgs.getCards() == null || pgs.getCards().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "The player has no cards for initiative.");
        }

        List<PlayerCard> candidates = pgs.getCards().stream()
                .filter(c -> c.getLocation() != null && c.getLocation().toString().equals("DECK"))
                .collect(Collectors.toList());

        if (candidates.isEmpty()) {
            candidates = new ArrayList<>(pgs.getCards());
        }

        if (candidates.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No cards found.");
        }

        Random random = new Random();
        PlayerCard randomCard = candidates.get(random.nextInt(candidates.size()));

        pgs.setInitiativeCard(randomCard.getTemplate());
        repository.save(pgs);

        return randomCard.getTemplate();
    }

    @Transactional
    public void drawTopCard(PlayerGameSession pgs) {
        Optional<PlayerCard> topCard = playerCardRepository
                .findFirstByPlayerAndLocationOrderByDeckOrderAsc(pgs, CardState.DECK);

        if (topCard.isPresent()) {
            PlayerCard card = topCard.get();
            card.setLocation(CardState.HAND);
            playerCardRepository.save(card);
        } else {
            logger.warn("Attempted to draw card but deck is empty for PlayerGameSession ID: {}", pgs.getId());
        }
    }

    @Transactional
    public void discardActiveCard(Integer gameId, String username, Integer cardId) {
        PlayerCard card = playerCardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card", "id", cardId));

        if (card.getLocation() != CardState.HAND) {
            throw new BadRequestException("You can only discard cards from your hand.");
        }
        card.setLocation(CardState.DISCARD);
        playerCardRepository.save(card);

        PlayerGameSession pgs = card.getPlayer();
        pgs.getDiscardPile().add(card.getTemplate());
        repository.save(pgs);
        drawTopCard(card.getPlayer());
    }

    @Transactional
    public void drawCardFromDeckAction(Integer gameId, String username) {
        GameSession game = gameSessionRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found"));
        PlayerGameSession pgs = game.getPlayers().stream()
                .filter(p -> p.getPlayer().getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Player not found in this match"));

        boolean isSolitaire = game.getGameMode() == GameMode.SOLITAIRE;
        if (!isSolitaire) {
            throw new BadRequestException("This action is only allowed in Solitaire mode.");
        }
        long cardsInHand = playerCardRepository.countByPlayerAndLocationAndUsedFalse(pgs, CardState.HAND);

        if (cardsInHand > 0) {
            throw new BadRequestException("You have cards in your hand, you cannot draw.");
        }
        drawTopCard(pgs);
    }

    @Transactional
    public void drawCardFromDiscardAction(Integer gameId, String username) {
        GameSession game = gameSessionRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found"));

        PlayerGameSession pgs = game.getPlayers().stream()
                .filter(p -> p.getPlayer().getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Player not found"));

        boolean isSolitaire = game.getGameMode() == GameMode.SOLITAIRE;
        if (!isSolitaire)
            throw new BadRequestException("This action is only allowed in Solitaire mode.");

        long cardsInHand = playerCardRepository.countByPlayerAndLocationAndUsedFalse(pgs, CardState.HAND);
        if (cardsInHand > 0)
            throw new BadRequestException("You have cards in your hand, you cannot draw.");

        List<CardTemplate> pile = pgs.getDiscardPile();
        if (pile.isEmpty()) {
            throw new BadRequestException("There are no cards in the discard pile.");
        }
        int lastIndex = pile.size() - 1;
        CardTemplate templateToRecover = pile.get(lastIndex);

        pile.remove(lastIndex);

        List<PlayerCard> physicalCards = playerCardRepository.findByPlayer(pgs);

        PlayerCard cardToRecover = physicalCards.stream()
                .filter(c -> c.getLocation() == CardState.DISCARD)
                .filter(c -> c.getTemplate().getId().equals(templateToRecover.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Integrity error: Visual pile has a card but it doesn't exist physically."));

        cardToRecover.setLocation(CardState.HAND);
        playerCardRepository.save(cardToRecover);
        repository.save(pgs);
    }

    private void drawExtraCard(PlayerGameSession pgs) {
        List<PlayerCard> deckCards = playerCardRepository.findByPlayer(pgs).stream()
                .filter(c -> c.getLocation() == CardState.DECK && !c.getUsed())
                .sorted(Comparator.comparingInt(PlayerCard::getDeckOrder))
                .toList();

        if (!deckCards.isEmpty()) {
            PlayerCard top = deckCards.get(0);
            top.setLocation(CardState.HAND);
            playerCardRepository.save(top);
        }
    }

    @Transactional
    public void switchTeam(Integer gameId, String username, Integer newTeamNumber) {
        GameSession game = gameSessionRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found"));

        if (game.getGameMode() != GameMode.TEAMBATTLE) {
            throw new BadRequestException("This action is only allowed in Team Battle");
        }

        if (game.getState() != GameState.PENDING) {
            throw new BadRequestException("This action is only allowed in the lobby (PENDING)");
        }

        if (newTeamNumber == null || (newTeamNumber != 1 && newTeamNumber != 2)) {
            throw new BadRequestException("Invalid team number. Must be 1 or 2.");
        }

        PlayerGameSession pgs = game.getPlayers().stream()
                .filter(p -> p.getPlayer().getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Player not found in this match"));

        if (pgs.getTeamNumber() != null && pgs.getTeamNumber().equals(newTeamNumber)) {
            return;
        }

        long targetTeamCount = game.getPlayers().stream()
                .filter(p -> p.getTeamNumber() != null && p.getTeamNumber().equals(newTeamNumber))
                .count();

        int maxPerTeam = (int) Math.ceil((double) game.getNumPlayers() / 2);

        if (targetTeamCount >= maxPerTeam) {
            throw new BadRequestException("Team " + newTeamNumber + " is full.");
        }

        pgs.setTeamNumber(newTeamNumber);
        repository.save(pgs);
    }
}
