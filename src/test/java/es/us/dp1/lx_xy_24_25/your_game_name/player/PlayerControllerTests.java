package es.us.dp1.lx_xy_24_25.your_game_name.player;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.us.dp1.lx_xy_24_25.your_game_name.configuration.SecurityConfiguration;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.AccessDeniedException;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.playerStatistics.PlayerStatisticsService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.Authorities;
import es.us.dp1.lx_xy_24_25.your_game_name.user.AuthoritiesService;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;


@Epic("Users & Admin Module")
@Feature("Player Management")
@Owner("DP1-tutors")
@WebMvcTest(controllers = PlayerController.class, 
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), 
    excludeAutoConfiguration = SecurityConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class PlayerControllerTests {

    private static final int PLAYER_ID = 1;
    private static final int OTHER_PLAYER_ID = 2;
    private static final int ADMIN_ID = 99;
    private static final int NON_EXISTENT_ID = 999;
    private static final String PLAYER_USERNAME = "player1";
    private static final String ADMIN_USERNAME = "adminuser";
    private static final String BASE_URL = "/api/v1/players";

    @Autowired 
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean 
    private PlayerService playerService;
    
    @MockBean 
    private AuthoritiesService authService;
    
    @MockBean 
    private PlayerStatisticsService playerStatisticsService;

    private Player player;
    private Player admin;
    private Player otherPlayer;
    private Player adminSpy; 
    private Authorities authPlayer;
    private Authorities authAdmin;

    @BeforeEach
    void setup() {
        authPlayer = new Authorities();
        authPlayer.setId(1);     
        authPlayer.setAuthority("PLAYER");

        Authorities authAdmin = new Authorities();
        authAdmin.setId(2);
        authAdmin.setAuthority("ADMIN");
        
        player = new Player();
        player.setId(PLAYER_ID);
        player.setUsername(PLAYER_USERNAME);
        player.setPassword("pass");
        player.setFirstName("Player"); player.setLastName("Player"); player.setEmail("player@test.com");
        player.setAuthority(authPlayer);
        player.setAvatarId(1);

        admin = new Player();
        admin.setId(ADMIN_ID);
        admin.setUsername(ADMIN_USERNAME);
        admin.setPassword("adminpass");
        admin.setFirstName("Admin"); admin.setLastName("Root"); admin.setEmail("admin@test.com");
        admin.setAuthority(authAdmin);
        admin.setAvatarId(1);

        otherPlayer = new Player();
        otherPlayer.setId(OTHER_PLAYER_ID);
        otherPlayer.setUsername("other");
        otherPlayer.setPassword("otherpass");
        otherPlayer.setFirstName("Other"); otherPlayer.setLastName("P"); otherPlayer.setEmail("other@test.com");
        otherPlayer.setAuthority(authPlayer);
        otherPlayer.setAvatarId(1);

        adminSpy = Mockito.spy(admin);
        when(adminSpy.hasAuthority("ADMIN")).thenReturn(true);
        
        when(playerService.findCurrentPlayer()).thenAnswer(invocation -> {
            UserDetails details = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (details.getUsername().equals(ADMIN_USERNAME)) {
                return adminSpy;
            } else if (details.getUsername().equals(PLAYER_USERNAME)) {
                 Player playerSpy = Mockito.spy(player);
                 when(playerSpy.hasAuthority("ADMIN")).thenReturn(false);
                 return playerSpy;
            }
            return player; 
        });
    
        when(playerService.findPlayer(PLAYER_ID)).thenReturn(player);
        when(playerService.findPlayer(OTHER_PLAYER_ID)).thenReturn(otherPlayer);
        when(playerService.findPlayer(ADMIN_ID)).thenReturn(admin);
        when(playerService.findPlayer(PLAYER_USERNAME)).thenReturn(player);
        when(playerService.findPlayer(ADMIN_USERNAME)).thenReturn(admin);

        when(playerService.findPlayer(NON_EXISTENT_ID)).thenThrow(new ResourceNotFoundException("Player", "ID", NON_EXISTENT_ID));
    }


    @Test
    @WithMockUser(username = ADMIN_USERNAME, authorities = {"ADMIN"})
    void shouldFindAll() throws Exception {
        when(playerService.findAll()).thenReturn(List.of(player, admin));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
        
        verify(playerService).findAll();
    }

    @Test
    @WithMockUser(username = ADMIN_USERNAME, authorities = {"ADMIN"})
    void shouldFindAllAuthorities() throws Exception {
        Authorities adminAuth = new Authorities();
        adminAuth.setId(2);
        adminAuth.setAuthority("ADMIN");

        when(authService.findAll()).thenReturn(List.of(authPlayer, adminAuth));

        mockMvc.perform(get(BASE_URL + "/authorities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[?(@.authority == 'PLAYER')]").exists())
                .andExpect(jsonPath("$[?(@.authority == 'ADMIN')]").exists());
    }

    @Test
    @WithMockUser(username = ADMIN_USERNAME, authorities = {"ADMIN"})
    void shouldFindPlayerById() throws Exception {
        when(playerService.findPlayer(PLAYER_ID)).thenReturn(player);

        mockMvc.perform(get(BASE_URL + "/{id}", PLAYER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(PLAYER_ID))
                .andExpect(jsonPath("$.username").value(PLAYER_USERNAME));
    }

    @Test
    @WithMockUser(username = ADMIN_USERNAME, authorities = {"ADMIN"})
    void shouldFindPlayerByUsername() throws Exception {
        when(playerService.findByUsername(PLAYER_USERNAME)).thenReturn(player);

        mockMvc.perform(get(BASE_URL + "/username/{username}", PLAYER_USERNAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(PLAYER_ID))
                .andExpect(jsonPath("$.username").value(PLAYER_USERNAME));
    }

    @Test
    @WithMockUser(username = ADMIN_USERNAME, authorities = {"ADMIN"})
    void shouldCreatePlayer() throws Exception {
        String playerJson = """
        {
          "username": "newplayer",
          "password": "password",
          "firstName": "New",
          "lastName": "User",
          "email": "new@user.com",
          "avatarId": 1,
          "authority": { "id": 1, "authority": "PLAYER" }
        }
        """;

        Player savedPlayer = new Player();
        savedPlayer.setId(123);
        savedPlayer.setUsername("newplayer");
        savedPlayer.setPassword("password");
        savedPlayer.setFirstName("New");
        savedPlayer.setLastName("User");
        savedPlayer.setEmail("new@user.com");
        savedPlayer.setAvatarId(1);
        savedPlayer.setAuthority(authPlayer);

        when(playerService.savePlayer(any(Player.class))).thenReturn(savedPlayer);

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(playerJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newplayer"));

        verify(playerService).savePlayer(any(Player.class));
    }

    @Test
    @WithMockUser(username = ADMIN_USERNAME, authorities = {"ADMIN"})
    void shouldUpdatePlayer() throws Exception {
        String updateJson = """
        {
          "username": "updated_name",
          "password": "newpass",
          "firstName": "Player",
          "lastName": "Player",
          "email": "player@test.com",
          "avatarId": 1,
          "authority": { "id": 1, "authority": "PLAYER" }
        }
        """;

        when(playerService.findPlayer(PLAYER_ID)).thenReturn(player);
        when(playerService.updatePlayer(any(Player.class), anyInt())).thenReturn(player);

        mockMvc.perform(put(BASE_URL + "/{playerId}", PLAYER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk());

        verify(playerService).updatePlayer(any(Player.class), anyInt());
    }
    
    @Test
    @WithMockUser(username = ADMIN_USERNAME, authorities = {"ADMIN"})
    void shouldReturnNotFoundOnUpdate() throws Exception {
        String updateJson = """
        {
          "username": "ghost",
          "password": "none",
          "firstName": "Ghost",
          "lastName": "Player",
          "email": "ghost@player.com",
          "avatarId": 1,
          "authority": { "id": 1, "authority": "PLAYER" }
        }
        """;

        when(playerService.findPlayer(anyInt()))
                .thenThrow(new ResourceNotFoundException("Player", "ID", 99));

        mockMvc.perform(put(BASE_URL + "/{playerId}", 99)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isNotFound());

        verify(playerService, never()).updatePlayer(any(Player.class), anyInt());
    }
    
    @Test
    @WithMockUser(username = ADMIN_USERNAME, authorities = {"ADMIN"})
    void shouldDeleteOtherPlayer_asAdmin() throws Exception {
        Player otherPlayer = new Player();
        otherPlayer.setId(OTHER_PLAYER_ID);
        
        when(playerService.findPlayer(OTHER_PLAYER_ID)).thenReturn(otherPlayer); 
        
        mockMvc.perform(delete(BASE_URL + "/{playerId}", OTHER_PLAYER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Player deleted!"));
        
        verify(playerService).deletePlayer(OTHER_PLAYER_ID); 
    }
    
    @Test
    @WithMockUser(PLAYER_USERNAME)
    void shouldDeleteOwnPlayer_asOwnUser() throws Exception {
        when(playerService.findPlayer(PLAYER_ID)).thenReturn(player); 
        
        mockMvc.perform(delete(BASE_URL + "/{playerId}", PLAYER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Player deleted!"));
        
        verify(playerService).deletePlayer(PLAYER_ID);
    }
    
}
