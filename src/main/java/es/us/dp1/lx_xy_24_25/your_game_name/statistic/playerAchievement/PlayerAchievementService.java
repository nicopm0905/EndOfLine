package es.us.dp1.lx_xy_24_25.your_game_name.statistic.playerAchievement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.us.dp1.lx_xy_24_25.your_game_name.dto.PlayerAchievementDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.achievements.Achievement;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.achievements.AchievementService;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.playerStatistics.PlayerStatistics;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.playerStatistics.PlayerStatisticsRepository;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlayerAchievementService {
    private PlayerAchievementRepository playerAchievementRepository;
    private AchievementService achievementService;
    private final PlayerRepository playerRepository;
    private final PlayerStatisticsRepository playerStatisticsRepository;

    @Autowired
    public PlayerAchievementService(PlayerAchievementRepository playerAchievementRepository,
            AchievementService achievementService,
            PlayerRepository playerRepository,
            PlayerStatisticsRepository playerStatisticsRepository) {
        this.playerAchievementRepository = playerAchievementRepository;
        this.achievementService = achievementService;
        this.playerRepository = playerRepository;
        this.playerStatisticsRepository = playerStatisticsRepository;
    }

    @Transactional
    public PlayerAchievement getOrCreatePlayerAchievement(Integer playerId, Integer achievementId) {
        return playerAchievementRepository.findByPlayerId(playerId).stream()
                .filter(pa -> pa.getAchievement().getId().equals(achievementId))
                .findFirst()
                .orElseGet(() -> {
                    Player player = playerRepository.findById(playerId)
                            .orElseThrow(() -> new ResourceNotFoundException("Player", "id", playerId));
                    Achievement achievement = achievementService.findById(achievementId);

                    PlayerAchievement pa = new PlayerAchievement();
                    pa.setPlayer(player);
                    pa.setAchievement(achievement);
                    pa.setProgress(0);
                    pa.setCompleted(false);
                    return playerAchievementRepository.save(pa);
                });
    }

    @Transactional
    public List<PlayerAchievement> findByPlayerId(Integer playerId) {
        return playerAchievementRepository.findByPlayerId(playerId);
    }

    @Transactional
    public PlayerAchievement findById(Integer id) {
        return playerAchievementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PlayerAchievement", "id", id));
    }

    @Transactional
    public PlayerAchievement findByPlayerIdAndAchievementId(Integer playerId, Integer achievementId) {
        return playerAchievementRepository.findByPlayerId(playerId).stream()
                .filter(pa -> pa.getAchievement().getId().equals(achievementId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("PlayerAchievement",
                        "playerId=" + playerId + ", achievementId=" + achievementId, null));
    }

    @Transactional
    public PlayerAchievement save(PlayerAchievement playerAchievement) {
        return playerAchievementRepository.save(playerAchievement);
    }

    @Transactional
    public PlayerAchievement updateProgress(Integer playerId, Integer achievementId, Integer progress) {
        PlayerAchievement pa = getOrCreatePlayerAchievement(playerId, achievementId);
        pa.setProgress(progress);

        Achievement achievement = achievementService.findById(achievementId);
        if (progress >= achievement.getThreshold() && !pa.getCompleted()) {
            pa.setCompleted(true);
            pa.setUnlockedAt(LocalDateTime.now());
        }

        return playerAchievementRepository.save(pa);
    }

    @Transactional
    public void deleteById(Integer id) {
        PlayerAchievement pa = findById(id);
        playerAchievementRepository.delete(pa);
    }

    @Transactional
    public List<PlayerAchievement> findCompletedByPlayerId(Integer playerId) {
        return playerAchievementRepository.findByPlayerId(playerId).stream()
                .filter(PlayerAchievement::getCompleted)
                .toList();
    }

    @Transactional
    public List<PlayerAchievement> findByAchievementId(Integer achievementId) {
        return playerAchievementRepository.findByAchievementId(achievementId);
    }

    @Transactional
    public PlayerAchievement modifyPlayerAchievement(Integer id, PlayerAchievement newPlayerAchievement) {
        PlayerAchievement toUpdate = findById(id);

        toUpdate.setProgress(newPlayerAchievement.getProgress());
        toUpdate.setCompleted(newPlayerAchievement.getCompleted());
        toUpdate.setUnlockedAt(newPlayerAchievement.getUnlockedAt());

        return playerAchievementRepository.save(toUpdate);
    }

    @Transactional
    public List<PlayerAchievementDTO> getAchievementProgress(Integer achievementId) {
        Achievement achievement = achievementService.findById(achievementId);
        List<Player> allPlayers = playerRepository.findAll();

        return allPlayers.stream().map(player -> {
            PlayerStatistics stats = playerStatisticsRepository.findByPlayerId(player.getId()).orElse(null);

            int progress = calculateProgress(achievement, stats);
            boolean completed = progress >= achievement.getThreshold();

            updateProgress(player.getId(), achievementId, progress);

            return new PlayerAchievementDTO(
                    player.getId(),
                    achievement.getId(),
                    player.getUsername(),
                    progress,
                    achievement.getThreshold(),
                    completed);
        }).toList();
    }

    @Transactional
    public List<PlayerAchievementDTO> getPlayerProgress(Integer playerId) {
        List<Achievement> allAchievements = achievementService.findAll();
        PlayerStatistics stats = playerStatisticsRepository.findByPlayerId(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("PlayerStatistics", "playerId", playerId));

        return allAchievements.stream().map(achievement -> {
            int progress = calculateProgress(achievement, stats);
            boolean completed = progress >= achievement.getThreshold();

            updateProgress(playerId, achievement.getId(), progress);

            return new PlayerAchievementDTO(
                    stats.getPlayer().getId(),
                    achievement.getId(),
                    stats.getPlayer().getUsername(),
                    progress,
                    achievement.getThreshold(),
                    completed);
        }).toList();
    }

    private int calculateProgress(Achievement achievement, PlayerStatistics stats) {
        int progress = 0;
        if (stats != null) {
            progress = switch (achievement.getMetric()) {
                case GAMES_PLAYED -> stats.getGamesPlayed();
                case VICTORIES -> stats.getVictories();
                case MAX_LINE_LENGTH -> stats.getMaxLineLength();
                case TOTAL_PLAY_TIME -> stats.getTotalPlayTime().intValue();
                case AVERAGE_DURATION -> stats.getAverageDuration().intValue();
                case HIGHEST_SCORE_PUZZLE -> stats.getHighestScorePuzzle();
                case COMPLETED_PUZZLES -> stats.getCompletedPuzzles();
                case FRIENDS_COUNT -> stats.getFriendsCount();
                default -> 0;
            };
        }
        return progress;
    }
}
