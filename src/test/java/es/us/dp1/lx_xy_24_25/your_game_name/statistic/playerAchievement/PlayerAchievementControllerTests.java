package es.us.dp1.lx_xy_24_25.your_game_name.statistic.playerAchievement;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import javax.sql.DataSource;

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

import es.us.dp1.lx_xy_24_25.your_game_name.configuration.SecurityConfiguration;
import es.us.dp1.lx_xy_24_25.your_game_name.configuration.services.UserDetailsServiceImpl;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.Metric;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.achievements.Achievement;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.achievements.AchievementService;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.playerStatistics.PlayerStatistics;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.playerStatistics.PlayerStatisticsRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.user.AuthoritiesService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;

@Epic("Statistics & Achievements Module")
@Feature("Player Achievement Controller Tests")
@Owner("DP1-tutors")
@WebMvcTest(controllers = PlayerAchievementRestController.class, excludeFilters = @org.springframework.context.annotation.ComponentScan.Filter(type = org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE, classes = org.springframework.security.config.annotation.web.WebSecurityConfigurer.class), excludeAutoConfiguration = es.us.dp1.lx_xy_24_25.your_game_name.configuration.SecurityConfiguration.class)
@Import({ es.us.dp1.lx_xy_24_25.your_game_name.configuration.SpringSecurityWebAuxTestConfiguration.class,
        PlayerAchievementControllerTests.TestExceptionHandler.class })
@org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity(prePostEnabled = true)
class PlayerAchievementControllerTests {

    @org.springframework.boot.test.context.TestConfiguration
    @org.springframework.web.bind.annotation.RestControllerAdvice
    public static class TestExceptionHandler {
        @org.springframework.web.bind.annotation.ExceptionHandler(es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException.class)
        @org.springframework.web.bind.annotation.ResponseStatus(org.springframework.http.HttpStatus.NOT_FOUND)
        public void handleNotFound() {
        }

        @org.springframework.web.bind.annotation.ExceptionHandler({
                es.us.dp1.lx_xy_24_25.your_game_name.exceptions.BadRequestException.class,
                org.springframework.web.bind.MethodArgumentNotValidException.class
        })
        @org.springframework.web.bind.annotation.ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
        public void handleBadRequest() {
        }
    }

    private static final int PA_ID = 1;
    private static final int PLAYER_ID = 10;
    private static final int ACHIEVEMENT_ID = 20;
    private static final String BASE_URL = "/api/v1/player-achievements";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PlayerAchievementService paService;

    @MockBean
    private AchievementService achievementService;

    @MockBean
    private PlayerStatisticsRepository playerStatisticsRepository;

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
    private es.us.dp1.lx_xy_24_25.your_game_name.configuration.jwt.AuthEntryPointJwt authEntryPointJwt;
    @MockBean
    private es.us.dp1.lx_xy_24_25.your_game_name.configuration.jwt.JwtUtils jwtUtils;

    private PlayerAchievement playerAchievement, invalidPlayerAchievement;
    private Player player;
    private Achievement achievement;
    private PlayerStatistics playerStats;

    @BeforeEach
    void setup() {
        player = new Player();
        player.setId(PLAYER_ID);
        player.setUsername("testPlayer");

        achievement = new Achievement();
        achievement.setId(ACHIEVEMENT_ID);
        achievement.setThreshold(5.0);
        achievement.setMetric(Metric.GAMES_PLAYED);

        playerAchievement = new PlayerAchievement();
        playerAchievement.setId(PA_ID);
        playerAchievement.setPlayer(player);
        playerAchievement.setAchievement(achievement);
        playerAchievement.setProgress(3);
        playerAchievement.setCompleted(false);

        invalidPlayerAchievement = new PlayerAchievement();
        invalidPlayerAchievement.setPlayer(null);
        invalidPlayerAchievement.setAchievement(achievement);

        playerStats = new PlayerStatistics();
        playerStats.setPlayer(player);
        playerStats.setGamesPlayed(10);
    }

    @Test
    @WithMockUser
    void shouldFindByPlayerId() throws Exception {
        when(paService.findByPlayerId(PLAYER_ID)).thenReturn(List.of(playerAchievement));

        mockMvc.perform(get(BASE_URL + "/player/{playerId}", PLAYER_ID))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void shouldFindPlayerAchievementById() throws Exception {
        when(paService.findById(PA_ID)).thenReturn(playerAchievement);

        mockMvc.perform(get(BASE_URL + "/{id}", PA_ID))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    void shouldCreatePlayerAchievement() throws Exception {
        PlayerAchievement newPA = new PlayerAchievement();
        newPA.setPlayer(player);
        newPA.setAchievement(achievement);
        newPA.setProgress(0);

        when(paService.save(any(PlayerAchievement.class))).thenReturn(playerAchievement);

        mockMvc.perform(post(BASE_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPA)))
                .andExpect(status().isCreated());

        verify(paService).save(any(PlayerAchievement.class));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    void shouldReturnBadRequestOnCreateIfInvalidData() throws Exception {
        mockMvc.perform(post(BASE_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidPlayerAchievement)))
                .andExpect(status().isBadRequest());

        verify(paService, never()).save(any(PlayerAchievement.class));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    void shouldUpdateProgress() throws Exception {
        PlayerAchievement updatedPA = new PlayerAchievement();
        updatedPA.setId(PA_ID);
        updatedPA.setProgress(8);

        when(paService.updateProgress(PLAYER_ID, ACHIEVEMENT_ID, 8)).thenReturn(updatedPA);

        mockMvc.perform(
                put(BASE_URL + "/player/{playerId}/achievement/{achievementId}/progress", PLAYER_ID, ACHIEVEMENT_ID)
                        .with(csrf())
                        .param("progress", "8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.progress").value(8));

        verify(paService).updateProgress(PLAYER_ID, ACHIEVEMENT_ID, 8);
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    void shouldModifyPlayerAchievement() throws Exception {
        PlayerAchievement updateData = new PlayerAchievement();
        updateData.setId(PA_ID);
        updateData.setProgress(5);
        updateData.setCompleted(true);
        updateData.setPlayer(player);
        updateData.setAchievement(achievement);

        when(paService.modifyPlayerAchievement(eq(PA_ID), any(PlayerAchievement.class))).thenReturn(updateData);

        mockMvc.perform(put(BASE_URL + "/{id}", PA_ID)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk());

        verify(paService).modifyPlayerAchievement(eq(PA_ID), any(PlayerAchievement.class));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    void shouldDeletePlayerAchievement() throws Exception {
        doNothing().when(paService).deleteById(PA_ID);

        mockMvc.perform(delete(BASE_URL + "/{id}", PA_ID)
                .with(csrf()))
                .andExpect(status().isOk());

        verify(paService).deleteById(PA_ID);
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    void shouldReturnNotFoundOnDelete() throws Exception {
        doThrow(new ResourceNotFoundException("PA", "id", 99)).when(paService).deleteById(99);

        mockMvc.perform(delete(BASE_URL + "/{id}", 99)
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(paService).deleteById(99);
    }

}