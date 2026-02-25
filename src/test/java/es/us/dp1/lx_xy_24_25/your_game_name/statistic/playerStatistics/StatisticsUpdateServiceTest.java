package es.us.dp1.lx_xy_24_25.your_game_name.statistic.playerStatistics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.Color;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.PlayerGameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.PowerName;

@ExtendWith(MockitoExtension.class)
class StatisticsUpdateServiceTest {

    @Mock
    private PlayerStatisticsService playerStatisticsService;

    @InjectMocks
    private StatisticsUpdateService statisticsUpdateService;

    private PlayerStatistics stats;
    private Player player;
    private GameSession gameSession;
    private PlayerGameSession pgs;

    @BeforeEach
    void setUp() {
        player = new Player();
        player.setId(1);
        player.setUsername("testUser");

        stats = new PlayerStatistics();
        stats.setPlayer(player);
        stats.setGamesPlayed(0);
        stats.setVictories(0);
        stats.setDefeats(0);
        stats.setTotalPlayTime(0L);
        stats.setShortestGame(Long.MAX_VALUE);
        stats.setLongestGame(0L);
        stats.setTotalCardsUsed(0);
        stats.setPowerCardsUsed(0);
        stats.setTotalLinesCompleted(0);
        stats.setMaxLineLength(0);
        stats.setAverageLineLength(0.0);
        stats.setCompletedPuzzles(0);
        stats.setTotalPuzzleScore(0);
        stats.setHighestScorePuzzle(0);
        stats.setTotalScore(0);
        stats.setHighestScore(0);
        stats.setLowestScore(Integer.MAX_VALUE);
        stats.setMessagesSent(0);
        stats.setFriendsCount(0);

        gameSession = new GameSession();
        gameSession.setStartTime(LocalDateTime.now().minusMinutes(10));
        gameSession.setEndTime(LocalDateTime.now());

        pgs = new PlayerGameSession();
        pgs.setPlayer(player);

        gameSession.setPlayers(new java.util.HashSet<>());
        gameSession.getPlayers().add(pgs);
    }

    @Test
    void shouldUpdateAfterGameWinner() {
        gameSession.setWinner("testUser");
        when(playerStatisticsService.findByPlayerId(1)).thenReturn(stats);

        statisticsUpdateService.updateAfterGame(gameSession);

        assertEquals(1, stats.getGamesPlayed());
        assertEquals(1, stats.getVictories());
        assertEquals(0, stats.getDefeats());
        assertEquals(600, stats.getTotalPlayTime(), 5);
        verify(playerStatisticsService).save(stats);
    }

    @Test
    void shouldUpdateAfterGameLoser() {
        gameSession.setWinner("otherUser");
        when(playerStatisticsService.findByPlayerId(1)).thenReturn(stats);

        statisticsUpdateService.updateAfterGame(gameSession);

        assertEquals(1, stats.getGamesPlayed());
        assertEquals(0, stats.getVictories());
        assertEquals(1, stats.getDefeats());
        verify(playerStatisticsService).save(stats);
    }

    @Test
    void shouldNotUpdateIfTimesMissing() {
        gameSession.setEndTime(null);
        statisticsUpdateService.updateAfterGame(gameSession);
        verify(playerStatisticsService, times(0)).save(any());
    }

    @Test
    void shouldUpdatePuzzleCompleted() {
        when(playerStatisticsService.findByPlayerId(1)).thenReturn(stats);

        statisticsUpdateService.updatePuzzleCompleted(1, 100);

        assertEquals(1, stats.getCompletedPuzzles());
        assertEquals(100, stats.getTotalPuzzleScore());
        assertEquals(100, stats.getHighestScorePuzzle());
    }

    @Test
    void shouldUpdateScore() {
        when(playerStatisticsService.findByPlayerId(1)).thenReturn(stats);

        statisticsUpdateService.updateScore(1, 500);

        assertEquals(500, stats.getTotalScore());
        assertEquals(500, stats.getHighestScore());
        assertEquals(500, stats.getLowestScore());

        statisticsUpdateService.updateScore(1, 100);
        assertEquals(600, stats.getTotalScore());
        assertEquals(500, stats.getHighestScore());
        assertEquals(100, stats.getLowestScore());
    }

    @Test
    void shouldUpdateMessageSent() {
        when(playerStatisticsService.findByPlayerId(1)).thenReturn(stats);
        statisticsUpdateService.updateMessageSent(1);
        assertEquals(1, stats.getMessagesSent());
    }

    @Test
    void shouldUpdateFriendsCount() {
        when(playerStatisticsService.findByPlayerId(1)).thenReturn(stats);
        statisticsUpdateService.updateFriendsCount(1);
        assertEquals(1, stats.getFriendsCount());
    }

    @Test
    void shouldDecrementFriendsCount() {
        stats.setFriendsCount(5);
        when(playerStatisticsService.findByPlayerId(1)).thenReturn(stats);
        statisticsUpdateService.decrementFriendsCount(1);
        assertEquals(4, stats.getFriendsCount());
    }

    @Test
    void shouldNotDecrementFriendsCountBelowZero() {
        stats.setFriendsCount(0);
        when(playerStatisticsService.findByPlayerId(1)).thenReturn(stats);
        statisticsUpdateService.decrementFriendsCount(1);
        assertEquals(0, stats.getFriendsCount());
    }

    @Test
    void shouldBatchUpdate() {
        when(playerStatisticsService.findByPlayerId(1)).thenReturn(stats);

        Map<String, Object> updates = Map.of(
                "cardsUsed", 5,
                "linesCompleted", 2,
                "score", 100);

        statisticsUpdateService.batchUpdate(1, updates);

        assertEquals(5, stats.getTotalCardsUsed());
        assertEquals(2, stats.getTotalLinesCompleted());
        assertEquals(100, stats.getTotalScore());
    }

    @Test
    void shouldResetPlayerStatistics() {
        stats.setGamesPlayed(10);
        stats.setVictories(5);
        when(playerStatisticsService.findByPlayerId(1)).thenReturn(stats);

        statisticsUpdateService.resetPlayerStatistics(1);

        assertEquals(0, stats.getGamesPlayed());
        assertEquals(0, stats.getVictories());
        assertEquals(Long.MAX_VALUE, stats.getShortestGame());
        assertNull(stats.getFavoriteColor());
    }
}
