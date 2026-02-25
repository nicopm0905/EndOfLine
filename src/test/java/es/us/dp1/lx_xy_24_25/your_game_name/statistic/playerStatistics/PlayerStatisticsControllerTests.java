package es.us.dp1.lx_xy_24_25.your_game_name.statistic.playerStatistics;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
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
import es.us.dp1.lx_xy_24_25.your_game_name.dto.PlayerRankingDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerService;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.Metric;
import es.us.dp1.lx_xy_24_25.your_game_name.user.AuthoritiesService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;

@Epic("Statistics & Achievements Module")
@Feature("Player Statistics Controller Tests")
@Owner("DP1-tutors")
@WebMvcTest(controllers = PlayerStatisticsRestController.class, excludeFilters = @org.springframework.context.annotation.ComponentScan.Filter(type = org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE, classes = org.springframework.security.config.annotation.web.WebSecurityConfigurer.class), excludeAutoConfiguration = es.us.dp1.lx_xy_24_25.your_game_name.configuration.SecurityConfiguration.class)
@Import({ es.us.dp1.lx_xy_24_25.your_game_name.configuration.SpringSecurityWebAuxTestConfiguration.class,
        PlayerStatisticsControllerTests.TestExceptionHandler.class })
@org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity(prePostEnabled = true)
class PlayerStatisticsControllerTests {

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

    private static final int STATS_ID = 1;
    private static final int PLAYER_ID = 10;
    private static final String BASE_URL = "/api/v1/statistics";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PlayerStatisticsService playerStatisticsService;

    @MockBean
    private PlayerService playerService;

    @MockBean
    private StatisticsUpdateService statisticsUpdateService;

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

    private PlayerStatistics stats;
    private Player player;

    @BeforeEach
    void setup() {
        player = new Player();
        player.setId(PLAYER_ID);
        player.setUsername("testPlayer");

        stats = new PlayerStatistics();
        stats.setId(STATS_ID);
        stats.setPlayer(player);
        stats.setGamesPlayed(10);
        stats.setVictories(5);
        stats.setTotalScore(1000);
    }

    @Test
    @WithMockUser
    void shouldGetStatisticsById() throws Exception {
        when(playerStatisticsService.findById(STATS_ID)).thenReturn(stats);

        mockMvc.perform(get(BASE_URL + "/{id}", STATS_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(STATS_ID))
                .andExpect(jsonPath("$.gamesPlayed").value(10));
    }

    @Test
    @WithMockUser
    void shouldGetStatisticsByPlayerId() throws Exception {
        when(playerStatisticsService.findByPlayerId(PLAYER_ID)).thenReturn(stats);

        mockMvc.perform(get(BASE_URL + "/player/{playerId}", PLAYER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(STATS_ID));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    void shouldCreatePlayerStatistics() throws Exception {
        when(playerStatisticsService.save(any(PlayerStatistics.class))).thenReturn(stats);

        mockMvc.perform(post(BASE_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stats)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    void shouldCreateStatisticsForPlayer() throws Exception {
        when(playerService.findPlayer(PLAYER_ID)).thenReturn(player);
        when(playerStatisticsService.createForPlayer(player)).thenReturn(stats);

        mockMvc.perform(post(BASE_URL + "/player/{playerId}", PLAYER_ID)
                .with(csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    void shouldUpdateStatistics() throws Exception {
        stats.setGamesPlayed(11);
        when(playerStatisticsService.updateStatistics(eq(PLAYER_ID), any(PlayerStatistics.class))).thenReturn(stats);

        mockMvc.perform(put(BASE_URL + "/player/{playerId}", PLAYER_ID)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stats)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gamesPlayed").value(11));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    void shouldDeleteStatistics() throws Exception {
        doNothing().when(playerStatisticsService).deleteById(STATS_ID);

        mockMvc.perform(delete(BASE_URL + "/{id}", STATS_ID)
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void shouldGetRankingByMetric() throws Exception {
        PlayerRankingDTO rankingDTO = new PlayerRankingDTO(PLAYER_ID, "testPlayer", 1, 1, Metric.TOTAL_SCORE, 1000,
                50.0, 10);
        when(playerStatisticsService.getRankingByMetric(Metric.TOTAL_SCORE, 10)).thenReturn(List.of(rankingDTO));

        mockMvc.perform(get(BASE_URL + "/ranking")
                .param("metric", "TOTAL_SCORE")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testPlayer"));
    }

    @Test
    @WithMockUser
    void shouldGetRankingByTotalScore() throws Exception {
        PlayerRankingDTO rankingDTO = new PlayerRankingDTO(PLAYER_ID, "testPlayer", 1, 1, Metric.TOTAL_SCORE, 1000,
                50.0, 10);
        when(playerStatisticsService.getRankingByMetric(Metric.TOTAL_SCORE, 10)).thenReturn(List.of(rankingDTO));

        mockMvc.perform(get(BASE_URL + "/ranking/total-score"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].value").value(1000));
    }
}