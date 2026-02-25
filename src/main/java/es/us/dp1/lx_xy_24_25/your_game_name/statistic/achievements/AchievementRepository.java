package es.us.dp1.lx_xy_24_25.your_game_name.statistic.achievements;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Integer>{
    
    public Optional<Achievement> findByName(String name);
}
