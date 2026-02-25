package es.us.dp1.lx_xy_24_25.your_game_name.statistic.playerStatistics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardTemplate;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Color;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.placedCard.PlacedCard;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameMode;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.PlayerGameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;

@ExtendWith(MockitoExtension.class)
class StatisticsUpdateServiceTests {

    @Mock
    private PlayerStatisticsService playerStatisticsService;

    @InjectMocks
    private StatisticsUpdateService service;

    private Player player;
    private PlayerStatistics stats;
    private GameSession gameSession;
    private PlayerGameSession pgs;

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
        stats.setShortestGame(Long.MAX_VALUE);
        stats.setLongestGame(0L);

        pgs = new PlayerGameSession();
        pgs.setId(1);
        pgs.setPlayer(player);
        pgs.setPlacedCards(new ArrayList<>());

        gameSession = new GameSession();
        gameSession.setId(1);
        gameSession.setStartTime(LocalDateTime.now().minusMinutes(10));
        gameSession.setEndTime(LocalDateTime.now());
        gameSession.setPlayers(Set.of(pgs));
        gameSession.setGameMode(GameMode.VERSUS);
    }

    @Test
    void updateAfterGame_shouldUpdateStats_Winner() {
        gameSession.setWinner("testUser");

        when(playerStatisticsService.findByPlayerId(1)).thenReturn(stats);

        service.updateAfterGame(gameSession);

        assertEquals(11, stats.getGamesPlayed());
        assertEquals(6, stats.getVictories());
        verify(playerStatisticsService).save(stats);
    }

    @Test
    void updateAfterGame_shouldUpdateStats_Loser() {
        gameSession.setWinner("otherUser");

        when(playerStatisticsService.findByPlayerId(1)).thenReturn(stats);

        service.updateAfterGame(gameSession);

        assertEquals(11, stats.getGamesPlayed());
        assertEquals(6, stats.getDefeats());
        verify(playerStatisticsService).save(stats);
    }

    @Test
    void updateAfterGame_shouldUpdateCardStats() {
        gameSession.setWinner("testUser");

        CardTemplate t1 = new CardTemplate();
        t1.setColor(Color.RED);
        PlacedCard c1 = new PlacedCard();
        c1.setTemplate(t1);
        pgs.getPlacedCards().add(c1);

        when(playerStatisticsService.findByPlayerId(1)).thenReturn(stats);

        service.updateAfterGame(gameSession);

        assertEquals(1, stats.getTotalCardsUsed());
        assertEquals(Color.RED, stats.getFavoriteColor());
        verify(playerStatisticsService).save(stats);
    }

    @Test
    void updatePuzzleCompleted_shouldUpdate() {
        when(playerStatisticsService.findByPlayerId(1)).thenReturn(stats);

        service.updatePuzzleCompleted(1, 100);

        assertEquals(1, stats.getCompletedPuzzles());
        assertEquals(100, stats.getTotalPuzzleScore());
        assertEquals(100, stats.getHighestScorePuzzle());
        verify(playerStatisticsService).save(stats);
    }

    @Test
    void updateFriendsCount_shouldIncrement() {
        when(playerStatisticsService.findByPlayerId(1)).thenReturn(stats);

        service.updateFriendsCount(1);

        assertEquals(1, stats.getFriendsCount());
        verify(playerStatisticsService).save(stats);
    }

    @Test
    void decrementFriendsCount_shouldDecrement() {
        stats.setFriendsCount(5);
        when(playerStatisticsService.findByPlayerId(1)).thenReturn(stats);

        service.decrementFriendsCount(1);

        assertEquals(4, stats.getFriendsCount());
        verify(playerStatisticsService).save(stats);
    }

    @Test
    void updateAfterGame_Solitaire_ShouldUpdatePuzzleStats() {
        gameSession.setGameMode(GameMode.SOLITAIRE);
        gameSession.setWinner("testUser");
        gameSession.setWinnerScore(500);

        when(playerStatisticsService.findByPlayerId(1)).thenReturn(stats);

        service.updateAfterGame(gameSession);

        assertEquals(1, stats.getCompletedPuzzles());
        assertEquals(500, stats.getTotalPuzzleScore());
        assertEquals(500, stats.getHighestScorePuzzle());
        verify(playerStatisticsService).save(stats);
    }

    @Test
    void updateAfterGame_Versus_ShouldUpdateTotalScore() {
        gameSession.setGameMode(GameMode.VERSUS);
        gameSession.setWinner("testUser");
        gameSession.setWinnerScore(300);

        when(playerStatisticsService.findByPlayerId(1)).thenReturn(stats);

        service.updateAfterGame(gameSession);

        assertEquals(300, stats.getTotalScore()); // 0 + 300
    }

    @Test
    void updateMessageSent_ShouldIncrement() {
        when(playerStatisticsService.findByPlayerId(1)).thenReturn(stats);
        service.updateMessageSent(1);
        assertEquals(1, stats.getMessagesSent());
        verify(playerStatisticsService).save(stats);
    }

    @Test
    void batchUpdate_ShouldUpdateFields() {
        when(playerStatisticsService.findByPlayerId(1)).thenReturn(stats);

        java.util.Map<String, Object> updates = new java.util.HashMap<>();
        updates.put("cardsUsed", 5);
        updates.put("linesCompleted", 2);
        updates.put("score", 100);

        service.batchUpdate(1, updates);

        assertEquals(5, stats.getTotalCardsUsed());
        assertEquals(2, stats.getTotalLinesCompleted());
        assertEquals(100, stats.getTotalScore());
        verify(playerStatisticsService, times(2)).save(stats);
    }

    @Test
    void resetPlayerStatistics_ShouldResetFields() {
        when(playerStatisticsService.findByPlayerId(1)).thenReturn(stats);

        service.resetPlayerStatistics(1);

        assertEquals(0, stats.getGamesPlayed());
        assertEquals(0, stats.getVictories());
        assertEquals(0, stats.getDefeats());
        assertEquals(0L, stats.getTotalPlayTime());
        assertEquals(0, stats.getTotalScore());
        verify(playerStatisticsService).save(stats);
    }
}
