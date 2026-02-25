package es.us.dp1.lx_xy_24_25.your_game_name.statistic.playerStatistics;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.Metric;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.PlayerRankingDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.PlayerStatisticsDTO;
import jakarta.transaction.Transactional;

@Service
public class PlayerStatisticsService {

    private PlayerStatisticsRepository playerStatisticsRepository;

    @Autowired
    public PlayerStatisticsService(PlayerStatisticsRepository playerStatisticsRepository) {
        this.playerStatisticsRepository = playerStatisticsRepository;
    }

    @Transactional
    public PlayerStatistics findById(Integer id) {
        return playerStatisticsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PlayerStatistics", "id", id));
    }

    @Transactional
    public PlayerStatistics findByPlayerId(Integer playerId) {
        return playerStatisticsRepository.findByPlayerId(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("PlayerStatictis", "playerId", playerId));
    }

    @Transactional
    public PlayerStatistics save(PlayerStatistics playerStatistics) {
        return playerStatisticsRepository.save(playerStatistics);
    }

    @Transactional
    public PlayerStatistics createForPlayer(Player player) {
        PlayerStatistics stats = new PlayerStatistics();
        stats.setPlayer(player);
        return playerStatisticsRepository.save(stats);
    }

    @Transactional
    public PlayerStatistics updateStatistics(Integer playerId, PlayerStatistics updatedStats) {
        PlayerStatistics existing = findByPlayerId(playerId);

        existing.setGamesPlayed(updatedStats.getGamesPlayed());
        existing.setVictories(updatedStats.getVictories());
        existing.setDefeats(updatedStats.getDefeats());
        existing.setTotalPlayTime(updatedStats.getTotalPlayTime());
        existing.setShortestGame(updatedStats.getShortestGame());
        existing.setLongestGame(updatedStats.getLongestGame());

        existing.setTotalCardsUsed(updatedStats.getTotalCardsUsed());
        existing.setPowerCardsUsed(updatedStats.getPowerCardsUsed());
        existing.setPowerMostUsed(updatedStats.getPowerMostUsed());
        existing.setFavoriteColor(updatedStats.getFavoriteColor());
        existing.setFavoriteColorUses(updatedStats.getFavoriteColorUses());

        existing.setMaxLineLength(updatedStats.getMaxLineLength());
        existing.setAverageLineLength(updatedStats.getAverageLineLength());

        existing.setHighestScorePuzzle(updatedStats.getHighestScorePuzzle());
        existing.setCompletedPuzzles(updatedStats.getCompletedPuzzles());
        existing.setTotalPuzzleScore(updatedStats.getTotalPuzzleScore());

        existing.setTotalScore(updatedStats.getTotalScore());
        existing.setHighestScore(updatedStats.getHighestScore());
        existing.setLowestScore(updatedStats.getLowestScore());

        existing.setMessagesSent(updatedStats.getMessagesSent());
        existing.setFriendsCount(updatedStats.getFriendsCount());

        return playerStatisticsRepository.save(existing);
    }

    @Transactional
    public void deleteById(Integer id) {
        PlayerStatistics stats = findById(id);
        playerStatisticsRepository.delete(stats);
    }

    @Transactional
    public List<PlayerRankingDTO> getRankingByMetric(Metric metric, Integer limit) {
        if (isCalculatedMetric(metric)) {
            return getRankingForCalculatedMetric(metric, limit);
        }

        String fieldName = getFieldNameFromMetric(metric);
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, fieldName));
        List<PlayerStatistics> stats = playerStatisticsRepository.findAll(pageable).getContent();
        return buildRanking(stats, metric);
    }

    public PlayerStatisticsDTO toDTO(PlayerStatistics stats) {
        var dto = new PlayerStatisticsDTO();
        dto.setPlayerId(stats.getPlayer().getId());
        dto.setUsername(stats.getPlayer().getUsername());
        dto.setGamesPlayed(stats.getGamesPlayed());
        dto.setVictories(stats.getVictories());
        dto.setDefeats(stats.getDefeats());
        dto.setWinRate(stats.getWinRate());
        dto.setTotalPlayTime(stats.getTotalPlayTime());
        dto.setAverageDuration(stats.getAverageDuration());
        dto.setShortestGame(stats.getShortestGame());
        dto.setLongestGame(stats.getLongestGame());
        dto.setTotalCardsUsed(stats.getTotalCardsUsed());
        dto.setPowerCardsUsed(stats.getPowerCardsUsed());
        dto.setPowerMostUsed(stats.getPowerMostUsed());
        dto.setFavoriteColor(stats.getFavoriteColor());
        dto.setAverageCardsPerGame(stats.getAverageCardsPerGame());
        dto.setMaxLineLength(stats.getMaxLineLength());
        dto.setTotalLinesCompleted(stats.getTotalLinesCompleted());
        dto.setAverageLineLength(stats.getAverageLineLength());
        dto.setTotalScore(stats.getTotalScore());
        dto.setHighestScore(stats.getHighestScore());
        dto.setLowestScore(stats.getLowestScore());
        dto.setAverageScore(stats.getAverageScore());
        dto.setCompletedPuzzles(stats.getCompletedPuzzles());
        dto.setHighestScorePuzzle(stats.getHighestScorePuzzle());
        dto.setMessagesSent(stats.getMessagesSent());
        dto.setFriendsCount(stats.getFriendsCount());
        return dto;
    }

    @Transactional
    private List<PlayerRankingDTO> getRankingForCalculatedMetric(Metric metric, Integer limit) {
        List<PlayerStatistics> allStats = playerStatisticsRepository.findAll();

        if (metric == Metric.WIN_RATE) {
            allStats = allStats.stream()
                    .filter(s -> s.getGamesPlayed() >= 10)
                    .collect(Collectors.toList());
        }

        List<PlayerStatistics> sortedStats = allStats.stream()
                .sorted((s1, s2) -> {
                    Number v1 = getValueFromStatistic(s1, metric);
                    Number v2 = getValueFromStatistic(s2, metric);
                    return Double.compare(v2.doubleValue(), v1.doubleValue());
                })
                .limit(limit)
                .collect(Collectors.toList());

        return buildRanking(sortedStats, metric);
    }

    private boolean isCalculatedMetric(Metric metric) {
        return metric == Metric.WIN_RATE ||
                metric == Metric.AVERAGE_DURATION ||
                metric == Metric.AVERAGE_CARDS_PER_GAME ||
                metric == Metric.AVERAGE_PUZZLE_SCORE ||
                metric == Metric.AVERAGE_SCORE;
    }

    private String getFieldNameFromMetric(Metric metric) {
        return switch (metric) {
            case GAMES_PLAYED -> "gamesPlayed";
            case VICTORIES -> "victories";
            case DEFEATS -> "defeats";
            case TOTAL_PLAY_TIME -> "totalPlayTime";
            case SHORTEST_GAME -> "shortestGame";
            case LONGEST_GAME -> "longestGame";
            case TOTAL_CARDS_USED -> "totalCardsUsed";
            case POWER_CARDS_USED -> "powerCardsUsed";
            case MAX_LINE_LENGTH -> "maxLineLength";
            case TOTAL_LINES_COMPLETED -> "totalLinesCompleted";
            case AVERAGE_LINE_LENGTH -> "averageLineLength";
            case COMPLETED_PUZZLES -> "completedPuzzles";
            case HIGHEST_SCORE_PUZZLE -> "highestScorePuzzle";
            case TOTAL_PUZZLE_SCORE -> "totalPuzzleScore";
            case TOTAL_SCORE -> "totalScore";
            case HIGHEST_SCORE -> "highestScore";
            case LOWEST_SCORE -> "lowestScore";
            case MESSAGES_SENT -> "messagesSent";
            case FRIENDS_COUNT -> "friendsCount";

            default -> throw new IllegalArgumentException("Calculated metric: " + metric);
        };
    }

    private Number getValueFromStatistic(PlayerStatistics stats, Metric metric) {
        return switch (metric) {
            case GAMES_PLAYED -> stats.getGamesPlayed();
            case VICTORIES -> stats.getVictories();
            case DEFEATS -> stats.getDefeats();
            case WIN_RATE -> stats.getWinRate();
            case TOTAL_PLAY_TIME -> stats.getTotalPlayTime();
            case AVERAGE_DURATION -> stats.getAverageDuration();
            case SHORTEST_GAME -> stats.getShortestGame();
            case LONGEST_GAME -> stats.getLongestGame();
            case TOTAL_CARDS_USED -> stats.getTotalCardsUsed();
            case POWER_CARDS_USED -> stats.getPowerCardsUsed();
            case AVERAGE_CARDS_PER_GAME -> stats.getAverageCardsPerGame();
            case MAX_LINE_LENGTH -> stats.getMaxLineLength();
            case TOTAL_LINES_COMPLETED -> stats.getTotalLinesCompleted();
            case AVERAGE_LINE_LENGTH -> stats.getAverageLineLength();
            case COMPLETED_PUZZLES -> stats.getCompletedPuzzles();
            case HIGHEST_SCORE_PUZZLE -> stats.getHighestScorePuzzle();
            case TOTAL_PUZZLE_SCORE -> stats.getTotalPuzzleScore();
            case AVERAGE_PUZZLE_SCORE -> {
                if (stats.getCompletedPuzzles() == 0)
                    yield 0.0;
                yield stats.getTotalPuzzleScore().doubleValue() / stats.getCompletedPuzzles();
            }
            case TOTAL_SCORE -> stats.getTotalScore();
            case HIGHEST_SCORE -> stats.getHighestScore();
            case LOWEST_SCORE -> stats.getLowestScore();
            case AVERAGE_SCORE -> stats.getAverageScore();
            case MESSAGES_SENT -> stats.getMessagesSent();
            case FRIENDS_COUNT -> stats.getFriendsCount();
        };
    }

    private List<PlayerRankingDTO> buildRanking(List<PlayerStatistics> statsList, Metric metric) {
        List<PlayerRankingDTO> ranking = new java.util.ArrayList<>();

        for (int i = 0; i < statsList.size(); i++) {
            PlayerStatistics stats = statsList.get(i);
            ranking.add(new PlayerRankingDTO(
                    stats.getPlayer().getId(),
                    stats.getPlayer().getUsername(),
                    stats.getPlayer().getAvatarId(),
                    i + 1,
                    metric,
                    getValueFromStatistic(stats, metric),
                    stats.getWinRate(),
                    stats.getGamesPlayed()));
        }

        return ranking;
    }
}
