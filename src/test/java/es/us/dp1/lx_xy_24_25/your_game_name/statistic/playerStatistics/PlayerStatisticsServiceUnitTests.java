package es.us.dp1.lx_xy_24_25.your_game_name.statistic.playerStatistics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
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

import es.us.dp1.lx_xy_24_25.your_game_name.cards.Color;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.PlayerRankingDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.PlayerStatisticsDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.Metric;

@ExtendWith(MockitoExtension.class)
class PlayerStatisticsServiceUnitTests {

    @Mock
    private PlayerStatisticsRepository playerStatisticsRepository;

    @InjectMocks
    private PlayerStatisticsService playerStatisticsService;

    private Player player;
    private PlayerStatistics stats;

    @BeforeEach
    void setUp() {
        player = new Player();
        player.setId(1);
        player.setUsername("testUser");
        player.setAvatarId(1);

        stats = new PlayerStatistics();
        stats.setId(100);
        stats.setPlayer(player);
        stats.setGamesPlayed(10);
        stats.setVictories(5);
        stats.setDefeats(5);
        stats.setTotalScore(1000);
        stats.setCompletedPuzzles(2);
        stats.setTotalPuzzleScore(200);
    }

    @Test
    void testFindByIdSuccess() {
        when(playerStatisticsRepository.findById(100)).thenReturn(Optional.of(stats));
        PlayerStatistics found = playerStatisticsService.findById(100);
        assertEquals(100, found.getId());
    }

    @Test
    void testFindByIdNotFound() {
        when(playerStatisticsRepository.findById(999)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> playerStatisticsService.findById(999));
    }

    @Test
    void testFindByPlayerIdSuccess() {
        when(playerStatisticsRepository.findByPlayerId(1)).thenReturn(Optional.of(stats));
        PlayerStatistics found = playerStatisticsService.findByPlayerId(1);
        assertEquals(1, found.getPlayer().getId());
    }

    @Test
    void testFindByPlayerIdNotFound() {
        when(playerStatisticsRepository.findByPlayerId(999)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> playerStatisticsService.findByPlayerId(999));
    }

    @Test
    void testCreateForPlayer() {
        when(playerStatisticsRepository.save(any(PlayerStatistics.class))).thenAnswer(invocation -> {
            PlayerStatistics s = invocation.getArgument(0);
            s.setId(200);
            return s;
        });

        PlayerStatistics created = playerStatisticsService.createForPlayer(player);

        assertNotNull(created);
        assertEquals(200, created.getId());
        assertEquals(player, created.getPlayer());
    }

    @Test
    void testSave() {
        when(playerStatisticsRepository.save(stats)).thenReturn(stats);
        PlayerStatistics saved = playerStatisticsService.save(stats);
        assertEquals(stats, saved);
    }

    @Test
    void testToDTO() {
        stats.setTotalPlayTime(1200L);
        stats.setShortestGame(100L);
        stats.setLongestGame(300L);
        stats.setTotalCardsUsed(50);
        stats.setPowerCardsUsed(5);
        stats.setFavoriteColor(Color.RED);
        stats.setMaxLineLength(10);
        stats.setTotalLinesCompleted(20);
        stats.setHighestScorePuzzle(100);
        stats.setMessagesSent(10);
        stats.setFriendsCount(5);

        PlayerStatisticsDTO dto = playerStatisticsService.toDTO(stats);

        assertEquals(player.getId(), dto.getPlayerId());
        assertEquals(player.getUsername(), dto.getUsername());
        assertEquals(10, dto.getGamesPlayed());
        assertEquals(5, dto.getVictories());
        assertEquals(5, dto.getDefeats());
        assertEquals(50.0, dto.getWinRate());
        assertEquals(1200L, dto.getTotalPlayTime());
        assertEquals(120.0, dto.getAverageDuration());
        assertEquals(100L, dto.getShortestGame());
        assertEquals(300L, dto.getLongestGame());
        assertEquals(50, dto.getTotalCardsUsed());
        assertEquals(5, dto.getPowerCardsUsed());
        assertEquals(Color.RED, dto.getFavoriteColor());
        assertEquals(5.0, dto.getAverageCardsPerGame());
        assertEquals(10, dto.getMaxLineLength());
        assertEquals(20, dto.getTotalLinesCompleted());
        assertEquals(1000, dto.getTotalScore());
        assertEquals(2, dto.getCompletedPuzzles());
        assertEquals(100, dto.getHighestScorePuzzle());
        assertEquals(10, dto.getMessagesSent());
        assertEquals(5, dto.getFriendsCount());
    }

    @Test
    void testUpdateStatistics() {
        when(playerStatisticsRepository.findByPlayerId(1)).thenReturn(Optional.of(stats));
        when(playerStatisticsRepository.save(any(PlayerStatistics.class))).thenAnswer(i -> i.getArgument(0));

        PlayerStatistics updateData = new PlayerStatistics();
        updateData.setGamesPlayed(20);
        updateData.setVictories(10);
        updateData.setDefeats(10);
        updateData.setTotalScore(2000);
        updateData.setFavoriteColor(Color.BLUE);

        PlayerStatistics updated = playerStatisticsService.updateStatistics(1, updateData);

        assertEquals(20, updated.getGamesPlayed());
        assertEquals(10, updated.getVictories());
        assertEquals(2000, updated.getTotalScore());
        assertEquals(Color.BLUE, updated.getFavoriteColor());
    }

    @Test
    void testDeleteById() {
        when(playerStatisticsRepository.findById(100)).thenReturn(Optional.of(stats));
        playerStatisticsService.deleteById(100);
        verify(playerStatisticsRepository).delete(stats);
    }

    @Test
    void testGetRankingByMetric_Simple() {
        Page<PlayerStatistics> page = new PageImpl<>(List.of(stats));
        when(playerStatisticsRepository.findAll(any(Pageable.class))).thenReturn(page);

        List<PlayerRankingDTO> ranking = playerStatisticsService.getRankingByMetric(Metric.TOTAL_SCORE, 10);

        assertNotNull(ranking);
        assertEquals(1, ranking.size());
        assertEquals("testUser", ranking.get(0).getUsername());
        assertEquals(1000, ranking.get(0).getValue());
        assertEquals(Metric.TOTAL_SCORE, ranking.get(0).getMetric());
    }

    @Test
    void testGetRankingByMetric_Calculated_WinRate() {
        Player p2 = new Player();
        p2.setId(2);
        p2.setUsername("noob");
        PlayerStatistics statsLowGames = new PlayerStatistics();
        statsLowGames.setPlayer(p2);
        statsLowGames.setGamesPlayed(2);
        statsLowGames.setVictories(2);

        when(playerStatisticsRepository.findAll()).thenReturn(List.of(stats, statsLowGames));

        List<PlayerRankingDTO> ranking = playerStatisticsService.getRankingByMetric(Metric.WIN_RATE, 10);

        assertEquals(1, ranking.size());
        assertEquals("testUser", ranking.get(0).getUsername());
        assertEquals(50.0, ranking.get(0).getValue());
    }

    @Test
    void testGetRankingByMetric_Calculated_AverageScore() {
        when(playerStatisticsRepository.findAll()).thenReturn(List.of(stats));

        List<PlayerRankingDTO> ranking = playerStatisticsService.getRankingByMetric(Metric.AVERAGE_SCORE, 10);

        assertEquals(1, ranking.size());
        assertEquals(100.0, ranking.get(0).getValue().doubleValue(), 0.1);
    }

    @Test
    void testGetRankingByMetric_Calculated_AveragePuzzleScore() {
        when(playerStatisticsRepository.findAll()).thenReturn(List.of(stats));

        List<PlayerRankingDTO> ranking = playerStatisticsService.getRankingByMetric(Metric.AVERAGE_PUZZLE_SCORE, 10);

        assertEquals(1, ranking.size());
        assertEquals(100.0, ranking.get(0).getValue().doubleValue(), 0.1);
    }
}
