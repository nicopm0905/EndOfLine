package es.us.dp1.lx_xy_24_25.your_game_name.statistic.playerStatistics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerStatisticsRepository extends JpaRepository<PlayerStatistics, Integer> {
    Optional<PlayerStatistics> findByPlayerId(Integer playerId);    
}
