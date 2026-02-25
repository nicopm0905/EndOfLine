package es.us.dp1.lx_xy_24_25.your_game_name.statistic.playerAchievement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.us.dp1.lx_xy_24_25.your_game_name.dto.PlayerAchievementDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.Metric;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.achievements.Achievement;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.achievements.AchievementService;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.playerStatistics.PlayerStatistics;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.playerStatistics.PlayerStatisticsRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class PlayerAchievementServiceTests {

    @Mock
    private PlayerAchievementRepository playerAchievementRepository;
    @Mock
    private AchievementService achievementService;
    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private PlayerStatisticsRepository playerStatisticsRepository;

    @InjectMocks
    private PlayerAchievementService service;

    private Player player;
    private Achievement achievement;
    private PlayerAchievement playerAchievement;
    private PlayerStatistics stats;

    @BeforeEach
    void setUp() {
        player = new Player();
        player.setId(1);
        player.setUsername("user1");

        achievement = new Achievement();
        achievement.setId(1);
        achievement.setMetric(Metric.GAMES_PLAYED);
        achievement.setThreshold(10);

        playerAchievement = new PlayerAchievement();
        playerAchievement.setId(1);
        playerAchievement.setPlayer(player);
        playerAchievement.setAchievement(achievement);
        playerAchievement.setProgress(5);
        playerAchievement.setCompleted(false);

        stats = new PlayerStatistics();
        stats.setPlayer(player);
        stats.setGamesPlayed(5);
        stats.setTotalPlayTime(100L);
    }

    @Test
    void getOrCreatePlayerAchievement_ShouldCreateNew() {
        when(playerAchievementRepository.findByPlayerId(1)).thenReturn(List.of());
        when(playerRepository.findById(1)).thenReturn(Optional.of(player));
        when(achievementService.findById(1)).thenReturn(achievement);
        when(playerAchievementRepository.save(any(PlayerAchievement.class))).thenAnswer(i -> i.getArguments()[0]);

        PlayerAchievement result = service.getOrCreatePlayerAchievement(1, 1);

        assertNotNull(result);
        assertEquals(0, result.getProgress());
        assertEquals(achievement, result.getAchievement());
    }

    @Test
    void updateProgress_ShouldUnlockAchievement() {
        when(playerAchievementRepository.findByPlayerId(1)).thenReturn(List.of(playerAchievement));
        when(achievementService.findById(1)).thenReturn(achievement);
        when(playerAchievementRepository.save(any(PlayerAchievement.class))).thenAnswer(i -> i.getArguments()[0]);

        PlayerAchievement result = service.updateProgress(1, 1, 10);

        assertTrue(result.getCompleted());
        assertNotNull(result.getUnlockedAt());
    }

    @Test
    void getPlayerProgress_ShouldCalculateGamesPlayed() {
        stats.setGamesPlayed(8);
        when(achievementService.findAll()).thenReturn(List.of(achievement));
        when(playerStatisticsRepository.findByPlayerId(1)).thenReturn(Optional.of(stats));
        when(playerAchievementRepository.findByPlayerId(1)).thenReturn(List.of(playerAchievement));
        when(achievementService.findById(1)).thenReturn(achievement);

        List<PlayerAchievementDTO> result = service.getPlayerProgress(1);

        assertEquals(1, result.size());
        assertEquals(8, result.get(0).getProgress());
    }

    @Test
    void findByPlayerId_ShouldReturnList() {
        when(playerAchievementRepository.findByPlayerId(1)).thenReturn(List.of(playerAchievement));
        List<PlayerAchievement> result = service.findByPlayerId(1);
        assertEquals(1, result.size());
    }

    @Test
    void findById_ShouldReturnAchievement() {
        when(playerAchievementRepository.findById(1)).thenReturn(Optional.of(playerAchievement));
        PlayerAchievement result = service.findById(1);
        assertEquals(playerAchievement, result);
    }

    @Test
    void findById_ShouldThrowException_WhenNotFound() {
        when(playerAchievementRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.findById(99));
    }

    @Test
    void findByPlayerIdAndAchievementId_ShouldReturnAchievement() {
        when(playerAchievementRepository.findByPlayerId(1)).thenReturn(List.of(playerAchievement));

        PlayerAchievement result = service.findByPlayerIdAndAchievementId(1, 1);

        assertEquals(playerAchievement, result);
    }

    @Test
    void findByPlayerIdAndAchievementId_ShouldThrowException_WhenNotFound() {
        when(playerAchievementRepository.findByPlayerId(1)).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> service.findByPlayerIdAndAchievementId(1, 1));
    }

    @Test
    void save_ShouldReturnSavedAchievement() {
        when(playerAchievementRepository.save(any(PlayerAchievement.class))).thenReturn(playerAchievement);
        PlayerAchievement result = service.save(playerAchievement);
        assertEquals(playerAchievement, result);
    }

    @Test
    void deleteById_ShouldDeleteAchievement() {
        when(playerAchievementRepository.findById(1)).thenReturn(Optional.of(playerAchievement));
        service.deleteById(1);
        verify(playerAchievementRepository).delete(playerAchievement);
    }

    @Test
    void findCompletedByPlayerId_ShouldReturnCompleted() {
        playerAchievement.setCompleted(true);
        when(playerAchievementRepository.findByPlayerId(1)).thenReturn(List.of(playerAchievement));

        List<PlayerAchievement> result = service.findCompletedByPlayerId(1);

        assertEquals(1, result.size());
        assertTrue(result.get(0).getCompleted());
    }

    @Test
    void findByAchievementId_ShouldReturnList() {
        when(playerAchievementRepository.findByAchievementId(1)).thenReturn(List.of(playerAchievement));
        List<PlayerAchievement> result = service.findByAchievementId(1);
        assertEquals(1, result.size());
    }

    @Test
    void modifyPlayerAchievement_ShouldUpdateFields() {
        PlayerAchievement newInfo = new PlayerAchievement();
        newInfo.setProgress(100);
        newInfo.setCompleted(true);

        when(playerAchievementRepository.findById(1)).thenReturn(Optional.of(playerAchievement));
        when(playerAchievementRepository.save(any(PlayerAchievement.class))).thenAnswer(i -> i.getArguments()[0]);

        PlayerAchievement result = service.modifyPlayerAchievement(1, newInfo);

        assertEquals(100, result.getProgress());
        assertTrue(result.getCompleted());
    }

    @Test
    void getAchievementProgress_ShouldReturnDTOs() {
        when(achievementService.findById(1)).thenReturn(achievement);
        when(playerRepository.findAll()).thenReturn(List.of(player));
        when(playerStatisticsRepository.findByPlayerId(1)).thenReturn(Optional.of(stats));
        when(playerAchievementRepository.findByPlayerId(1)).thenReturn(List.of());
        // Mock getOrCreatePlayerAchievement internals (save)
        when(playerRepository.findById(1)).thenReturn(Optional.of(player));
        when(playerAchievementRepository.save(any(PlayerAchievement.class))).thenAnswer(i -> i.getArguments()[0]);

        List<PlayerAchievementDTO> result = service.getAchievementProgress(1);

        assertEquals(1, result.size());
        assertEquals("user1", result.get(0).getUsername());
    }

    @Test
    void calculateProgress_ShouldHandleMetrics() {
        Achievement ach2 = new Achievement();
        ach2.setId(2);
        ach2.setMetric(Metric.AVERAGE_DURATION);
        ach2.setThreshold(20);

        when(achievementService.findAll()).thenReturn(List.of(ach2));
        when(playerStatisticsRepository.findByPlayerId(1)).thenReturn(Optional.of(stats));
        // Need to handle the getOrCreate internals which are called inside
        // getPlayerProgress -> updateProgress -> getOrCreate
        when(playerAchievementRepository.findByPlayerId(1)).thenReturn(List.of());
        when(playerRepository.findById(1)).thenReturn(Optional.of(player));
        when(achievementService.findById(2)).thenReturn(ach2);
        when(playerAchievementRepository.save(any(PlayerAchievement.class))).thenAnswer(i -> i.getArguments()[0]);

        List<PlayerAchievementDTO> result = service.getPlayerProgress(1);

        assertEquals(20, result.get(0).getProgress());
    }
}