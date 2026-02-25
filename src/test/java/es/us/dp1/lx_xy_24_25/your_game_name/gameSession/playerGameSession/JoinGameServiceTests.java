package es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import es.us.dp1.lx_xy_24_25.your_game_name.dto.GameSessionDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.JoinRequestDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.BadRequestException;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameMode;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSessionService;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameState;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerService;

@ExtendWith(MockitoExtension.class)
class JoinGameServiceTests {

    @Mock
    private GameSessionService gameService;
    @Mock
    private PlayerService playerService;
    @Mock
    private PlayerGameSessionService playerGameSessionService;
    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private JoinGameService service;

    private GameSession gameSession;
    private Player player;
    private JoinRequestDTO joinRequest;

    @BeforeEach
    void setUp() {
        gameSession = new GameSession();
        gameSession.setId(1);
        gameSession.setState(GameState.PENDING);
        gameSession.setPlayers(new HashSet<>());
        gameSession.setNumPlayers(4);
        gameSession.setGameMode(GameMode.SOLITARY_PUZZLE);

        player = new Player();
        player.setId(2);
        player.setUsername("joiner");

        joinRequest = new JoinRequestDTO();
    }

    @Test
    void joinPlayerToGame_GameNotFound() {
        when(gameService.getGameById(1)).thenReturn(null);

        ResponseEntity<?> response = service.joinPlayerToGame(1, "joiner", joinRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Game not found", response.getBody());
    }

    @Test
    void joinPlayerToGame_NotPending() {
        gameSession.setState(GameState.ACTIVE);
        when(gameService.getGameById(1)).thenReturn(gameSession);

        ResponseEntity<?> response = service.joinPlayerToGame(1, "joiner", joinRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Game is not open for joining", response.getBody());
    }

    @Test
    void joinPlayerToGame_PlayerAlreadyJoined() {
        when(gameService.getGameById(1)).thenReturn(gameSession);
        when(playerService.findPlayer("joiner")).thenReturn(player);
        
        PlayerGameSession existingPgs = new PlayerGameSession();
        existingPgs.setPlayer(player);
        gameSession.getPlayers().add(existingPgs);

        ResponseEntity<?> response = service.joinPlayerToGame(1, "joiner", joinRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Player already joined", response.getBody());
    }

    @Test
    void joinPlayerToGame_GameFull() {
        gameSession.setNumPlayers(1);
        PlayerGameSession existingPgs = new PlayerGameSession();
        Player existingPlayer = new Player();
        existingPlayer.setId(3);
        existingPgs.setPlayer(existingPlayer);
        gameSession.getPlayers().add(existingPgs);

        when(gameService.getGameById(1)).thenReturn(gameSession);
        when(playerService.findPlayer("joiner")).thenReturn(player);

        ResponseEntity<?> response = service.joinPlayerToGame(1, "joiner", joinRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Game is full", response.getBody());
    }

    @Test
    void joinPlayerToGame_PlayerActiveInAnotherGame() {
        when(gameService.getGameById(1)).thenReturn(gameSession);
        when(playerService.findPlayer("joiner")).thenReturn(player);
        when(playerGameSessionService.playerHasActiveGame(player)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> service.joinPlayerToGame(1, "joiner", joinRequest));
    }

    @Test
    void joinPlayerToGame_PrivateMissingPassword() {
        gameSession.setPrivate(true);
        joinRequest.setPassword("");
        
        when(gameService.getGameById(1)).thenReturn(gameSession);
        when(playerService.findPlayer("joiner")).thenReturn(player);

        ResponseEntity<?> response = service.joinPlayerToGame(1, "joiner", joinRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void joinPlayerToGame_PrivateWrongPassword() {
        gameSession.setPrivate(true);
        gameSession.setPassword("secret");
        joinRequest.setPassword("wrong");

        when(gameService.getGameById(1)).thenReturn(gameSession);
        when(playerService.findPlayer("joiner")).thenReturn(player);

        ResponseEntity<?> response = service.joinPlayerToGame(1, "joiner", joinRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void joinPlayerToGame_Success() {
        when(gameService.getGameById(1)).thenReturn(gameSession);
        when(playerService.findPlayer("joiner")).thenReturn(player);
        when(playerGameSessionService.playerHasActiveGame(player)).thenReturn(false);
        
        PlayerGameSession newPgs = new PlayerGameSession();
        newPgs.setPlayer(player);
        when(playerGameSessionService.createPlayerForGame(eq(gameSession), eq(player), any())).thenReturn(newPgs);

        ResponseEntity<?> response = service.joinPlayerToGame(1, "joiner", joinRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Joined successfully", response.getBody());
        verify(playerGameSessionService).save(newPgs);
        verify(messagingTemplate).convertAndSend(eq("/topic/lobby/1"), any(GameSessionDTO.class));
    }

    @Test
    void joinPlayerToGame_TeamBattleLogic() {
        gameSession.setGameMode(GameMode.TEAMBATTLE);
        when(gameService.getGameById(1)).thenReturn(gameSession);
        when(playerService.findPlayer("joiner")).thenReturn(player);
        
        Player pObj = new Player(); pObj.setId(10);
        PlayerGameSession p1 = new PlayerGameSession(); p1.setTeamNumber(1); p1.setPlayer(pObj);
        PlayerGameSession p2 = new PlayerGameSession(); p2.setTeamNumber(1); p2.setPlayer(pObj);
        PlayerGameSession p3 = new PlayerGameSession(); p3.setTeamNumber(2); p3.setPlayer(pObj);
        gameSession.getPlayers().add(p1);
        gameSession.getPlayers().add(p2);
        gameSession.getPlayers().add(p3);
        PlayerGameSession newPgs = new PlayerGameSession();
        newPgs.setPlayer(player);
        when(playerGameSessionService.createPlayerForGame(eq(gameSession), eq(player), any())).thenReturn(newPgs);

        service.joinPlayerToGame(1, "joiner", joinRequest);

        assertEquals(2, newPgs.getTeamNumber());
    }
}
