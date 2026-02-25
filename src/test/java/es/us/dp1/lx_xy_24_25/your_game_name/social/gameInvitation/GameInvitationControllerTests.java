package es.us.dp1.lx_xy_24_25.your_game_name.social.gameInvitation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.us.dp1.lx_xy_24_25.your_game_name.configuration.SecurityConfiguration;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.CreateGameInvitationDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSessionService;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerService;

@WebMvcTest(controllers = GameInvitationController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class),
    excludeAutoConfiguration = SecurityConfiguration.class)
class GameInvitationControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameInvitationService gameInvitationService;

    @MockBean
    private PlayerService playerService;

    @MockBean
    private GameSessionService gameSessionService;

    @Autowired
    private ObjectMapper objectMapper;

    private Player sender;
    private Player receiver;
    private GameSession game;
    private GameInvitation invitation;

    @BeforeEach
    void setUp() {
        sender = new Player();
        sender.setId(1);
        sender.setUsername("sender");

        receiver = new Player();
        receiver.setId(2);
        receiver.setUsername("receiver");

        game = new GameSession();
        game.setId(100);
        game.setName("Test Game");

        invitation = new GameInvitation(sender, receiver, game, InvitationType.PLAYER);
        invitation.setId(1);
        invitation.setCreatedAt(LocalDateTime.now());
        invitation.setStatus(InvitationStatus.PENDING);
    }

    @Test
    @WithMockUser(username = "sender")
    void shouldCreateInvitation() throws Exception {
        CreateGameInvitationDTO dto = new CreateGameInvitationDTO();
        dto.setReceiverId(2);
        dto.setGameSessionId(100);
        dto.setType(InvitationType.PLAYER);

        when(playerService.findByUsername("sender")).thenReturn(sender);
        when(playerService.findPlayer(2)).thenReturn(receiver);
        when(gameSessionService.getGameById(100)).thenReturn(game);
        when(gameInvitationService.createInvitation(any(Player.class), any(Player.class), any(GameSession.class), any(InvitationType.class)))
            .thenReturn(invitation);

        mockMvc.perform(post("/api/v1/invitations")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.senderUsername").value("sender"))
                .andExpect(jsonPath("$.receiverUsername").value("receiver"));
    }

    @Test
    @WithMockUser(username = "receiver")
    void shouldGetReceivedInvitations() throws Exception {
        when(playerService.findByUsername("receiver")).thenReturn(receiver);
        when(gameInvitationService.getReceivedInvitations(receiver)).thenReturn(List.of(invitation));

        mockMvc.perform(get("/api/v1/invitations/received")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @WithMockUser(username = "receiver")
    void shouldGetPendingInvitations() throws Exception {
        when(playerService.findByUsername("receiver")).thenReturn(receiver);
        when(gameInvitationService.getPendingReceivedInvitations(receiver)).thenReturn(List.of(invitation));

        mockMvc.perform(get("/api/v1/invitations/received/pending")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @WithMockUser(username = "sender")
    void shouldGetSentInvitations() throws Exception {
        when(playerService.findByUsername("sender")).thenReturn(sender);
        when(gameInvitationService.getSentInvitations(sender)).thenReturn(List.of(invitation));

        mockMvc.perform(get("/api/v1/invitations/sent")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @WithMockUser(username = "receiver")
    void shouldGetPendingCount() throws Exception {
        when(playerService.findByUsername("receiver")).thenReturn(receiver);
        when(gameInvitationService.getPendingInvitationCount(receiver)).thenReturn(5L);

        mockMvc.perform(get("/api/v1/invitations/pending-count")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(5));
    }

    @Test
    @WithMockUser(username = "receiver")
    void shouldAcceptInvitation() throws Exception {
        invitation.setStatus(InvitationStatus.ACCEPTED);
        invitation.setRespondedAt(LocalDateTime.now());
        
        when(gameInvitationService.acceptInvitation(1)).thenReturn(invitation);

        mockMvc.perform(put("/api/v1/invitations/1/accept")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));
    }

    @Test
    @WithMockUser(username = "receiver")
    void shouldRejectInvitation() throws Exception {
        invitation.setStatus(InvitationStatus.REJECTED);
        invitation.setRespondedAt(LocalDateTime.now());
        
        when(gameInvitationService.rejectInvitation(1)).thenReturn(invitation);

        mockMvc.perform(put("/api/v1/invitations/1/reject")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    @WithMockUser(username = "sender")
    void shouldCancelInvitation() throws Exception {
        invitation.setStatus(InvitationStatus.CANCELED);
        invitation.setRespondedAt(LocalDateTime.now());
        
        when(gameInvitationService.cancelInvitation(1)).thenReturn(invitation);

        mockMvc.perform(put("/api/v1/invitations/1/cancel")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELED"));
    }
}
