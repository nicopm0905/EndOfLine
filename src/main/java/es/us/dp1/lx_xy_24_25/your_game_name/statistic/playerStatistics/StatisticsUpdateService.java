package es.us.dp1.lx_xy_24_25.your_game_name.statistic.playerStatistics;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardTemplate;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Color;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.placedCard.PlacedCard;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameMode;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.PlayerGameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.PowerName;

@Service
public class StatisticsUpdateService {

    private final PlayerStatisticsService playerStatisticsService;

    @Autowired
    public StatisticsUpdateService(PlayerStatisticsService playerStatisticsService) {
        this.playerStatisticsService = playerStatisticsService;
    }

    @Transactional
    public void updateAfterGame(GameSession gameSession) {
        if (gameSession.getStartTime() == null || gameSession.getEndTime() == null) {
            return;
        }
        Duration duration = Duration.between(gameSession.getStartTime(), gameSession.getEndTime());
        long durationInSeconds = duration.getSeconds();

        for (PlayerGameSession pgs : gameSession.getPlayers()) {
            updatePlayerStatistics(pgs, gameSession, durationInSeconds);
        }
    }

    @Transactional
    private void updatePlayerStatistics(PlayerGameSession pgs, GameSession gameSession, long durationInSeconds) {
        PlayerStatistics stats = playerStatisticsService.findByPlayerId(pgs.getPlayer().getId());

        stats.setGamesPlayed(stats.getGamesPlayed() + 1);

        boolean isWinner = isWinner(pgs, gameSession);
        if (isWinner) {
            stats.setVictories(stats.getVictories() + 1);
        } else {
            stats.setDefeats(stats.getDefeats() + 1);
        }

        stats.setTotalPlayTime(stats.getTotalPlayTime() + durationInSeconds);

        if (durationInSeconds < stats.getShortestGame()) {
            stats.setShortestGame(durationInSeconds);
        }
        if (durationInSeconds > stats.getLongestGame()) {
            stats.setLongestGame(durationInSeconds);
        }
        List<PlacedCard> placedCards = pgs.getPlacedCards();
        int cardsUsed = placedCards.size();
        stats.setTotalCardsUsed(stats.getTotalCardsUsed() + cardsUsed);
        Map<Color, Integer> colorCounts = new HashMap<>();
        Map<PowerName, Integer> strictPowerCounts = new HashMap<>();

        int powerCardsUsedInGame = 0;

        for (PlacedCard card : placedCards) {
            CardTemplate template = card.getTemplate();
            if (template.getColor() != null) {
                colorCounts.merge(template.getColor(), 1, Integer::sum);
            }
            if (template.getPower() != null) {
                powerCardsUsedInGame++;
                strictPowerCounts.merge(template.getPower(), 1, Integer::sum);
            }
        }
        stats.setPowerCardsUsed(stats.getPowerCardsUsed() + powerCardsUsedInGame);
        updateMostUsedPower(stats, strictPowerCounts);
        updateFavoriteColor(stats, colorCounts);
        int lineLength = cardsUsed;
        stats.setTotalLinesCompleted(stats.getTotalLinesCompleted() + 1);

        if (lineLength > stats.getMaxLineLength()) {
            stats.setMaxLineLength(lineLength);
        }

        int totalLines = stats.getTotalLinesCompleted();
        if (totalLines > 0) {
            double oldAvg = stats.getAverageLineLength();
            double newAvg = ((oldAvg * (totalLines - 1)) + lineLength) / totalLines;
            stats.setAverageLineLength(newAvg);
        }

        if (isPuzzleOrSolitaire(gameSession.getGameMode())) {
            if (isWinner && gameSession.getWinnerScore() != null) {
                int score = gameSession.getWinnerScore();

                stats.setCompletedPuzzles(stats.getCompletedPuzzles() + 1);
                stats.setTotalPuzzleScore(stats.getTotalPuzzleScore() + score);
                stats.setTotalScore(stats.getTotalScore() + score);

                if (score > stats.getHighestScorePuzzle()) {
                    stats.setHighestScorePuzzle(score);
                }

                updateScoreRecords(stats, score);
            }
        } else {
            if (isWinner && gameSession.getWinnerScore() != null) {
                int score = gameSession.getWinnerScore();
                stats.setTotalScore(stats.getTotalScore() + score);
                updateScoreRecords(stats, score);
            }
        }

        if (pgs.getMessages() != null) {
            stats.setMessagesSent(stats.getMessagesSent() + pgs.getMessages().size());
        }
        playerStatisticsService.save(stats);
    }

    private void updateMostUsedPower(PlayerStatistics stats, Map<PowerName, Integer> gameCounts) {
        if (gameCounts.isEmpty())
            return;

        Map.Entry<PowerName, Integer> maxEntry = gameCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        if (maxEntry == null)
            return;

        if (stats.getPowerMostUsed() == null) {
            stats.setPowerMostUsed(maxEntry.getKey());
        }
    }

    private void updateFavoriteColor(PlayerStatistics stats, Map<Color, Integer> gameCounts) {
        if (gameCounts.isEmpty())
            return;

        if (stats.getFavoriteColor() != null) {
            Integer count = gameCounts.get(stats.getFavoriteColor());
            if (count != null) {
                stats.setFavoriteColorUses(stats.getFavoriteColorUses() + count);
            }
        } else {
            Map.Entry<Color, Integer> maxEntry = gameCounts.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .orElse(null);

            if (maxEntry != null) {
                stats.setFavoriteColor(maxEntry.getKey());
                stats.setFavoriteColorUses(maxEntry.getValue());
            }
        }
    }

    private void updateScoreRecords(PlayerStatistics stats, int score) {
        if (score > stats.getHighestScore()) {
            stats.setHighestScore(score);
        }
        if (score < stats.getLowestScore()) {
            stats.setLowestScore(score);
        }
    }

    private boolean isWinner(PlayerGameSession pgs, GameSession gameSession) {
        String winner = gameSession.getWinner();
        if (winner == null)
            return false;
        return winner.contains(pgs.getPlayer().getUsername());
    }

    private boolean isPuzzleOrSolitaire(GameMode mode) {
        return mode == GameMode.SOLITAIRE || mode == GameMode.SOLITARY_PUZZLE;
    }

    @Transactional
    public void updatePuzzleCompleted(Integer playerId, int score) {
        PlayerStatistics stats = playerStatisticsService.findByPlayerId(playerId);
        stats.setCompletedPuzzles(stats.getCompletedPuzzles() + 1);
        stats.setTotalPuzzleScore(stats.getTotalPuzzleScore() + score);
        if (score > stats.getHighestScorePuzzle()) {
            stats.setHighestScorePuzzle(score);
        }
        playerStatisticsService.save(stats);
    }

    @Transactional
    public void updateScore(Integer playerId, int gameScore) {
        PlayerStatistics stats = playerStatisticsService.findByPlayerId(playerId);
        stats.setTotalScore(stats.getTotalScore() + gameScore);
        updateScoreRecords(stats, gameScore);
        playerStatisticsService.save(stats);
    }

    @Transactional
    public void updateMessageSent(Integer playerId) {
        PlayerStatistics stats = playerStatisticsService.findByPlayerId(playerId);
        stats.setMessagesSent(stats.getMessagesSent() + 1);
        playerStatisticsService.save(stats);
    }

    @Transactional
    public void updateFriendsCount(Integer playerId) {
        PlayerStatistics stats = playerStatisticsService.findByPlayerId(playerId);
        stats.setFriendsCount(stats.getFriendsCount() + 1);
        playerStatisticsService.save(stats);
    }

    @Transactional
    public void decrementFriendsCount(Integer playerId) {
        PlayerStatistics stats = playerStatisticsService.findByPlayerId(playerId);
        if (stats.getFriendsCount() > 0) {
            stats.setFriendsCount(stats.getFriendsCount() - 1);
            playerStatisticsService.save(stats);
        }
    }

    @Transactional
    public void batchUpdate(Integer playerId, Map<String, Object> updates) {
        PlayerStatistics stats = playerStatisticsService.findByPlayerId(playerId);
        updates.forEach((key, value) -> {
            switch (key) {
                case "cardsUsed":
                    if (value instanceof Integer)
                        stats.setTotalCardsUsed(stats.getTotalCardsUsed() + (Integer) value);
                    break;
                case "linesCompleted":
                    if (value instanceof Integer)
                        stats.setTotalLinesCompleted(stats.getTotalLinesCompleted() + (Integer) value);
                    break;
                case "score":
                    if (value instanceof Integer)
                        updateScore(playerId, (Integer) value);
                    break;
            }
        });
        playerStatisticsService.save(stats);
    }

    @Transactional
    public void resetPlayerStatistics(Integer playerId) {
        PlayerStatistics stats = playerStatisticsService.findByPlayerId(playerId);

        stats.setGamesPlayed(0);
        stats.setVictories(0);
        stats.setDefeats(0);
        stats.setTotalPlayTime(0L);
        stats.setShortestGame(Long.MAX_VALUE);
        stats.setLongestGame(0L);
        stats.setTotalCardsUsed(0);
        stats.setPowerCardsUsed(0);
        stats.setMaxLineLength(0);
        stats.setTotalLinesCompleted(0);
        stats.setAverageLineLength(0.0);
        stats.setCompletedPuzzles(0);
        stats.setHighestScorePuzzle(0);
        stats.setTotalPuzzleScore(0);
        stats.setTotalScore(0);
        stats.setHighestScore(0);
        stats.setLowestScore(Integer.MAX_VALUE);
        stats.setMessagesSent(0);
        stats.setFriendsCount(0);
        stats.setFavoriteColor(null);
        stats.setFavoriteColorUses(0);
        stats.setPowerMostUsed(null);

        playerStatisticsService.save(stats);
    }
}