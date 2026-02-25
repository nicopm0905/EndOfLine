package es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSessionService;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerService;

@ExtendWith(MockitoExtension.class)
class LeaveGameServiceTests {

    @Mock
    private GameSessionService gameService;
    @Mock
    private PlayerService playerService;
    @Mock
    private PlayerGameSessionService playerGameSessionService;
    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private LeaveGameService service;

    private GameSession gameSession;
    private Player hostPlayer;
    private Player regularPlayer;
    private PlayerGameSession hostPgs;
    private PlayerGameSession regularPgs;

    @BeforeEach
    void setUp() {
        hostPlayer = new Player();
        hostPlayer.setId(1);
        hostPlayer.setUsername("host");

        regularPlayer = new Player();
        regularPlayer.setId(2);
        regularPlayer.setUsername("player");

        hostPgs = new PlayerGameSession();
        hostPgs.setId(1);
        hostPgs.setPlayer(hostPlayer);

        regularPgs = new PlayerGameSession();
        regularPgs.setId(2);
        regularPgs.setPlayer(regularPlayer);

        gameSession = new GameSession();
        gameSession.setId(1);
        gameSession.setHost("host");
        gameSession.setPlayers(new HashSet<>(Set.of(hostPgs, regularPgs)));
    }

    @Test
    void leaveGame_GameNotFound() {
        when(gameService.getGameById(1)).thenReturn(null);

        ResponseEntity<?> response = service.leaveGame(1, "host");
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Game not found", response.getBody());
    }

    @Test
    void leaveGame_HostLeaves_ShouldDeleteGame() {
        when(gameService.getGameById(1)).thenReturn(gameSession);
        when(playerService.findPlayer("host")).thenReturn(hostPlayer);

        ResponseEntity<?> response = service.leaveGame(1, "host");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Host left: game deleted and all players expelled", response.getBody());
        
        verify(gameService).delete(1);
        verify(messagingTemplate).convertAndSend(eq("/topic/lobby/1"), any(java.util.Map.class));
    }

    @Test
    void leaveGame_RegularPlayerLeaves_ShouldRemovePlayer() {
        when(gameService.getGameById(1)).thenReturn(gameSession);
        when(playerService.findPlayer("player")).thenReturn(regularPlayer);

        ResponseEntity<?> response = service.leaveGame(1, "player");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Left successfully", response.getBody());
        
        verify(playerGameSessionService).delete(regularPgs);
        verify(messagingTemplate, org.mockito.Mockito.times(2)).convertAndSend(eq("/topic/lobby/1"), any(Object.class));
    }

    @Test
    void leaveGame_PlayerNotInGame_ShouldReturnConflict() {
        Player otherPlayer = new Player();
        otherPlayer.setId(3);
        otherPlayer.setUsername("other");
        
        when(gameService.getGameById(1)).thenReturn(gameSession);
        when(playerService.findPlayer("other")).thenReturn(otherPlayer);

        ResponseEntity<?> response = service.leaveGame(1, "other");

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Player not in the game", response.getBody());
    }
}
