package es.us.dp1.lx_xy_24_25.your_game_name.puzzle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardTemplate;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardTemplateRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardType;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.placedCard.PlacedCardRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameMode;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSessionService;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.PlayerGameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.PlayerGameSessionService;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;

@ExtendWith(MockitoExtension.class)
class PuzzleFactoryServiceTests {

    @Mock
    private GameSessionService gameSessionService;

    @Mock
    private PlacedCardRepository placedCardRepository;

    @Mock
    private CardTemplateRepository cardTemplateRepository;

    @Mock
    private PlayerGameSessionService playerGameSessionService;

    @InjectMocks
    private PuzzleFactoryService service;

    private Player player;
    private GameSession gameSession;
    private PlayerGameSession pgs;
    private CardTemplate startTemplate;
    private CardTemplate obstacleTemplate;

    @BeforeEach
    void setUp() {
        service.initPuzzles(); 

        player = new Player();
        player.setId(1);
        player.setUsername("testUser");

        gameSession = new GameSession();
        gameSession.setId(1);
        gameSession.setPlayers(Collections.emptySet());
        gameSession.setGameMode(GameMode.SOLITARY_PUZZLE);

        pgs = new PlayerGameSession();
        pgs.setId(1);

        startTemplate = new CardTemplate();
        startTemplate.setId(1);
        
        obstacleTemplate = new CardTemplate();
        obstacleTemplate.setId(2);
    }

    @Test
    void createPuzzleGame_ShouldCreateSuccessfully() {
        when(gameSessionService.save(any(GameSession.class))).thenReturn(gameSession);
        when(playerGameSessionService.save(any(PlayerGameSession.class))).thenReturn(pgs);
        when(gameSessionService.getGameById(1)).thenReturn(gameSession);
        when(cardTemplateRepository.findByType(CardType.START)).thenReturn(Optional.of(startTemplate));
        when(cardTemplateRepository.findByType(CardType.BACK)).thenReturn(Optional.of(obstacleTemplate));

        GameSession result = service.createPuzzleGame(1, player);

        assertNotNull(result);
        assertEquals(GameMode.SOLITARY_PUZZLE, result.getGameMode());
        verify(placedCardRepository).saveAll(anyList());
    }

    @Test
    void createPuzzleGame_ShouldThrowException_WhenPuzzleNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> service.createPuzzleGame(99, player));
    }

    @Test
    void createSolitaireGame_ShouldCreateSuccessfully() {
        when(gameSessionService.save(any(GameSession.class))).thenReturn(gameSession);
        when(playerGameSessionService.save(any(PlayerGameSession.class))).thenReturn(pgs);
        when(gameSessionService.getGameById(1)).thenReturn(gameSession);
        when(cardTemplateRepository.findByType(CardType.START)).thenReturn(Optional.of(startTemplate));
        when(cardTemplateRepository.findByType(CardType.BACK)).thenReturn(Optional.of(obstacleTemplate));

        GameSession result = service.createSolitaireGame(1, player);

        assertNotNull(result);
        verify(placedCardRepository).saveAll(anyList());
    }
}