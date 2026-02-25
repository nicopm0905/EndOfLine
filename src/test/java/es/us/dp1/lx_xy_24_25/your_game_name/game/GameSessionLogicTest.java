package es.us.dp1.lx_xy_24_25.your_game_name.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardState;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardTemplate;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardTemplateRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardType;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Color;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Orientation;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.placedCard.PlacedCard;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.placedCard.PlacedCardRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.placedCard.PlacedCardService;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.playerCard.PlayerCard;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.playerCard.PlayerCardRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.GameStartDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.PlaceCardRequestDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameMode;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSessionRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSessionService;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameState;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.PlayerGameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.PlayerGameSessionRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.PlayerGameSessionService;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerService;
import es.us.dp1.lx_xy_24_25.your_game_name.social.chatMessage.ChatMessageRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.playerStatistics.StatisticsUpdateService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserRepository;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class GameSessionLogicTest {

    @Mock
    private GameSessionRepository gameSessionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PlayerService playerService;
    @Mock
    private PlayerCardRepository playerCardRepository;
    @Mock
    private CardTemplateRepository cardTemplateRepository;
    @Mock
    private ChatMessageRepository chatMessageRepository;
    @Mock
    private StatisticsUpdateService statisticsUpdateService;
    @Mock
    private PlacedCardService placedCardService;
    @Mock
    private PlacedCardRepository placedCardRepository;
    @Mock
    private PlayerGameSessionRepository playerGameSessionRepository;
    @Mock
    private PlayerGameSessionService playerGameSessionService;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private GameSessionService gameSessionService;

    private GameSession gameSession;
    private Player player1;
    private Player player2;
    private PlayerGameSession pgs1;
    private PlayerGameSession pgs2;
    private User user1;
    private CardTemplate startTemplate;
    private CardTemplate lineTemplate;

    @BeforeEach
    void setUp() {
        player1 = new Player();
        player1.setId(1);
        player1.setUsername("player1");

        player2 = new Player();
        player2.setId(2);
        player2.setUsername("player2");

        user1 = new User();
        user1.setUsername("player1");

        gameSession = new GameSession();
        gameSession.setId(1);
        gameSession.setGameMode(GameMode.VERSUS);
        gameSession.setState(GameState.PENDING);
        gameSession.setPlayers(new java.util.HashSet<>());

        pgs1 = new PlayerGameSession();
        pgs1.setId(1);
        pgs1.setPlayer(player1);
        pgs1.setGameSession(gameSession);

        pgs2 = new PlayerGameSession();
        pgs2.setId(2);
        pgs2.setPlayer(player2);
        pgs2.setGameSession(gameSession);

        gameSession.getPlayers().add(pgs1);
        gameSession.getPlayers().add(pgs2);

        startTemplate = new CardTemplate();
        startTemplate.setId(999);
        startTemplate.setImageId(999);
        startTemplate.setType(CardType.START);

        lineTemplate = new CardTemplate();
        lineTemplate.setId(1);
        lineTemplate.setImageId(1);
        lineTemplate.setType(CardType.LINE);
    }

    @Test
    void shouldStartGameSuccessfully() {
        when(gameSessionRepository.findByIdWithPlayers(1)).thenReturn(Optional.of(gameSession));

        List<CardTemplate> templates = new ArrayList<>();
        templates.add(startTemplate);
        for (int i = 0; i < 10; i++) {
            CardTemplate t = new CardTemplate();
            t.setId(i + 10);
            t.setImageId(i + 10);
            t.setType(CardType.LINE);
            templates.add(t);
        }

        when(cardTemplateRepository.findAll()).thenReturn(templates);

        PlayerCard initCard1 = new PlayerCard();
        initCard1.setTemplate(new CardTemplate());
        initCard1.getTemplate().setInitiative(10);
        pgs1.setInitiativeCard(initCard1.getTemplate());

        PlayerCard initCard2 = new PlayerCard();
        initCard2.setTemplate(new CardTemplate());
        initCard2.getTemplate().setInitiative(5);
        pgs2.setInitiativeCard(initCard2.getTemplate());

        GameStartDTO result = gameSessionService.startGame(1, user1, gameSession);

        assertNotNull(result);
        assertEquals(GameState.ACTIVE, gameSession.getState());
        assertNotNull(gameSession.getStartTime());
        assertEquals(1, gameSession.getRound());

        verify(playerCardRepository, atLeastOnce()).saveAll(anyList());

        verify(placedCardService, times(2)).save(any(PlacedCard.class));

        verify(playerGameSessionRepository).saveAll(anyList());
        assertEquals(Integer.valueOf(2), gameSession.getGamePlayerTurnId());
    }

    @Test
    void shouldPlaceCardSuccessfully() {
        gameSession.setState(GameState.ACTIVE);
        gameSession.setGamePlayerTurnId(1);
        gameSession.setBoardSize(5);
        gameSession.setRound(1);

        PlaceCardRequestDTO request = new PlaceCardRequestDTO();
        request.setRow(2);
        request.setCol(2);
        request.setOrientation(Orientation.N);
        request.setPlayerCardId(100);

        PlayerCard handCard = new PlayerCard();
        handCard.setId(100);
        handCard.setLocation(CardState.HAND);
        handCard.setTemplate(lineTemplate);
        handCard.setUsed(false);

        when(gameSessionRepository.findByIdWithPlayers(1)).thenReturn(Optional.of(gameSession));
        when(playerCardRepository.findByPlayer(any(PlayerGameSession.class))).thenReturn(List.of(handCard));
        when(placedCardRepository.findByGameSessionIdAndPlacedByIdOrderByPlacedAtDesc(anyInt(), anyInt()))
                .thenReturn(new ArrayList<>());
        when(placedCardRepository.findByGameSessionId(anyInt())).thenReturn(new ArrayList<>());

        PlacedCard placedCard = new PlacedCard();
        placedCard.setRow(2);
        placedCard.setCol(2);
        when(placedCardService.placeCard(any(), any(), any(), anyBoolean())).thenReturn(placedCard);

        PlacedCard result = gameSessionService.handleCardPlacement(1, "player1", request);

        assertNotNull(result);
        verify(placedCardService).placeCard(eq(gameSession), eq(pgs1), eq(request), anyBoolean());
        verify(playerGameSessionRepository, atLeastOnce()).save(pgs1);
    }

    @Test
    void shouldThrowExceptionWhenNotPlayerTurn() {
        gameSession.setState(GameState.ACTIVE);
        gameSession.setGamePlayerTurnId(2);

        PlaceCardRequestDTO request = new PlaceCardRequestDTO();

        when(gameSessionRepository.findByIdWithPlayers(1)).thenReturn(Optional.of(gameSession));

        assertThrows(es.us.dp1.lx_xy_24_25.your_game_name.exceptions.BadRequestException.class, () -> {
            gameSessionService.handleCardPlacement(1, "player1", request);
        });
    }

    @Test
    void shouldThrowExceptionWhenPositionOccupied() {
        gameSession.setState(GameState.ACTIVE);
        gameSession.setGamePlayerTurnId(1);
        gameSession.setBoardSize(5);

        PlaceCardRequestDTO request = new PlaceCardRequestDTO();
        request.setRow(2);
        request.setCol(2);

        when(gameSessionRepository.findByIdWithPlayers(1)).thenReturn(Optional.of(gameSession));
        when(placedCardRepository.existsByRowAndColAndGameSessionId(2, 2, 1)).thenReturn(true);

        assertThrows(es.us.dp1.lx_xy_24_25.your_game_name.exceptions.BadRequestException.class, () -> {
            gameSessionService.handleCardPlacement(1, "player1", request);
        });
    }
}
