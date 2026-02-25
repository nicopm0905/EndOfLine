package es.us.dp1.lx_xy_24_25.your_game_name.game;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.us.dp1.lx_xy_24_25.your_game_name.configuration.services.UserDetailsServiceImpl;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameMode;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSessionRestController;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSessionService;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameState;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.JoinGameService;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.LeaveGameService;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.PlayerGameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.PlayerGameSessionService;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerService;
import es.us.dp1.lx_xy_24_25.your_game_name.puzzle.PuzzleFactoryService;
import es.us.dp1.lx_xy_24_25.your_game_name.social.friendship.FriendshipService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.AuthoritiesService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import jakarta.persistence.EntityManagerFactory;

@Epic("Game Session Module")
@Feature("Game Session Controller Tests")
@Owner("DP1-tutors")
@WebMvcTest(GameSessionRestController.class)
class GameSessionControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GameSessionService gameService;
    @MockBean
    private PlayerGameSessionService playerGameSessionService;
    @MockBean
    private PlayerService playerService;
    @MockBean
    private UserService userService;
    @MockBean
    private SimpMessagingTemplate messagingTemplate;
    @MockBean
    private PuzzleFactoryService puzzleFactoryService;
    @MockBean
    private FriendshipService friendshipService;
    @MockBean
    private DataSource dataSource;
    @MockBean
    private AuthoritiesService authoritiesService;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private UserDetailsServiceImpl userDetailsService;
    @MockBean
    private EntityManagerFactory entityManagerFactory;
    @MockBean
    private JoinGameService joinGameService;
    @MockBean
    private LeaveGameService leaveGameService;

    private GameSession gameSession;
    private Player hostPlayer;
    private Player player2;

    @BeforeEach
    void setUp() {
        hostPlayer = new Player();
        hostPlayer.setId(1);
        hostPlayer.setUsername("hostUser");

        player2 = new Player();
        player2.setId(2);
        player2.setUsername("player2");

        gameSession = new GameSession();
        gameSession.setId(100);
        gameSession.setHost("hostUser");
        gameSession.setName("Test Game");
        gameSession.setGameMode(GameMode.VERSUS);
        gameSession.setNumPlayers(2);
        gameSession.setState(GameState.PENDING);
        gameSession.setPlayers(new HashSet<>());
        gameSession.setStartTime(LocalDateTime.now());
    }

    @Test
    @WithMockUser
    void shouldGetActiveGames() throws Exception {
        List<GameSession> games = new ArrayList<>();
        games.add(gameSession);
        when(gameService.getActiveGames()).thenReturn(games);

        mockMvc.perform(get("/api/v1/gameList/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(100));
    }

    @Test
    @WithMockUser
    void shouldGetGameById() throws Exception {
        when(gameService.getGameById(100)).thenReturn(gameSession);

        mockMvc.perform(get("/api/v1/gameList/{id}", 100))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.host").value("hostUser"));
    }

    @Test
    @WithMockUser
    void shouldReturnNotFoundWhenGameDoesNotExist() throws Exception {
        when(gameService.getGameById(999)).thenReturn(null);

        mockMvc.perform(get("/api/v1/gameList/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "hostUser")
    void shouldCreateGameSession() throws Exception {
        when(gameService.save(any(GameSession.class))).thenReturn(gameSession);
        when(gameService.getGameById(100)).thenReturn(gameSession);

        mockMvc.perform(post("/api/v1/gameList")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(gameSession)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100));
    }

    @Test
    @WithMockUser(username = "hostUser")
    void shouldDeleteGameSession() throws Exception {
        when(gameService.getGameById(100)).thenReturn(gameSession);
        when(gameService.delete(100)).thenReturn(gameSession);

        mockMvc.perform(delete("/api/v1/gameList/{id}", 100)
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "player2")
    void shouldJoinGameSuccessfully() throws Exception {

        when(gameService.getGameById(100)).thenReturn(gameSession);
        when(playerService.findPlayer("player2")).thenReturn(player2);
        when(playerGameSessionService.save(any(PlayerGameSession.class))).thenReturn(new PlayerGameSession());
        when(joinGameService.joinPlayerToGame(eq(100), eq("player2"), any()))
                .thenReturn((ResponseEntity) ResponseEntity.ok("Joined successfully"));

        mockMvc.perform(post("/api/v1/gameList/{id}/join", 100)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Joined successfully"));
    }

    @Test
    @WithMockUser(username = "player2")
    void shouldFailJoinIfGameIsFull() throws Exception {

        PlayerGameSession pgs1 = new PlayerGameSession();
        pgs1.setPlayer(hostPlayer);
        PlayerGameSession pgs2 = new PlayerGameSession();
        pgs2.setPlayer(new Player());

        gameSession.getPlayers().add(pgs1);
        gameSession.getPlayers().add(pgs2);

        when(gameService.getGameById(100)).thenReturn(gameSession);
        when(playerService.findPlayer("player2")).thenReturn(player2);
        when(joinGameService.joinPlayerToGame(eq(100), eq("player2"), any()))
                .thenReturn((ResponseEntity) ResponseEntity.badRequest().body("Game is full"));

        mockMvc.perform(post("/api/v1/gameList/{id}/join", 100)
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Game is full"));
    }

    @Test
    @WithMockUser(username = "player2")
    void shouldLeaveGameSuccessfully() throws Exception {
        PlayerGameSession pgs = new PlayerGameSession();
        pgs.setId(50);
        pgs.setPlayer(player2);
        pgs.setGameSession(gameSession);
        gameSession.getPlayers().add(pgs);

        when(gameService.getGameById(100)).thenReturn(gameSession);
        when(playerService.findPlayer("player2")).thenReturn(player2);
        doNothing().when(playerGameSessionService).delete(any(PlayerGameSession.class));
        when(leaveGameService.leaveGame(eq(100), eq("player2")))
                .thenReturn((ResponseEntity) ResponseEntity.ok("Left successfully"));

        mockMvc.perform(post("/api/v1/gameList/{id}/leave", 100)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Left successfully"));
    }

    @Test
    @WithMockUser(username = "hostUser")
    void shouldDeleteGameIfHostLeaves() throws Exception {

        when(gameService.getGameById(100)).thenReturn(gameSession);
        when(playerService.findPlayer("hostUser")).thenReturn(hostPlayer);

        when(gameService.delete(100)).thenReturn(gameSession);
        when(leaveGameService.leaveGame(eq(100), eq("hostUser")))
                .thenReturn((ResponseEntity) ResponseEntity.ok("Host left: game deleted and all players expelled"));

        mockMvc.perform(post("/api/v1/gameList/{id}/leave", 100)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Host left: game deleted and all players expelled"));
    }
}