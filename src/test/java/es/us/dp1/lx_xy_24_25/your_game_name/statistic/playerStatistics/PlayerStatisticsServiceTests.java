package es.us.dp1.lx_xy_24_25.your_game_name.statistic.playerStatistics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import es.us.dp1.lx_xy_24_25.your_game_name.dto.PlayerRankingDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.Metric;

@ExtendWith(MockitoExtension.class)
class PlayerStatisticsServiceTests {

    @Mock
    private PlayerStatisticsRepository repository;

    @InjectMocks
    private PlayerStatisticsService service;

    private Player player;
    private PlayerStatistics stats;

    @BeforeEach
    void setUp() {
        player = new Player();
        player.setId(1);
        player.setUsername("testUser");

        stats = new PlayerStatistics();
        stats.setId(1);
        stats.setPlayer(player);
        stats.setGamesPlayed(10);
        stats.setVictories(5);
        stats.setDefeats(5);
        stats.setTotalPlayTime(100L);
        stats.setTotalScore(1000);
        stats.setCompletedPuzzles(5);
        stats.setTotalPuzzleScore(500);
        stats.setCompletedPuzzles(10);
    }

    @Test
    void findByPlayerId_ShouldReturnStats() {
        when(repository.findByPlayerId(1)).thenReturn(Optional.of(stats));
        PlayerStatistics result = service.findByPlayerId(1);
        assertEquals(stats, result);
    }

    @Test
    void findByPlayerId_ShouldThrowException_WhenNotFound() {
        when(repository.findByPlayerId(99)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.findByPlayerId(99));
    }

    @Test
    void updateStatistics_ShouldUpdateFields() {
        PlayerStatistics newStats = new PlayerStatistics();
        newStats.setGamesPlayed(11);
        newStats.setVictories(6);

        when(repository.findByPlayerId(1)).thenReturn(Optional.of(stats));
        when(repository.save(any(PlayerStatistics.class))).thenAnswer(i -> i.getArguments()[0]);

        PlayerStatistics updated = service.updateStatistics(1, newStats);

        assertEquals(11, updated.getGamesPlayed());
        assertEquals(6, updated.getVictories());
    }

    @Test
    void getRankingByMetric_ShouldReturnCalculatedRanking_WinRate() {
        PlayerStatistics p1 = new PlayerStatistics();
        p1.setPlayer(player);
        p1.setGamesPlayed(20);
        p1.setVictories(10);

        PlayerStatistics p2 = new PlayerStatistics();
        Player player2 = new Player();
        player2.setId(2);
        player2.setUsername("p2");
        p2.setPlayer(player2);
        p2.setGamesPlayed(20);
        p2.setVictories(15);

        when(repository.findAll()).thenReturn(List.of(p1, p2));

        List<PlayerRankingDTO> ranking = service.getRankingByMetric(Metric.WIN_RATE, 10);

        assertEquals(2, ranking.size());
        assertEquals("p2", ranking.get(0).getUsername());
        assertEquals("testUser", ranking.get(1).getUsername());
    }

    @Test
    void getRankingByMetric_ShouldReturnSimpleRanking_TotalScore() {
        Page<PlayerStatistics> page = new PageImpl<>(List.of(stats));
        when(repository.findAll(any(Pageable.class))).thenReturn(page);

        List<PlayerRankingDTO> ranking = service.getRankingByMetric(Metric.TOTAL_SCORE, 10);

        assertEquals(1, ranking.size());
        assertEquals("testUser", ranking.get(0).getUsername());
    }

    @Test
    void toDTO_ShouldMapCorrectly() {
        var dto = service.toDTO(stats);
        assertEquals(stats.getPlayer().getId(), dto.getPlayerId());
        assertEquals(stats.getGamesPlayed(), dto.getGamesPlayed());
    }

    @Test
    void deleteById_ShouldDelete() {
        when(repository.findById(1)).thenReturn(Optional.of(stats));
        service.deleteById(1);
        verify(repository).delete(stats);
    }

    @Test
    void createForPlayer_ShouldCreate() {
        when(repository.save(any(PlayerStatistics.class))).thenReturn(stats);
        PlayerStatistics result = service.createForPlayer(player);
        assertNotNull(result);
        assertEquals(player, result.getPlayer());
    }

    @Test
    void getRankingByMetric_AveragePuzzleScore() {
        PlayerStatistics p1 = new PlayerStatistics();
        p1.setPlayer(player);
        p1.setCompletedPuzzles(2);
        p1.setTotalPuzzleScore(200);

        PlayerStatistics p2 = new PlayerStatistics();
        Player player2 = new Player();
        player2.setId(2);
        player2.setUsername("p2");
        p2.setPlayer(player2);
        p2.setCompletedPuzzles(1);
        p2.setTotalPuzzleScore(50);

        when(repository.findAll()).thenReturn(List.of(p1, p2));

        List<PlayerRankingDTO> ranking = service.getRankingByMetric(Metric.AVERAGE_PUZZLE_SCORE, 10);

        assertEquals(2, ranking.size());
        assertEquals("testUser", ranking.get(0).getUsername()); // 100 avg
        assertEquals("p2", ranking.get(1).getUsername()); // 50 avg
    }
}