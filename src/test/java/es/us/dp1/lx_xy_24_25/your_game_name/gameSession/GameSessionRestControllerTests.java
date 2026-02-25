package es.us.dp1.lx_xy_24_25.your_game_name.gameSession;

import es.us.dp1.lx_xy_24_25.your_game_name.dto.EnergyActionRequestDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.GameStartDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.JoinRequestDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.PlayerGameSession;
import org.springframework.http.ResponseEntity;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.us.dp1.lx_xy_24_25.your_game_name.configuration.SecurityConfiguration;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.GameSessionDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.JoinGameService;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.LeaveGameService;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.PlayerGameSessionService;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerService;
import es.us.dp1.lx_xy_24_25.your_game_name.puzzle.PuzzleFactoryService;
import es.us.dp1.lx_xy_24_25.your_game_name.social.friendship.FriendshipService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardTemplate;
import es.us.dp1.lx_xy_24_25.your_game_name.puzzle.PuzzleDefinition;

@WebMvcTest(controllers = GameSessionRestController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityConfiguration.class)
class GameSessionRestControllerTests {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private GameSessionService gameSessionService;

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
        private JoinGameService joinGameService;

        @MockBean
        private LeaveGameService leaveGameService;

        private GameSession gameSession;

        @BeforeEach
        void setUp() {
                gameSession = new GameSession();
                gameSession.setId(1);
                gameSession.setGameMode(GameMode.VERSUS);
                gameSession.setNumPlayers(4);
                gameSession.setBoardSize(5);
                gameSession.setHost("player1");
                gameSession.setState(GameState.PENDING);
        }

        @Test
        @WithMockUser
        void shouldGetActiveGames() throws Exception {
                given(gameSessionService.getActiveGames()).willReturn(List.of(gameSession));

                mockMvc.perform(get("/api/v1/gameList/active"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.size()").value(1))
                                .andExpect(jsonPath("$[0].id").value(1));
        }

        @Test
        @WithMockUser
        void shouldGetPendingGames() throws Exception {
                given(gameSessionService.getPendingGames()).willReturn(List.of(gameSession));

                mockMvc.perform(get("/api/v1/gameList/pending"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.size()").value(1));
        }

        @Test
        @WithMockUser
        void shouldGetFinishedGames() throws Exception {
                given(gameSessionService.getFinishedGames()).willReturn(List.of(gameSession));

                mockMvc.perform(get("/api/v1/gameList/finished"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.size()").value(1));
        }

        @Test
        @WithMockUser
        void shouldGetGameById() throws Exception {
                given(gameSessionService.getGameById(1)).willReturn(gameSession);

                mockMvc.perform(get("/api/v1/gameList/{id}", 1))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @WithMockUser
        void shouldCreateGameSession() throws Exception {
                given(gameSessionService.save(any(GameSession.class))).willReturn(gameSession);
                given(gameSessionService.getGameById(1)).willReturn(gameSession);

                mockMvc.perform(post("/api/v1/gameList")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(gameSession)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @WithMockUser
        void shouldGetSpectatorView() throws Exception {
                given(gameSessionService.getSpectatorView(1)).willReturn(null);

                mockMvc.perform(get("/api/v1/gameList/{id}/spectate", 1))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "player1")
        void shouldJoinGame() throws Exception {
                JoinRequestDTO joinRequest = new JoinRequestDTO();
                joinRequest.setPassword("pass");

                given(joinGameService.joinPlayerToGame(eq(1), eq("player1"), any(JoinRequestDTO.class)))
                                .willReturn(ResponseEntity.ok().build());

                mockMvc.perform(post("/api/v1/gameList/{id}/join", 1)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(joinRequest)))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "player1")
        void shouldLeaveGame() throws Exception {
                given(leaveGameService.leaveGame(1, "player1"))
                                .willReturn(ResponseEntity.ok().build());

                mockMvc.perform(post("/api/v1/gameList/{id}/leave", 1)
                                .with(csrf()))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "player1")
        void shouldConsumeEnergy() throws Exception {
                EnergyActionRequestDTO request = new EnergyActionRequestDTO();
                request.setActionId("BOOST");

                PlayerGameSession pgs = new PlayerGameSession();
                pgs.setId(1);
                given(playerGameSessionService.consumeEnergy(eq(1), eq("player1"), eq("BOOST"))).willReturn(pgs);
                given(gameSessionService.getGameById(1)).willReturn(gameSession);

                mockMvc.perform(post("/api/v1/gameList/{id}/energy", 1)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "player1")
        void shouldDiscardCard() throws Exception {

                Player player = new Player();
                player.setUsername("player1");
                given(playerService.findPlayer("player1")).willReturn(player);
                given(gameSessionService.getGameById(1)).willReturn(gameSession);

                mockMvc.perform(post("/api/v1/gameList/{id}/discard", 1)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"cardId\": 1}"))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser
        void shouldDeleteGameSession_NotFound() throws Exception {
                given(gameSessionService.getGameById(999)).willReturn(null);

                mockMvc.perform(delete("/api/v1/gameList/{id}", 999)
                                .with(csrf()))
                                .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(username = "player2", authorities = { "PLAYER" })
        void shouldFailFinishGameIfNotHost() throws Exception {
                gameSession.setHost("player1");
                given(gameSessionService.getGameById(1)).willReturn(gameSession);

                User user = new User();
                user.setUsername("player2"); // Current user is NOT host

                mockMvc.perform(post("/api/v1/gameList/{id}/finish", 1)
                                .with(csrf())
                                .param("winner", "player1"))
                                .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "player1")
        void shouldSwitchTeam() throws Exception {
                given(gameSessionService.getGameById(1)).willReturn(gameSession);
                mockMvc.perform(post("/api/v1/gameList/{id}/switchTeam", 1)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"teamNumber\": 2}"))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "player1")
        void shouldGetActiveGamesWithFriends() throws Exception {
                given(friendshipService.getConfirmedFriendUsernames("player1")).willReturn(List.of("friend1"));
                given(gameSessionService.getActiveGamesByFriends(any()))
                                .willReturn(List.of(new GameSessionDTO(gameSession)));

                mockMvc.perform(get("/api/v1/gameList/active/friends"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.size()").value(1));
        }

        @Test
        @WithMockUser
        void shouldUpdateGameSession() throws Exception {
                GameSession updated = new GameSession();
                updated.setId(1);
                updated.setGameMode(GameMode.VERSUS);
                updated.setNumPlayers(4);
                updated.setBoardSize(5);
                updated.setHost("player1");
                updated.setState(GameState.PENDING);

                given(gameSessionService.getGameById(1)).willReturn(gameSession);
                given(gameSessionService.calculateBoardSize(GameMode.VERSUS, 4)).willReturn(5);
                given(gameSessionService.update(any(GameSession.class), eq(1))).willReturn(updated);

                mockMvc.perform(put("/api/v1/gameList/{id}", 1)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updated)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @WithMockUser
        void shouldDeleteGameSession() throws Exception {
                given(gameSessionService.getGameById(1)).willReturn(gameSession);

                mockMvc.perform(delete("/api/v1/gameList/{id}", 1)
                                .with(csrf()))
                                .andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser(username = "player1", authorities = { "PLAYER" })
        void shouldStartGame() throws Exception {
                User user = new User();
                user.setUsername("player1");

                given(userService.findByUsername("player1")).willReturn(user);
                given(gameSessionService.startGame(eq(1), eq(user), any(GameSession.class)))
                                .willReturn(new GameStartDTO(gameSession, user));
                given(gameSessionService.getGameById(1)).willReturn(gameSession);

                mockMvc.perform(post("/api/v1/gameList/{id}/start", 1)
                                .with(csrf()))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "player1", authorities = { "PLAYER" })
        void shouldFinishGame() throws Exception {
                GameSession finished = new GameSession();
                finished.setId(1);
                finished.setHost("player1");

                given(gameSessionService.getGameById(1)).willReturn(finished);
                given(gameSessionService.finishGame(1, "player1")).willReturn(finished);

                mockMvc.perform(post("/api/v1/gameList/{gameId}/finish", 1)
                                .with(csrf())
                                .param("winner", "player1"))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "player1")
        void shouldDrawFromDeck() throws Exception {
                Player player = new Player();
                player.setUsername("player1");
                given(playerService.findPlayer("player1")).willReturn(player);
                given(gameSessionService.getGameById(1)).willReturn(gameSession);

                mockMvc.perform(post("/api/v1/gameList/{id}/drawdeck", 1)
                                .with(csrf()))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "player1")
        void shouldDrawFromDiscard() throws Exception {
                given(gameSessionService.getGameById(1)).willReturn(gameSession);

                mockMvc.perform(post("/api/v1/gameList/{id}/draw-discard", 1)
                                .with(csrf()))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "player1")
        void shouldGetInitiativeCard() throws Exception {
                CardTemplate template = new CardTemplate();
                template.setId(1);
                given(playerGameSessionService.drawFirstCardForInitiative(1, "player1")).willReturn(template);

                mockMvc.perform(get("/api/v1/gameList/{gameSessionId}/player/{username}/initiative", 1, "player1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @WithMockUser(username = "player1")
        void shouldStartPuzzle() throws Exception {
                Player player = new Player();
                player.setUsername("player1");
                given(playerService.findPlayer("player1")).willReturn(player);
                given(puzzleFactoryService.createPuzzleGame(eq(1), eq(player))).willReturn(gameSession);

                mockMvc.perform(post("/api/v1/gameList/puzzle/{puzzleId}", 1)
                                .with(csrf()))
                                .andExpect(status().isCreated());
        }

        @Test
        @WithMockUser(username = "player1")
        void shouldStartSolitaire() throws Exception {
                Player player = new Player();
                player.setUsername("player1");
                given(playerService.findPlayer("player1")).willReturn(player);
                given(puzzleFactoryService.createSolitaireGame(eq(1), eq(player))).willReturn(gameSession);

                mockMvc.perform(post("/api/v1/gameList/solitaire/{solitaireId}", 1)
                                .with(csrf()))
                                .andExpect(status().isCreated());
        }

        @Test
        @WithMockUser
        void shouldGetPuzzleList() throws Exception {
                given(puzzleFactoryService.getAllPuzzles())
                                .willReturn(List.of(new PuzzleDefinition(1, "Puzzle 1", List.of())));

                mockMvc.perform(get("/api/v1/gameList/puzzle"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.size()").value(1));
        }

}
