package es.us.dp1.lx_xy_24_25.your_game_name.social.chat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.Color;

import es.us.dp1.lx_xy_24_25.your_game_name.configuration.SecurityConfiguration;
import es.us.dp1.lx_xy_24_25.your_game_name.configuration.jwt.AuthEntryPointJwt;
import es.us.dp1.lx_xy_24_25.your_game_name.configuration.jwt.JwtUtils;
import es.us.dp1.lx_xy_24_25.your_game_name.configuration.services.UserDetailsServiceImpl;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.ChatMessageDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.ChatRequestDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.BadRequestException;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.PlayerGameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.social.chatMessage.ChatMessage;
import es.us.dp1.lx_xy_24_25.your_game_name.social.chatMessage.ChatMessageController;
import es.us.dp1.lx_xy_24_25.your_game_name.social.chatMessage.ChatMessageService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.AuthoritiesService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import org.springframework.http.HttpStatus;

@Epic("Social Module")
@Feature("Chat Message Controller Tests")
@Owner("DP1-tutors")
@WebMvcTest(ChatMessageController.class)
@Import(ChatMessageControllerTests.TestExceptionHandler.class)
class ChatMessageControllerTests {

    @TestConfiguration
    @RestControllerAdvice
    public static class TestExceptionHandler {
        @ExceptionHandler(ResourceNotFoundException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        public void handleNotFound() {
        }

        @ExceptionHandler(Exception.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public void handleAll() {
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatMessageService chatMessageService;
    @MockBean
    private SimpMessagingTemplate messagingTemplate;
    @MockBean
    private DataSource dataSource;
    @MockBean
    private UserService userService;
    @MockBean
    private AuthoritiesService authoritiesService;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private UserDetailsServiceImpl userDetailsService;
    @MockBean
    private AuthEntryPointJwt authEntryPointJwt;
    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;
    private ChatMessage chatMessage1;
    private ChatMessage chatMessage2;
    private ChatMessageDTO chatMessageDTO1;
    private PlayerGameSession playerGameSession;
    private GameSession gameSession;
    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player();
        player.setId(1);
        player.setUsername("player1");

        gameSession = new GameSession();
        gameSession.setId(1);

        playerGameSession = new PlayerGameSession();
        playerGameSession.setId(1);
        playerGameSession.setPlayer(player);
        playerGameSession.setGameSession(gameSession);
        playerGameSession.setPlayerColor(Color.RED);

        chatMessage1 = new ChatMessage();
        chatMessage1.setId(1);
        chatMessage1.setMessage("Hi to everyone!");
        chatMessage1.setColor(Color.RED);
        chatMessage1.setPlayerGameSession(playerGameSession);

        chatMessage2 = new ChatMessage();
        chatMessage2.setId(2);
        chatMessage2.setMessage("Good game!");
        chatMessage2.setColor(Color.RED);
        chatMessage2.setPlayerGameSession(playerGameSession);

        chatMessageDTO1 = new ChatMessageDTO(chatMessage1);
    }

    @Test
    @WithMockUser(username = "player1", authorities = { "PLAYER" })
    void testCreateMessageInGame() throws Exception {
        ChatRequestDTO request = new ChatRequestDTO();
        request.setMessage("Hi to everyone!");

        when(chatMessageService.createAndBroadcast(any(ChatRequestDTO.class), anyInt(), anyString()))
                .thenReturn(chatMessageDTO1);

        mockMvc.perform(post("/api/v1/chat/{gameId}/message", 1)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Hi to everyone!"))
                .andExpect(jsonPath("$.color").value("RED"));
    }

    @Test
    @WithMockUser(username = "player1", authorities = { "PLAYER" })
    void testGetAllMessagesFromGame() throws Exception {
        ChatMessageDTO chatMessageDTO2 = new ChatMessageDTO(chatMessage2);
        List<ChatMessageDTO> messages = Arrays.asList(chatMessageDTO1, chatMessageDTO2);
        when(chatMessageService.findAllMessageDTOsByGameId(1)).thenReturn(messages);

        mockMvc.perform(get("/api/v1/chat/{gameId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].message").value("Hi to everyone!"))
                .andExpect(jsonPath("$[1].message").value("Good game!"));
    }

    @Test
    @WithMockUser(username = "player1", authorities = { "PLAYER" })
    void testCreateMessageInNonExistentGame() throws Exception {
        ChatRequestDTO request = new ChatRequestDTO();
        request.setMessage("Message in non-existent game");

        when(chatMessageService.createAndBroadcast(any(ChatRequestDTO.class), anyInt(), anyString()))
                .thenThrow(new ResourceNotFoundException("GameSession", "id", 999));

        mockMvc.perform(post("/api/v1/chat/{gameId}/message", 999)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "player1", authorities = { "PLAYER" })
    void testCreateMessageWhenPlayerNotInGame() throws Exception {
        ChatRequestDTO request = new ChatRequestDTO();
        request.setMessage("Message from player not in game");

        when(chatMessageService.createAndBroadcast(any(ChatRequestDTO.class), anyInt(), anyString()))
                .thenThrow(new BadRequestException("Player player1 is not in game 1"));

        mockMvc.perform(post("/api/v1/chat/{gameId}/message", 1)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "player1", authorities = { "PLAYER" })
    void testCreateMessageWithEmptyText() throws Exception {
        ChatRequestDTO request = new ChatRequestDTO();
        request.setMessage("");

        mockMvc.perform(post("/api/v1/chat/{gameId}/message", 1)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "player1", authorities = { "PLAYER" })
    void testCreateMessageWithTooLongText() throws Exception {
        ChatRequestDTO request = new ChatRequestDTO();
        request.setMessage("a".repeat(501));

        mockMvc.perform(post("/api/v1/chat/{gameId}/message", 1)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateMessageWithoutAuthentication() throws Exception {
        ChatRequestDTO request = new ChatRequestDTO();
        request.setMessage("Message without authentication");

        mockMvc.perform(post("/api/v1/chat/{gameId}/message", 1)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "player1", authorities = { "PLAYER" })
    void testGetMessagesFromEmptyChat() throws Exception {
        when(chatMessageService.findAllMessageDTOsByGameId(1)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/v1/chat/{gameId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}