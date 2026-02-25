package es.us.dp1.lx_xy_24_25.your_game_name.statistic.playerAchievement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerAchievementRepository extends JpaRepository<PlayerAchievement, Integer> {

    List<PlayerAchievement> findByPlayerId(Integer playerId);

    @Query("SELECT pa FROM PlayerAchievement pa WHERE pa.player.id = :playerId AND pa.achievement.id = :achievementId")
    Optional<PlayerAchievement> findByPlayerIdAndAchievementId(@Param("playerId") Integer playerId,
            @Param("achievementId") Integer achievementId);

    @Query("SELECT pa FROM PlayerAchievement pa WHERE pa.player.id = :playerId AND pa.completed = true")
    List<PlayerAchievement> findCompletedByPLayerId(@Param("playerId") Integer playerId);

    List<PlayerAchievement> findByAchievementId(Integer achievementId);

}
