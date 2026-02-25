package es.us.dp1.lx_xy_24_25.your_game_name.social.friendship;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.us.dp1.lx_xy_24_25.your_game_name.configuration.SpringSecurityWebAuxTestConfiguration;
import es.us.dp1.lx_xy_24_25.your_game_name.configuration.WebSocketEventListener;
import es.us.dp1.lx_xy_24_25.your_game_name.configuration.jwt.AuthEntryPointJwt;
import es.us.dp1.lx_xy_24_25.your_game_name.configuration.jwt.JwtUtils;
import es.us.dp1.lx_xy_24_25.your_game_name.configuration.services.UserDetailsServiceImpl;
import es.us.dp1.lx_xy_24_25.your_game_name.configuration.ExceptionHandlerConfiguration;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.FriendshipDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.FriendshipRequestDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.BadRequestException;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.user.AuthoritiesService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;

@WebMvcTest(controllers = FriendshipController.class, excludeFilters = @org.springframework.context.annotation.ComponentScan.Filter(type = org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE, classes = org.springframework.security.config.annotation.web.WebSecurityConfigurer.class), excludeAutoConfiguration = es.us.dp1.lx_xy_24_25.your_game_name.configuration.SecurityConfiguration.class)
public class FriendshipControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FriendshipService friendshipService;

    @MockBean
    private javax.sql.DataSource dataSource;
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
    @MockBean
    private WebSocketEventListener webSocketEventListener;

    @Autowired
    private ObjectMapper objectMapper;

    private Friendship friendship1;
    private Friendship friendship2;
    private Player player1;
    private Player player2;
    private Player player3;

    @BeforeEach
    void setUp() {
        player1 = new Player();
        player1.setId(1);
        player1.setUsername("player1");

        player2 = new Player();
        player2.setId(2);
        player2.setUsername("player2");

        player3 = new Player();
        player3.setId(3);
        player3.setUsername("player3");

        friendship1 = new Friendship();
        friendship1.setId(1);
        friendship1.setSender(player1);
        friendship1.setReceiver(player2);
        friendship1.setState(FriendshipState.PENDING);
        friendship1.setRequestDate(LocalDateTime.now());

        friendship2 = new Friendship();
        friendship2.setId(2);
        friendship2.setSender(player2);
        friendship2.setReceiver(player3);
        friendship2.setState(FriendshipState.ACCEPTED);
        friendship2.setRequestDate(LocalDateTime.now().minusDays(1));
        friendship2.setStartDate(LocalDateTime.now());
    }

    @Test
    @WithMockUser(username = "player1", authorities = { "PLAYER" })
    void testGetAllFriendships() throws Exception {
        List<FriendshipDTO> friendships = Arrays.asList(new FriendshipDTO(friendship1), new FriendshipDTO(friendship2));
        when(friendshipService.findAllDtosWithStatus()).thenReturn(friendships);

        mockMvc.perform(get("/api/v1/friendships/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].state").value("PENDING"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].state").value("ACCEPTED"));
    }

    @Test
    @WithMockUser(username = "player1", authorities = { "PLAYER" })
    void testFindById() throws Exception {
        when(friendshipService.findDtoById(1)).thenReturn(new FriendshipDTO(friendship1));

        mockMvc.perform(get("/api/v1/friendships/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.state").value("PENDING"));
    }

    @Test
    @WithMockUser(username = "player1", authorities = { "PLAYER" })
    void testFindByIdNotFound() throws Exception {
        when(friendshipService.findDtoById(999)).thenThrow(new ResourceNotFoundException("Friendship", "id", 999));

        mockMvc.perform(get("/api/v1/friendships/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "player1", authorities = { "PLAYER" })
    void testCreateFriendship() throws Exception {
        FriendshipRequestDTO requestDTO = new FriendshipRequestDTO();
        requestDTO.setSender(1);
        requestDTO.setReceiver(2);

        when(friendshipService.createDto(any(FriendshipRequestDTO.class))).thenReturn(new FriendshipDTO(friendship1));
        mockMvc.perform(post("/api/v1/friendships")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.state").value("PENDING"));
    }

    @Test
    @WithMockUser(username = "player1", authorities = { "PLAYER" })
    void testUpdateFriendship() throws Exception {
        FriendshipRequestDTO requestDTO = new FriendshipRequestDTO();
        requestDTO.setState(FriendshipState.ACCEPTED);

        FriendshipDTO updatedDto = new FriendshipDTO(friendship1);
        updatedDto.setState(FriendshipState.ACCEPTED);

        when(friendshipService.updateDto(anyInt(), any(FriendshipRequestDTO.class)))
                .thenReturn(updatedDto);

        mockMvc.perform(put("/api/v1/friendships/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.state").value("ACCEPTED"));
    }

    @Test
    @WithMockUser(username = "player1", authorities = { "PLAYER" })
    void testDeleteFriendship() throws Exception {
        doNothing().when(friendshipService).delete(1);

        mockMvc.perform(delete("/api/v1/friendships/1")
                .with(csrf()))
                .andExpect(status().isOk());
    }

}