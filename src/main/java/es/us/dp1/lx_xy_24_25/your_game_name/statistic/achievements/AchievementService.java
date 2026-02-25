package es.us.dp1.lx_xy_24_25.your_game_name.statistic.achievements;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;

@Service
public class AchievementService {
        
    private AchievementRepository repo;

    @Autowired
    public AchievementService(AchievementRepository repo){
        this.repo = repo;
    }

    @Transactional(readOnly = true)    
    public List<Achievement> findAll() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Achievement findById(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Achievement", "id", id));
    }

    @Transactional(readOnly = true)
    public Achievement findByName(String name) {
        return repo.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Achievement", "name", name));
    }

    @Transactional
    public Achievement save(Achievement achievement) {
        return repo.save(achievement);
    }

    @Transactional
    public Achievement update(Achievement achievement, Integer id) {
        Achievement toUpdate = findById(id);
        BeanUtils.copyProperties(achievement, toUpdate, "id", "playerAchievements");
        return repo.save(toUpdate);
    }

    @Transactional
    public void deleteById(Integer id) {
        Achievement achievement = findById(id);
        repo.delete(achievement);
    }

    public boolean existsByName(String name) {
        return repo.findByName(name).isPresent();
    }
}
