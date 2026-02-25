package es.us.dp1.lx_xy_24_25.your_game_name.gameSession;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardState;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardTemplate;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardTemplateRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardType;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Color;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.EnergyEffectType;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Orientation;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.SpectatorGameDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.StartPositionDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.placedCard.PlacedCard;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.placedCard.PlacedCardRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.placedCard.PlacedCardService;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.playerCard.PlayerCard;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.playerCard.PlayerCardRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.GameSessionDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.GameStartDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.PlaceCardRequestDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.BadRequestException;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.PlayerGameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.PlayerGameSessionRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.PlayerGameSessionService;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerService;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.playerStatistics.StatisticsUpdateService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserRepository;
import jakarta.validation.Valid;

@Service
public class GameSessionService {

    private final UserRepository userRepository;
    private final GameSessionRepository repo;
    private final PlayerService playerService;
    private final PlayerCardRepository playerCardRepository;
    private final CardTemplateRepository cardTemplateRepository;
    private final StatisticsUpdateService statisticsUpdateService;
    private final PlacedCardService placedCardService;
    private final PlayerGameSessionRepository playerGameSessionRepository;
    private final PlayerGameSessionService playerGameSessionService;
    private final PlacedCardRepository placedCardRepository;

    private final Integer maxCardRound0 = 1;
    private final Integer maxCardRoundN = 2;

    @Autowired
    public GameSessionService(GameSessionRepository repo, UserRepository userRepository, PlayerService playerService,
            PlayerCardRepository playerCardRepository,
            CardTemplateRepository cardTemplateRepository, StatisticsUpdateService statisticsUpdateService,
            PlacedCardService placedCardService, PlacedCardRepository placedCardRepository,
            PlayerGameSessionRepository playerGameSessionRepository,
            PlayerGameSessionService playerGameSessionService) {
        this.repo = repo;
        this.userRepository = userRepository;
        this.playerService = playerService;
        this.playerCardRepository = playerCardRepository;
        this.cardTemplateRepository = cardTemplateRepository;
        this.statisticsUpdateService = statisticsUpdateService;
        this.placedCardService = placedCardService;
        this.placedCardRepository = placedCardRepository;
        this.playerGameSessionRepository = playerGameSessionRepository;
        this.playerGameSessionService = playerGameSessionService;
    }

    @Transactional(readOnly = true)
    public List<GameSession> getActiveGames() {
        return repo.findByState(GameState.ACTIVE);
    }

    @Transactional(readOnly = true)
    public List<GameSession> getFinishedGames() {
        return repo.findByState(GameState.FINISHED);
    }

    @Transactional(readOnly = true)
    public List<GameSession> getPendingGames() {
        return repo.findByState(GameState.PENDING);
    }

    @Transactional(readOnly = true)
    public List<GameSession> getAll() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public GameSession getGameById(int id) {
        return repo.findByIdWithPlayers(id).orElse(null);
    }

    public int calculateBoardSize(GameMode gameMode, int maxPlayers) {
        if (gameMode == null)
            return 0;
        if (gameMode == GameMode.SOLITARY_PUZZLE || gameMode == GameMode.SOLITAIRE)
            return 5;
        if (maxPlayers <= 3)
            return 7;
        if (maxPlayers <= 5)
            return 9;
        if (maxPlayers <= 7)
            return 11;
        if (maxPlayers == 8)
            return 13;
        return 0;
    }

    public void initializePlayerDeck(PlayerGameSession playerGameSession, int initialHandSize) {
        List<CardTemplate> templates = cardTemplateRepository.findAll()
                .stream()
                .filter(t -> t.getType() == CardType.LINE)
                .sorted(Comparator.comparingInt(CardTemplate::getImageId))
                .toList();

        if (templates.size() < 8) {
            long totalTemplates = cardTemplateRepository.count();
            long lineCards = cardTemplateRepository.findAll().stream().filter(t -> t.getType() == CardType.LINE)
                    .count();
            throw new IllegalStateException(
                    "Not enough LINE templates (has " + lineCards + " of 8). Total in DB: " + totalTemplates);
        }

        List<PlayerCard> allCards = new ArrayList<>();

        for (CardTemplate template : templates) {
            int quantity = (template.getImageId() == 8) ? 4 : 3;

            for (int i = 0; i < quantity; i++) {
                PlayerCard card = new PlayerCard();
                card.setPlayer(playerGameSession);
                card.setTemplate(template);
                card.setLocation(CardState.DECK);
                card.setUsed(false);
                allCards.add(card);
            }
        }

        Collections.shuffle(allCards);

        for (int i = 0; i < allCards.size(); i++) {
            PlayerCard card = allCards.get(i);
            card.setDeckOrder(i);
            if (i < initialHandSize) {
                card.setLocation(CardState.HAND);
            } else {
                card.setLocation(CardState.DECK);
            }
        }
        playerCardRepository.saveAll(allCards);

        if (playerGameSession.getCards() == null) {
            playerGameSession.setCards(new HashSet<>());
        }
        playerGameSession.getCards().addAll(allCards);
    }

    @Transactional
    public GameSession save(GameSession session) {
        if (session.getState() == null) {
            session.setState(GameState.PENDING);
        }

        if (session.getState() == GameState.PENDING) {
            session.setStartTime(null);
            session.setEndTime(null);
        }
        session.setWinner(null);
        boolean isSolitary = session.getGameMode() == GameMode.SOLITAIRE
                || session.getGameMode() == GameMode.SOLITARY_PUZZLE;
        session.setRound(isSolitary ? 1 : 0);

        User hostUser = userRepository.findByUsername(session.getHost())
                .orElseThrow(() -> new UsernameNotFoundException("Host user not found"));

        Player hostPlayer = playerService.findPlayer(hostUser.getUsername());
        if (hostPlayer == null) {
            throw new ResourceNotFoundException("Player entity not found for host user: " + hostUser.getUsername());
        }

        PlayerGameSession hostLink = new PlayerGameSession();
        hostLink.setPlayer(hostPlayer);
        hostLink.setGameSession(session);
        hostLink.setEnergy(3);
        hostLink.setPlayerColor(Color.BLUE);
        if (session.getGameMode() == GameMode.TEAMBATTLE) {
            hostLink.setTeamNumber(1);
        }

        session.getPlayers().add(hostLink);

        GameSession savedSession = repo.save(session);
        return savedSession;
    }

    @Transactional
    public GameSession update(@Valid GameSession game, Integer id) {
        GameSession persisted = getGameById(id);
        if (persisted == null) {
            throw new ResourceNotFoundException("Game session not found");
        }
        BeanUtils.copyProperties(game, persisted, "id", "players", "placedCards", "chatMessages");

        Map<Integer, PlayerGameSession> incoming = game.getPlayers().stream()
                .filter(pgs -> pgs.getPlayer() != null && pgs.getPlayer().getId() != null)
                .collect(Collectors.toMap(pgs -> pgs.getPlayer().getId(), Function.identity(), (a, b) -> a));

        persisted.getPlayers().removeIf(pgs -> {
            Integer playerId = pgs.getPlayer() != null ? pgs.getPlayer().getId() : null;
            return playerId != null && !incoming.containsKey(playerId);
        });

        incoming.forEach((playerId, incomingPgs) -> {
            PlayerGameSession existing = persisted.getPlayers().stream()
                    .filter(pgs -> pgs.getPlayer() != null && playerId.equals(pgs.getPlayer().getId()))
                    .findFirst()
                    .orElse(null);
            if (existing == null) {
                incomingPgs.setGameSession(persisted);
                persisted.getPlayers().add(incomingPgs);
            } else {
                BeanUtils.copyProperties(incomingPgs, existing, "id", "gameSession", "player");
            }
        });

        return repo.save(persisted);
    }

    @Transactional
    public GameSession delete(Integer id) {
        GameSession gameToDelete = getGameById(id);
        repo.delete(gameToDelete);
        return gameToDelete;
    }

    @Transactional
    public GameStartDTO startGame(Integer gameId, User user, GameSession gamesession) {
        GameSession game = getGameById(gameId);
        GameMode gameMode = game.getGameMode();
        int initialHandSize = 0;
        if (gameMode == GameMode.SOLITAIRE) {
            initialHandSize = 1;
        } else {
            initialHandSize = 5;
        }

        if (game == null || game.getState() != GameState.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Game cannot be started");
        }

        if (game.getGameMode() == GameMode.VERSUS && game.getPlayers().size() != 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Versus mode requires exactly 2 players");
        }

        if (game.getGameMode() == GameMode.BATTLE_ROYALE && game.getPlayers().size() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot start Battle Royale with less than 2 players");
        }

        if ((game.getGameMode() == GameMode.TEAMBATTLE) && game.getPlayers().size() < 4) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot start Team Battle with less than 4 players");
        }

        game.setState(GameState.ACTIVE);
        game.setStartTime(LocalDateTime.now());

        int numPlayers = game.getPlayers().size();
        int board = calculateBoardSize(gamesession.getGameMode(), numPlayers);
        game.setBoardSize(board);

        for (PlayerGameSession pgs : game.getPlayers()) {
            initializePlayerDeck(pgs, initialHandSize);
        }
        placeStartCards(game);
        for (PlayerGameSession pgs : game.getPlayers()) {
            playerGameSessionService.drawFirstCardForInitiative(
                    game.getId(),
                    pgs.getPlayer().getUsername());
        }

        List<PlayerGameSession> sortedPlayers = game.getPlayers().stream()
                .sorted((p1, p2) -> {
                    if (p1.getInitiativeCard() == null || p2.getInitiativeCard() == null)
                        return 0;
                    int init1 = p1.getInitiativeCard().getInitiative();
                    int init2 = p2.getInitiativeCard().getInitiative();
                    if (init1 != init2) {
                        return Integer.compare(init1, init2);
                    }
                    return Integer.compare(p1.getId(), p2.getId());
                })
                .collect(Collectors.toList());

        for (int i = 0; i < sortedPlayers.size(); i++) {
            PlayerGameSession pgs = sortedPlayers.get(i);
            pgs.setTurnOrder(i);
        }
        playerGameSessionRepository.saveAll((sortedPlayers));

        if (!sortedPlayers.isEmpty()) {
            game.setGamePlayerTurnId(sortedPlayers.get(0).getId());
        } else {
            throw new IllegalStateException("No players to start the game");
        }
        game.setRound(1);
        repo.save(game);

        return new GameStartDTO(game, user);
    }

    @Transactional
    public GameSession finishGame(Integer gameId, String winnerUsername) {
        GameSession game = getGameById(gameId);

        if (game == null || game.getState() != GameState.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Game cannot be finished or not found");
        }

        PlayerGameSession winnerSession = game.getPlayers().stream()
                .filter(p -> p.getPlayer().getUsername().equals(winnerUsername))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Winner player not found"));

        game.setState(GameState.FINISHED);
        game.setEndTime(LocalDateTime.now());
        if (game.getGameMode() == GameMode.TEAMBATTLE && winnerSession.getTeamNumber() != null) {
            List<String> teamWinners = game.getPlayers().stream()
                    .filter(p -> winnerSession.getTeamNumber().equals(p.getTeamNumber()))
                    .map(p -> p.getPlayer().getUsername())
                    .sorted()
                    .toList();
            game.setWinner(String.join(", ", teamWinners));
        } else {
            game.setWinner(winnerUsername);
        }

        if (game.getStartTime() != null) {
            Duration duration = Duration.between(game.getStartTime(), game.getEndTime());
            game.setDuration(duration.getSeconds());
        }

        GameMode gameMode = game.getGameMode();
        if (gameMode == GameMode.SOLITAIRE || gameMode == GameMode.SOLITARY_PUZZLE) {
            int energyLeft = winnerSession.getEnergy();
            int handPoints = 0;

            if (winnerSession.getCards() != null) {
                handPoints = winnerSession.getCards().stream()
                        .filter(c -> c.getLocation() == CardState.HAND)
                        .mapToInt(card -> card.getTemplate().getInitiative() != 0 ? card.getTemplate().getInitiative()
                                : 0)
                        .sum();
            }
            int deckPoints = 0;
            if (winnerSession.getCards() != null) {
                deckPoints = winnerSession.getCards().stream()
                        .filter(c -> c.getLocation() == CardState.DECK)
                        .mapToInt(card -> card.getTemplate().getInitiative() != 0 ? card.getTemplate().getInitiative()
                                : 0)
                        .sum();
            }
            int totalScore = energyLeft + handPoints + deckPoints;
            game.setWinnerScore(totalScore);
        }
        GameSession savedGame = repo.save(game);
        statisticsUpdateService.updateAfterGame(savedGame);

        return savedGame;
    }

    @Transactional
    public PlacedCard handleCardPlacement(Integer gameSessionId, String username, PlaceCardRequestDTO request) {
        GameSession gameSession = getGameById(gameSessionId);
        GameMode gameMode = gameSession.getGameMode();
        int handSize = 5;

        PlayerGameSession playerGameSession = gameSession.getPlayers().stream()
                .filter(pgs -> pgs.getPlayer().getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Player not found"));

        boolean isMultiPlayer = gameSession.getPlayers().size() > 1;
        boolean isSolitary = gameSession.getGameMode() == GameMode.SOLITARY_PUZZLE
                || gameSession.getGameMode() == GameMode.SOLITAIRE;

        if (isMultiPlayer && !isSolitary) {
            if (!gameSession.getGamePlayerTurnId().equals(playerGameSession.getId())) {
                throw new BadRequestException("It's not your turn");
            }
        }

        int baseMaxCards = (gameSession.getRound() <= 1) ? maxCardRound0 : maxCardRoundN;
        int maxCardsThisRound = playerGameSession.getEnergyCardsToPlaceOverride() != null
                ? playerGameSession.getEnergyCardsToPlaceOverride()
                : baseMaxCards;

        if (playerGameSession.getCardsPlacedThisRound() >= maxCardsThisRound) {
            throw new BadRequestException("You have already placed the maximum number of cards this round");
        }

        List<PlacedCard> myPlacedCards = placedCardRepository
                .findByGameSessionIdAndPlacedByIdOrderByPlacedAtDesc(gameSession.getId(), playerGameSession.getId());

        boolean wantsPenultimate = Boolean.TRUE.equals(playerGameSession.getEnergyAllowPenultimateStart())
                && (playerGameSession.getCardsPlacedThisRound() == null
                        || playerGameSession.getCardsPlacedThisRound() == 0);

        boolean wantsJump = playerGameSession.getActiveEnergyEffect() == EnergyEffectType.JUMP_LINE
                && gameSession.getGameMode() == GameMode.TEAMBATTLE;

        if (!myPlacedCards.isEmpty()) {
            if (wantsPenultimate && myPlacedCards.size() < 2) {
                throw new BadRequestException("No penultimate card to use Back Move.");
            }

            PlacedCard anchor = wantsPenultimate ? myPlacedCards.get(1) : myPlacedCards.get(0);
            boolean isValidPosition = false;
            int boardSize = gameSession.getBoardSize();
            Set<Orientation> exits = getRotatedExits(anchor.getTemplate(), anchor.getOrientation());

            for (Orientation exit : exits) {
                int[] delta = getDelta(exit);
                int neighborRow = Math.floorMod(anchor.getRow() + delta[0], boardSize);
                int neighborCol = Math.floorMod(anchor.getCol() + delta[1], boardSize);

                if (neighborRow == request.getRow() && neighborCol == request.getCol()) {
                    isValidPosition = true;
                    break;
                }

                if (wantsJump && placedCardRepository.existsByRowAndColAndGameSessionId(neighborRow, neighborCol,
                        gameSession.getId())) {
                    PlacedCard middle = placedCardRepository
                            .findByRowAndColAndGameSessionId(neighborRow, neighborCol, gameSession.getId())
                            .orElse(null);
                    
                    if (middle != null
                            && !middle.getPlacedBy().getId().equals(playerGameSession.getId())
                            && middle.getPlacedBy().getTeamNumber() != null
                            && middle.getPlacedBy().getTeamNumber().equals(playerGameSession.getTeamNumber())) {
                        int jumpRow = Math.floorMod(anchor.getRow() + 2 * delta[0], boardSize);
                        int jumpCol = Math.floorMod(anchor.getCol() + 2 * delta[1], boardSize);
                        
                        if (jumpRow == request.getRow() && jumpCol == request.getCol()) {
                            isValidPosition = true;
                            break;
                        }
                    }
                }
            }


            if (!isValidPosition) {
                String msg = wantsPenultimate
                        ? "Invalid Position: You can only place cards on the exits of your penultimate card."
                        : "Invalid Position: You can only place cards on the exits of your last card.";
                throw new BadRequestException(msg);
            }



            if (wantsPenultimate) {
                playerGameSession.setEnergyAllowPenultimateStart(false);
            }
        } else if (!hasAdjacentCard(gameSession, request.getRow(), request.getCol())) {
            throw new BadRequestException("Invalid Position: The card must be adjacent to an existing one.");
        }

        if (placedCardRepository.existsByRowAndColAndGameSessionId(request.getRow(), request.getCol(),
                gameSession.getId())) {
            throw new BadRequestException("Invalid Position: The cell is already occupied.");
        }

        PlacedCard newPlacedCard = placedCardService.placeCard(gameSession, playerGameSession, request, wantsJump);
        playerGameSession.setCardsPlacedThisRound(playerGameSession.getCardsPlacedThisRound() + 1);

        boolean playerEliminatedSelf = false;

        if (playerGameSession.getCardsPlacedThisRound() < maxCardsThisRound) {
            if (!canPlayerMove(gameSession, playerGameSession)) {
                eliminatePlayerRound(gameSession, playerGameSession);
                playerEliminatedSelf = true;
            }
        }

        if (playerEliminatedSelf || playerGameSession.getCardsPlacedThisRound() >= maxCardsThisRound) {

            if (!playerEliminatedSelf) {
                if (gameMode != GameMode.SOLITAIRE) {
                    replenishPlayerHand(playerGameSession, handSize);
                }
                playerGameSession.setCardsPlacedThisRound(0);
                clearEnergyEffect(playerGameSession);
            }

            if (isSolitary) {
                if (gameSession.getState() == GameState.ACTIVE && !playerEliminatedSelf) {
                    gameSession.setRound(gameSession.getRound() + 1);
                    gameSession.setGamePlayerTurnId(playerGameSession.getId());
                }
            } else {
                boolean nextFound = false;
                PlayerGameSession pivotPlayer = playerGameSession;
                while (!nextFound && gameSession.getState() == GameState.ACTIVE) {
                    Integer nextPlayerId = calculateNextPlayerId(gameSession, pivotPlayer);

                    if (nextPlayerId == null || gameSession.getState() == GameState.FINISHED)
                        break;

                    Integer firstPlayerId = getFirstPlayerIdInRound(gameSession);

                    if (nextPlayerId.equals(firstPlayerId)) {
                        gameSession.setRound(gameSession.getRound() + 1);
                        recalculateTurnOrderForNextRound(gameSession);
                        Integer newPlayerZeroId = gameSession.getPlayers().stream()
                                .filter(p -> p.getTurnOrder() != null && p.getTurnOrder() == 0)
                                .findFirst()
                                .map(PlayerGameSession::getId)
                                .orElseThrow();

                        nextPlayerId = newPlayerZeroId;
                    }
                    final Integer targetId = nextPlayerId;
                    PlayerGameSession nextPlayer = gameSession.getPlayers().stream()
                            .filter(p -> p.getId().equals(targetId))
                            .findFirst()
                            .orElseThrow();

                    if (canPlayerMove(gameSession, nextPlayer)) {
                        gameSession.setGamePlayerTurnId(targetId);
                        nextFound = true;
                    } else {
                        eliminatePlayerRound(gameSession, nextPlayer);
                        pivotPlayer = nextPlayer;
                    }
                }
            }

        }

        repo.save(gameSession);
        playerGameSessionRepository.save(playerGameSession);

        if (isSolitary) {
            long cardsOnBoard = placedCardRepository.countByGameSessionId(gameSession.getId());
            long maxCells = (long) gameSession.getBoardSize() * gameSession.getBoardSize();

            boolean noMoves = !canPlayerMoveSolitaire(gameSession, playerGameSession,
                    playerCardRepository.findByPlayer(playerGameSession).stream()
                            .filter(c -> c.getLocation() == CardState.HAND && !c.getUsed()).toList());

            if (cardsOnBoard >= maxCells) {
                finishGame(gameSession.getId(), username);
            } else if (noMoves) {
                finishGameWithoutWinner(gameSession, playerGameSession);
            }
        }
        return newPlacedCard;
    }

    private int computeScore(PlayerGameSession pgs) {
        int energyLeft = pgs.getEnergy();
        int handPoints = pgs.getCards() == null ? 0
                : pgs.getCards().stream()
                        .filter(c -> c.getLocation() == CardState.HAND)
                        .mapToInt(card -> card.getTemplate().getInitiative() != 0 ? card.getTemplate().getInitiative()
                                : 0)
                        .sum();
        int deckPoints = pgs.getCards() == null ? 0
                : pgs.getCards().stream()
                        .filter(c -> c.getLocation() == CardState.DECK)
                        .mapToInt(card -> card.getTemplate().getInitiative() != 0 ? card.getTemplate().getInitiative()
                                : 0)
                        .sum();
        return energyLeft + handPoints + deckPoints;
    }

    private GameSession finishGameWithoutWinner(GameSession game, PlayerGameSession player) {
        if (game.getState() != GameState.ACTIVE)
            return game;

        game.setState(GameState.FINISHED);
        game.setEndTime(LocalDateTime.now());
        if (game.getStartTime() != null) {
            Duration duration = Duration.between(game.getStartTime(), game.getEndTime());
            game.setDuration(duration.getSeconds());
        }
        GameSession saved = repo.save(game);
        statisticsUpdateService.updateAfterGame(saved);
        return saved;
    }

    private boolean hasAdjacentCard(GameSession game, int targetRow, int targetCol) {
        int boardSize = game.getBoardSize();
        if (placedCardRepository.findByGameSessionId(game.getId()).isEmpty()) {
            return true;
        }

        int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };

        for (int[] dir : directions) {
            int neighborRow = Math.floorMod(targetRow + dir[0], boardSize);
            int neighborCol = Math.floorMod(targetCol + dir[1], boardSize);

            if (placedCardRepository.existsByRowAndColAndGameSessionId(neighborRow, neighborCol, game.getId())) {
                return true;
            }
        }

        return false;
    }

    private void clearEnergyEffect(PlayerGameSession pgs) {
        pgs.setActiveEnergyEffect(null);
        pgs.setEnergyCardsToPlaceOverride(null);
        pgs.setEnergyAllowPenultimateStart(false);
        pgs.setEnergyPendingExtraDraw(false);
    }

    private Integer getFirstPlayerIdInRound(GameSession gameSession) {
        return gameSession.getPlayers().stream()
                .filter(p -> p.getTurnOrder() != null)
                .sorted(Comparator.comparingInt(PlayerGameSession::getTurnOrder))
                .map(PlayerGameSession::getId)
                .findFirst()
                .orElse(null);
    }

    @Transactional
    private void placeStartCards(GameSession game) {
        int playerCount = game.getPlayers().size();
        List<StartPositionDTO> positions = getStartPositions(playerCount);

        CardTemplate startTemplate = cardTemplateRepository.findAll().stream()
                .filter(t -> t.getType() == CardType.START && t.getImageId() == 999)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("CardTemplate START not found"));

        List<PlayerGameSession> sortedPlayers = game.getPlayers().stream()
                .sorted(Comparator.comparing(PlayerGameSession::getId))
                .toList();

        for (int i = 0; i < Math.min(positions.size(), sortedPlayers.size()); i++) {
            StartPositionDTO pos = positions.get(i);
            PlayerGameSession player = sortedPlayers.get(i);

            PlacedCard startCard = new PlacedCard();
            startCard.setGameSession(game);
            startCard.setTemplate(startTemplate);
            startCard.setPlacedBy(player);
            startCard.setRow(pos.row);
            startCard.setCol(pos.col);
            startCard.setOrientation(pos.orientation);
            startCard.setPlacedAt(LocalDateTime.now());

            placedCardService.save(startCard);
        }
    }

    @Transactional(readOnly = true)
    public GameSession getGameByIdWithBoard(Integer id) {
        return repo.findByIdWithPlayersAndPlacedCards(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public SpectatorGameDTO getSpectatorView(Integer id) {
        GameSession game = getGameByIdWithBoard(id);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        }
        return new SpectatorGameDTO(game);
    }

    private void replenishPlayerHand(PlayerGameSession pgs, int handSize) {
        List<PlayerCard> allMyCards = playerCardRepository.findByPlayer(pgs);

        long cardsInHand = allMyCards.stream()
                .filter(c -> c.getLocation() == CardState.HAND && !c.getUsed())
                .count();

        int cardsNeeded = handSize - (int) cardsInHand;

        if (cardsNeeded > 0) {
            List<PlayerCard> cardsToDraw = allMyCards.stream()
                    .filter(c -> c.getLocation() == CardState.DECK && !c.getUsed())
                    .sorted(Comparator.comparingInt(PlayerCard::getDeckOrder))
                    .limit(cardsNeeded)
                    .collect(Collectors.toList());

            for (PlayerCard card : cardsToDraw) {
                card.setLocation(CardState.HAND);
                playerCardRepository.save(card);
            }
        }
    }

    private Integer calculateNextPlayerId(GameSession gameSession, PlayerGameSession currentPlayer) {
        if (currentPlayer.getTurnOrder() == null) {
            return calculateNextPlayerIdFallback(gameSession, currentPlayer);
        }

        List<PlayerGameSession> activePlayers = gameSession.getPlayers().stream()
                .filter(p -> p.getTurnOrder() != null)
                .sorted(Comparator.comparingInt(PlayerGameSession::getTurnOrder))
                .collect(Collectors.toList());

        if (activePlayers.isEmpty())
            return null;

        int currentIndex = -1;
        for (int i = 0; i < activePlayers.size(); i++) {
            if (activePlayers.get(i).getId().equals(currentPlayer.getId())) {
                currentIndex = i;
                break;
            }
        }

        if (currentIndex == -1) {
            return activePlayers.get(0).getId();
        }

        int nextIndex = (currentIndex + 1) % activePlayers.size();
        return activePlayers.get(nextIndex).getId();
    }

    private void recalculateTurnOrderForNextRound(GameSession game) {
        Map<Integer, Integer> playerInitiatives = new java.util.HashMap<>();

        for (PlayerGameSession pgs : game.getPlayers()) {
            List<PlacedCard> myCards = placedCardRepository
                    .findByGameSessionIdAndPlacedByIdOrderByPlacedAtDesc(game.getId(), pgs.getId());

            if (!myCards.isEmpty()) {
                int initiative = myCards.get(0).getTemplate().getInitiative();
                playerInitiatives.put(pgs.getId(), initiative);
            } else {
                playerInitiatives.put(pgs.getId(), 999);
            }
        }

        List<PlayerGameSession> sortedPlayers = game.getPlayers().stream()
                .filter(p -> p.getTurnOrder() != null)
                .sorted((p1, p2) -> {
                    int init1 = playerInitiatives.get(p1.getId());
                    int init2 = playerInitiatives.get(p2.getId());
                    if (init1 != init2) {
                        return Integer.compare(init1, init2);
                    }
                    return Integer.compare(p1.getId(), p2.getId());
                })
                .collect(Collectors.toList());

        for (int i = 0; i < sortedPlayers.size(); i++) {
            PlayerGameSession pgs = sortedPlayers.get(i);
            pgs.setTurnOrder(i);
            playerGameSessionRepository.save(pgs);
        }
    }

    private Integer calculateNextPlayerIdFallback(GameSession gameSession, PlayerGameSession currentPlayer) {
        List<PlayerGameSession> playersList = gameSession.getPlayers().stream()
                .sorted(Comparator.comparing(PlayerGameSession::getId))
                .toList();
        int currentIndex = playersList.indexOf(currentPlayer);
        if (currentIndex == -1)
            return null;
        int nextIndex = (currentIndex + 1) % playersList.size();
        return playersList.get(nextIndex).getId();
    }

    private void eliminatePlayerRound(GameSession gameSession, PlayerGameSession player) {
        player.setTurnOrder(null);
        playerGameSessionRepository.save(player);

        List<PlayerGameSession> activePlayers = gameSession.getPlayers().stream()
                .filter(p -> p.getTurnOrder() != null)
                .toList();

        long activePlayersCount = activePlayers.size();

        if (activePlayersCount == 1) {
            PlayerGameSession winner = activePlayers.get(0);
            finishGame(gameSession.getId(), winner.getPlayer().getUsername());
            gameSession.setState(GameState.FINISHED);
        } else if (gameSession.getGameMode() == GameMode.TEAMBATTLE && activePlayersCount > 1) {
            Set<Integer> activeTeams = activePlayers.stream()
                    .map(PlayerGameSession::getTeamNumber)
                    .filter(team -> team != null)
                    .collect(Collectors.toSet());

            if (activeTeams.size() == 1) {
                PlayerGameSession teamRepresentative = activePlayers.get(0);
                finishGame(gameSession.getId(), teamRepresentative.getPlayer().getUsername());
                gameSession.setState(GameState.FINISHED);
            }
        }
    }

    private List<StartPositionDTO> getStartPositions(int playerCount) {
        return switch (playerCount) {
            case 1 -> List.of(new StartPositionDTO(2, 2, "N"));
            case 2 -> List.of(new StartPositionDTO(3, 2, "N"), new StartPositionDTO(3, 4, "N"));
            case 3 -> List.of(new StartPositionDTO(2, 3, "N"), new StartPositionDTO(3, 2, "W"),
                    new StartPositionDTO(3, 4, "E"));
            case 4 -> List.of(new StartPositionDTO(3, 4, "N"), new StartPositionDTO(4, 3, "W"),
                    new StartPositionDTO(5, 4, "S"), new StartPositionDTO(4, 5, "E"));
            case 5 -> List.of(new StartPositionDTO(3, 3, "N"), new StartPositionDTO(3, 5, "N"),
                    new StartPositionDTO(5, 3, "S"), new StartPositionDTO(5, 5, "S"), new StartPositionDTO(4, 6, "E"));
            case 6 -> List.of(new StartPositionDTO(4, 4, "N"), new StartPositionDTO(4, 6, "N"),
                    new StartPositionDTO(6, 4, "S"), new StartPositionDTO(6, 6, "S"), new StartPositionDTO(5, 7, "E"),
                    new StartPositionDTO(5, 3, "W"));
            case 7 -> List.of(new StartPositionDTO(3, 4, "N"), new StartPositionDTO(3, 6, "N"),
                    new StartPositionDTO(5, 4, "W"), new StartPositionDTO(7, 4, "S"), new StartPositionDTO(7, 6, "S"),
                    new StartPositionDTO(4, 7, "E"), new StartPositionDTO(6, 7, "E"));
            case 8 -> List.of(new StartPositionDTO(4, 5, "N"), new StartPositionDTO(4, 7, "N"),
                    new StartPositionDTO(5, 4, "W"), new StartPositionDTO(7, 4, "W"), new StartPositionDTO(8, 5, "S"),
                    new StartPositionDTO(8, 7, "S"), new StartPositionDTO(5, 8, "E"), new StartPositionDTO(7, 8, "E"));
            default -> List.of();
        };
    }

    public boolean canPlayerMove(GameSession game, PlayerGameSession player) {
        List<PlayerCard> hand = playerCardRepository.findByPlayer(player).stream()
                .filter(c -> c.getLocation() == CardState.HAND && !c.getUsed())
                .toList();
        if (hand.isEmpty()) {
            if (game.getGameMode() == GameMode.SOLITAIRE) {
                return true;
            }
            return false;
        }

        List<PlacedCard> myPlacedCards = placedCardRepository
                .findByGameSessionIdAndPlacedByIdOrderByPlacedAtDesc(game.getId(), player.getId());
        if (myPlacedCards.isEmpty())
            return true;

        int boardSize = game.getBoardSize();
        PlacedCard lastCard = myPlacedCards.get(0);

        Set<Orientation> exits = getRotatedExits(lastCard.getTemplate(), lastCard.getOrientation());

        for (Orientation exitDir : exits) {
            int[] delta = getDelta(exitDir);
            int neighborRow = Math.floorMod(lastCard.getRow() + delta[0], boardSize);
            int neighborCol = Math.floorMod(lastCard.getCol() + delta[1], boardSize);

            if (placedCardRepository.existsByRowAndColAndGameSessionId(neighborRow, neighborCol, game.getId())) {
                continue;
            }
            Orientation requiredEntrance = getOpposite(exitDir);
            for (PlayerCard handCard : hand) {
                if (canCardConnect(handCard.getTemplate(), requiredEntrance)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean canPlayerMoveSolitaire(GameSession game, PlayerGameSession player, List<PlayerCard> hand) {
        List<PlayerCard> deck = playerCardRepository.findByPlayer(player).stream()
                .filter(c -> c.getLocation() == CardState.DECK && !c.getUsed())
                .sorted(Comparator.comparingInt(PlayerCard::getDeckOrder))
                .toList();

        List<PlayerCard> candidates = new ArrayList<>();
        candidates.addAll(hand);
        candidates.addAll(deck);

        if (candidates.isEmpty())
            return false;

        List<PlacedCard> myPlacedCards = placedCardRepository
                .findByGameSessionIdAndPlacedByIdOrderByPlacedAtDesc(game.getId(), player.getId());
        if (myPlacedCards.isEmpty())
            return true;

        int boardSize = game.getBoardSize();
        PlacedCard lastCard = myPlacedCards.get(0);
        Set<Orientation> exits = getRotatedExits(lastCard.getTemplate(), lastCard.getOrientation());

        for (Orientation exitDir : exits) {
            int[] delta = getDelta(exitDir);
            int neighborRow = Math.floorMod(lastCard.getRow() + delta[0], boardSize);
            int neighborCol = Math.floorMod(lastCard.getCol() + delta[1], boardSize);

            if (placedCardRepository.existsByRowAndColAndGameSessionId(neighborRow, neighborCol, game.getId())) {
                continue;
            }
            Orientation requiredEntrance = getOpposite(exitDir);
            for (PlayerCard card : candidates) {
                if (canCardConnect(card.getTemplate(), requiredEntrance)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean canCardConnect(CardTemplate template, Orientation requiredEntrance) {
        Orientation defaultEnt = template.getDefaultEntrance();
        if (defaultEnt == requiredEntrance)
            return true;
        if (rotate(defaultEnt) == requiredEntrance)
            return true;
        if (rotate(rotate(defaultEnt)) == requiredEntrance)
            return true;
        if (rotate(rotate(rotate(defaultEnt))) == requiredEntrance)
            return true;
        return false;
    }

    private Set<Orientation> getRotatedExits(CardTemplate template, Orientation rotation) {
        Set<Orientation> rotated = new HashSet<>();
        int rotations = switch (rotation) {
            case N -> 0;
            case E -> 1;
            case S -> 2;
            case W -> 3;
        };
        for (Orientation exit : template.getDefaultExits()) {
            Orientation current = exit;
            for (int i = 0; i < rotations; i++)
                current = rotate(current);
            rotated.add(current);
        }
        return rotated;
    }

    private Orientation rotate(Orientation o) {
        return switch (o) {
            case N -> Orientation.E;
            case E -> Orientation.S;
            case S -> Orientation.W;
            case W -> Orientation.N;
        };
    }

    private Orientation getOpposite(Orientation o) {
        return switch (o) {
            case N -> Orientation.S;
            case S -> Orientation.N;
            case E -> Orientation.W;
            case W -> Orientation.E;
        };
    }

    private int[] getDelta(Orientation o) {
        return switch (o) {
            case N -> new int[] { -1, 0 };
            case S -> new int[] { 1, 0 };
            case E -> new int[] { 0, 1 };
            case W -> new int[] { 0, -1 };
        };
    }

    @Transactional(readOnly = true)
    public List<GameSessionDTO> getActiveGamesByFriends(Set<String> friendUsernames) {
        return repo.findByState(GameState.ACTIVE).stream()
                .filter(gs -> gs.getPlayers().stream()
                        .map(pgs -> pgs.getPlayer().getUsername())
                        .allMatch(friendUsernames::contains))
                .map(GameSessionDTO::new)
                .collect(Collectors.toList());
    }
}
