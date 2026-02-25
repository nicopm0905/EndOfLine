package es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardState;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardTemplate;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Color;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.EnergyEffectType;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Orientation;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.playerCard.PlayerCard;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.playerCard.PlayerCardRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.BadRequestException;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameMode;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSessionRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameState;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import jakarta.persistence.EntityManager;

@ExtendWith(MockitoExtension.class)
class PlayerGameSessionServiceTests {

    @Mock
    private PlayerGameSessionRepository repository;

    @Mock
    private PlayerCardRepository playerCardRepository;

    @Mock
    private GameSessionRepository gameSessionRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private PlayerGameSessionService service;

    private PlayerGameSession pgs;
    private GameSession game;
    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player();
        player.setUsername("testUser");

        game = new GameSession();
        game.setId(1);
        game.setRound(3);
        game.setGameMode(GameMode.VERSUS);
        game.setPlayers(new HashSet<>());

        pgs = new PlayerGameSession();
        pgs.setId(1);
        pgs.setPlayer(player);
        pgs.setGameSession(game);
        pgs.setEnergy(3);
    }

    @Test
    void consumeEnergy_ShouldApplyBoostEffect() {
        when(repository.findByGameSessionIdAndPlayerUsername(1, "testUser")).thenReturn(Optional.of(pgs));
        when(repository.save(any(PlayerGameSession.class))).thenAnswer(i -> i.getArguments()[0]);

        PlayerGameSession result = service.consumeEnergy(1, "testUser", "BOOST");

        assertEquals(2, result.getEnergy());
        assertEquals(EnergyEffectType.BOOST, result.getActiveEnergyEffect());
        assertEquals(3, result.getEnergyCardsToPlaceOverride());
        assertEquals(3, result.getLastEnergyRoundUsed());
    }

    @Test
    void consumeEnergy_ShouldThrowException_WhenNoEnergy() {
        pgs.setEnergy(0);
        when(repository.findByGameSessionIdAndPlayerUsername(1, "testUser")).thenReturn(Optional.of(pgs));

        assertThrows(ResponseStatusException.class, () -> service.consumeEnergy(1, "testUser", "BOOST"));
    }

    @Test
    void consumeEnergy_ShouldThrowException_IfAlreadyPlacedCards() {
        pgs.setCardsPlacedThisRound(1);
        when(repository.findByGameSessionIdAndPlayerUsername(1, "testUser")).thenReturn(Optional.of(pgs));

        assertThrows(ResponseStatusException.class, () -> service.consumeEnergy(1, "testUser", "BOOST"));
    }

    @Test
    void consumeEnergy_ShouldThrowException_BeforeRound3() {
        game.setRound(2);
        when(repository.findByGameSessionIdAndPlayerUsername(1, "testUser")).thenReturn(Optional.of(pgs));

        assertThrows(ResponseStatusException.class, () -> service.consumeEnergy(1, "testUser", "BOOST"));
    }

    @Test
    void consumeEnergy_ShouldThrowException_InvalidAction() {
        when(repository.findByGameSessionIdAndPlayerUsername(1, "testUser")).thenReturn(Optional.of(pgs));

        assertThrows(ResponseStatusException.class, () -> service.consumeEnergy(1, "testUser", "NOT_VALID"));
    }

    @Test
    void consumeEnergy_ShouldThrowException_JumpLineNotTeamBattle() {
        when(repository.findByGameSessionIdAndPlayerUsername(1, "testUser")).thenReturn(Optional.of(pgs));

        assertThrows(ResponseStatusException.class, () -> service.consumeEnergy(1, "testUser", "JUMP_LINE"));
    }

    @Test
    void playerHasActiveGame_ShouldUseRepository() {
        player.setId(99);
        when(repository.existsActiveGameForPlayer(eq(99), any())).thenReturn(true);

        boolean result = service.playerHasActiveGame(player);

        assertEquals(true, result);
    }

    @Test
    void createPlayerForGame_ShouldAttachPlayer() {
        PlayerGameSession created = service.createPlayerForGame(game, player, Color.BLUE);

        assertEquals(player, created.getPlayer());
        assertEquals(game, created.getGameSession());
        assertEquals(Color.BLUE, created.getPlayerColor());
        assertEquals(3, created.getEnergy());
    }

    @Test
    void drawFirstCardForInitiative_ShouldReturnExisting() {
        CardTemplate template = new CardTemplate();
        template.setId(10);
        pgs.setInitiativeCard(template);
        when(repository.findByGameSessionIdAndPlayerUsername(1, "testUser")).thenReturn(Optional.of(pgs));

        CardTemplate result = service.drawFirstCardForInitiative(1, "testUser");

        assertEquals(10, result.getId());
    }

    @Test
    void drawFirstCardForInitiative_ShouldPickFromDeck() {
        CardTemplate template = new CardTemplate();
        template.setId(1);
        template.setType(null);
        template.setDefaultEntrance(Orientation.N);
        template.setDefaultExits(new HashSet<>());
        PlayerCard card = new PlayerCard();
        card.setTemplate(template);
        card.setLocation(CardState.DECK);
        pgs.getCards().add(card);
        when(repository.findByGameSessionIdAndPlayerUsername(1, "testUser")).thenReturn(Optional.of(pgs));
        when(repository.save(any(PlayerGameSession.class))).thenReturn(pgs);

        CardTemplate result = service.drawFirstCardForInitiative(1, "testUser");

        assertNotNull(result);
        verify(repository).save(pgs);
    }

    @Test
    void discardActiveCard_ShouldMoveToDiscardAndDraw() {
        CardTemplate template = new CardTemplate();
        template.setId(1);
        PlayerCard card = new PlayerCard();
        card.setId(5);
        card.setLocation(CardState.HAND);
        card.setTemplate(template);
        card.setPlayer(pgs);

        when(playerCardRepository.findById(5)).thenReturn(Optional.of(card));
        when(playerCardRepository.findFirstByPlayerAndLocationOrderByDeckOrderAsc(pgs, CardState.DECK))
                .thenReturn(Optional.empty());

        service.discardActiveCard(1, "testUser", 5);

        assertEquals(CardState.DISCARD, card.getLocation());
        verify(playerCardRepository).save(card);
        verify(repository).save(pgs);
    }

    @Test
    void drawCardFromDeckAction_ShouldDrawWhenSolitaireAndEmptyHand() {
        game.setGameMode(GameMode.SOLITAIRE);
        game.getPlayers().add(pgs);

        PlayerCard deckCard = new PlayerCard();
        deckCard.setLocation(CardState.DECK);
        when(gameSessionRepository.findById(1)).thenReturn(Optional.of(game));
        when(playerCardRepository.countByPlayerAndLocationAndUsedFalse(pgs, CardState.HAND)).thenReturn(0L);
        when(playerCardRepository.findFirstByPlayerAndLocationOrderByDeckOrderAsc(pgs, CardState.DECK))
                .thenReturn(Optional.of(deckCard));

        service.drawCardFromDeckAction(1, "testUser");

        assertEquals(CardState.HAND, deckCard.getLocation());
        verify(playerCardRepository).save(deckCard);
    }

    @Test
    void drawCardFromDiscardAction_ShouldRestoreFromDiscard() {
        game.setGameMode(GameMode.SOLITAIRE);
        game.getPlayers().add(pgs);
        CardTemplate template = new CardTemplate();
        template.setId(3);
        pgs.getDiscardPile().add(template);

        PlayerCard discarded = new PlayerCard();
        discarded.setTemplate(template);
        discarded.setLocation(CardState.DISCARD);

        when(gameSessionRepository.findById(1)).thenReturn(Optional.of(game));
        when(playerCardRepository.countByPlayerAndLocationAndUsedFalse(pgs, CardState.HAND)).thenReturn(0L);
        when(playerCardRepository.findByPlayer(pgs)).thenReturn(List.of(discarded));

        service.drawCardFromDiscardAction(1, "testUser");

        assertEquals(CardState.HAND, discarded.getLocation());
        verify(playerCardRepository).save(discarded);
        verify(repository).save(pgs);
    }

    @Test
    void drawCardFromDeckAction_ShouldThrowException_IfHandNotEmpty() {
        when(gameSessionRepository.findById(1)).thenReturn(Optional.of(game));
        game.getPlayers().add(pgs);
        game.setGameMode(GameMode.SOLITAIRE);

        when(playerCardRepository.countByPlayerAndLocationAndUsedFalse(pgs, CardState.HAND)).thenReturn(1L);

        assertThrows(BadRequestException.class, () -> service.drawCardFromDeckAction(1, "testUser"));
    }

    @Test
    void switchTeam_ShouldSwitchTeamSuccessfully() {
        game.setGameMode(GameMode.TEAMBATTLE);
        game.setState(GameState.PENDING);
        game.setNumPlayers(4);
        pgs.setTeamNumber(1);
        game.getPlayers().add(pgs);

        when(gameSessionRepository.findById(1)).thenReturn(Optional.of(game));

        service.switchTeam(1, "testUser", 2);

        assertEquals(2, pgs.getTeamNumber());
        verify(repository).save(pgs);
    }

    @Test
    void switchTeam_ShouldThrowException_IfGameNotPending() {
        game.setGameMode(GameMode.TEAMBATTLE);
        game.setState(GameState.ACTIVE);
        when(gameSessionRepository.findById(1)).thenReturn(Optional.of(game));

        assertThrows(BadRequestException.class, () -> service.switchTeam(1, "testUser", 2));
    }
}
