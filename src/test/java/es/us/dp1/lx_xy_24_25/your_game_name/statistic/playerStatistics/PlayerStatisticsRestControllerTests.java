package es.us.dp1.lx_xy_24_25.your_game_name.statistic.playerStatistics;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.us.dp1.lx_xy_24_25.your_game_name.configuration.SecurityConfiguration;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.PlayerRankingDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerService;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.Metric;

@WebMvcTest(controllers = PlayerStatisticsRestController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfiguration.class),
    excludeAutoConfiguration = org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class)
class PlayerStatisticsRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PlayerStatisticsService playerStatisticsService;

    @MockBean
    private PlayerService playerService;

    private PlayerStatistics stats;
    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player();
        player.setId(1);
        player.setUsername("testUser");

        stats = new PlayerStatistics();
        stats.setId(100);
        stats.setPlayer(player);
        stats.setGamesPlayed(10);
        stats.setVictories(5);
        stats.setTotalScore(1000);
    }

    @Test
    @WithMockUser
    void getStatisticsById_ShouldReturnStats() throws Exception {
        when(playerStatisticsService.findById(100)).thenReturn(stats);

        mockMvc.perform(get("/api/v1/statistics/100"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(100))
            .andExpect(jsonPath("$.gamesPlayed").value(10));
    }

    @Test
    @WithMockUser
    void getStatisticsByPlayerId_ShouldReturnStats() throws Exception {
        when(playerStatisticsService.findByPlayerId(1)).thenReturn(stats);

        mockMvc.perform(get("/api/v1/statistics/player/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(100));
    }

    @Test
    @WithMockUser
    void createPlayerStatistics_ShouldReturnCreated() throws Exception {
        when(playerStatisticsService.save(any(PlayerStatistics.class))).thenReturn(stats);

        mockMvc.perform(post("/api/v1/statistics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stats)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(100));
    }

    @Test
    @WithMockUser
    void createStatisticsForPlayer_ShouldReturnCreated() throws Exception {
        when(playerService.findPlayer(1)).thenReturn(player);
        when(playerStatisticsService.createForPlayer(player)).thenReturn(stats);

        mockMvc.perform(post("/api/v1/statistics/player/1"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(100));
    }

    @Test
    @WithMockUser
    void updateStatistics_ShouldReturnUpdated() throws Exception {
        when(playerStatisticsService.updateStatistics(eq(1), any(PlayerStatistics.class))).thenReturn(stats);

        mockMvc.perform(put("/api/v1/statistics/player/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stats)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(100));
    }

    @Test
    @WithMockUser
    void deleteStatistics_ShouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/v1/statistics/100"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Statistics deleted successfully"));
    }

    @Test
    @WithMockUser
    void getRankingByMetric_ShouldReturnRanking() throws Exception {
        PlayerRankingDTO ranking = new PlayerRankingDTO(1, "user1", 5, 1, Metric.TOTAL_SCORE, 1000);
        when(playerStatisticsService.getRankingByMetric(Metric.TOTAL_SCORE, 10))
            .thenReturn(List.of(ranking));

        mockMvc.perform(get("/api/v1/statistics/ranking")
                .param("metric", "TOTAL_SCORE")
                .param("limit", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].username").value("user1"));
    }

    @Test
    @WithMockUser
    void getRankingByTotalScore_ShouldReturnRanking() throws Exception {
        PlayerRankingDTO ranking = new PlayerRankingDTO(1, "user1", 5, 1, Metric.TOTAL_SCORE, 1000);
        when(playerStatisticsService.getRankingByMetric(Metric.TOTAL_SCORE, 10))
            .thenReturn(List.of(ranking));

        mockMvc.perform(get("/api/v1/statistics/ranking/total-score")
                .param("limit", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].value").value(1000));
    }

    @Test
    @WithMockUser
    void getRankingByHighestScore_ShouldReturnRanking() throws Exception {
        PlayerRankingDTO ranking = new PlayerRankingDTO(1, "user1", 5, 1, Metric.HIGHEST_SCORE, 500);
        when(playerStatisticsService.getRankingByMetric(Metric.HIGHEST_SCORE, 10))
            .thenReturn(List.of(ranking));

        mockMvc.perform(get("/api/v1/statistics/ranking/highest-score"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getRankingByAverageScore_ShouldReturnRanking() throws Exception {
        PlayerRankingDTO ranking = new PlayerRankingDTO(1, "user1", 5, 1, Metric.AVERAGE_SCORE, 100);
        when(playerStatisticsService.getRankingByMetric(Metric.AVERAGE_SCORE, 10))
            .thenReturn(List.of(ranking));

        mockMvc.perform(get("/api/v1/statistics/ranking/average-score"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getRankingByLinesCompleted_ShouldReturnRanking() throws Exception {
        PlayerRankingDTO ranking = new PlayerRankingDTO(1, "user1", 5, 1, Metric.TOTAL_LINES_COMPLETED, 50);
        when(playerStatisticsService.getRankingByMetric(Metric.TOTAL_LINES_COMPLETED, 10))
            .thenReturn(List.of(ranking));

        mockMvc.perform(get("/api/v1/statistics/ranking/lines-completed"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getRankingByPowerCards_ShouldReturnRanking() throws Exception {
        PlayerRankingDTO ranking = new PlayerRankingDTO(1, "user1", 5, 1, Metric.POWER_CARDS_USED, 20);
        when(playerStatisticsService.getRankingByMetric(Metric.POWER_CARDS_USED, 10))
            .thenReturn(List.of(ranking));

        mockMvc.perform(get("/api/v1/statistics/ranking/power-cards"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getRankingByMessages_ShouldReturnRanking() throws Exception {
        PlayerRankingDTO ranking = new PlayerRankingDTO(1, "user1", 5, 1, Metric.MESSAGES_SENT, 100);
        when(playerStatisticsService.getRankingByMetric(Metric.MESSAGES_SENT, 10))
            .thenReturn(List.of(ranking));

        mockMvc.perform(get("/api/v1/statistics/ranking/social"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getRankingByFriends_ShouldReturnRanking() throws Exception {
        PlayerRankingDTO ranking = new PlayerRankingDTO(1, "user1", 5, 1, Metric.FRIENDS_COUNT, 15);
        when(playerStatisticsService.getRankingByMetric(Metric.FRIENDS_COUNT, 10))
            .thenReturn(List.of(ranking));

        mockMvc.perform(get("/api/v1/statistics/ranking/friends"))
            .andExpect(status().isOk());
    }
}
