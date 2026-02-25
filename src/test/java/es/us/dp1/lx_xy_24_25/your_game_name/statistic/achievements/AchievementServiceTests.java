package es.us.dp1.lx_xy_24_25.your_game_name.statistic.achievements;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse; 
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.Metric;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;

@Epic("Statistics & Achievements Module")
@Feature("Achievement Service Tests")
@Owner("DP1-tutors")
@SpringBootTest
@AutoConfigureTestDatabase
class AchievementServiceTests {

    @Autowired
    private AchievementService achievementService;

    @Test
    void shouldFindAllAchievements() {
        List<Achievement> achievements = this.achievementService.findAll();
        assertNotNull(achievements);
        assertFalse(achievements.isEmpty(), "The database should not be empty");
    }

    @Test
    @Transactional
    void shouldFindAchievementById() {
        Achievement newAchievement = createTestAchievement("TestFindId", 1.0, Metric.GAMES_PLAYED);
        Achievement savedAchievement = this.achievementService.save(newAchievement);
        
        Achievement foundAchievement = this.achievementService.findById(savedAchievement.getId());
        
        assertNotNull(foundAchievement);
        assertEquals(savedAchievement.getId(), foundAchievement.getId());
    }
    
    @Test
    void shouldThrowExceptionWhenAchievementNotFoundById() {
        assertThrows(ResourceNotFoundException.class, () -> this.achievementService.findById(9999));
    }
    
    @Test
    @Transactional
    void shouldFindAchievementByName() {
        String name = "TestFindNameUnique";
        Achievement newAchievement = createTestAchievement(name, 2.0, Metric.VICTORIES);
        this.achievementService.save(newAchievement);
        
        Achievement foundAchievement = this.achievementService.findByName(name);
        
        assertNotNull(foundAchievement);
        assertEquals(name, foundAchievement.getName());
    }

    @Test
    void shouldThrowExceptionWhenAchievementNotFoundByName() {
        assertThrows(ResourceNotFoundException.class, () -> this.achievementService.findByName("NonExistentName404"));
    }

    @Test
    @Transactional
    void shouldExistByName() {
        String name = "TestExistsUnique";
        Achievement newAchievement = createTestAchievement(name, 3.0, Metric.TOTAL_PLAY_TIME);
        this.achievementService.save(newAchievement);
        
        assertTrue(this.achievementService.existsByName(name));
    }

    @Test
    @Transactional
    void shouldInsertAchievement() {
        int initialCount = this.achievementService.findAll().size();

        Achievement newAchievement = createTestAchievement("TestInsertUnique", 10.0, Metric.AVERAGE_DURATION);
        Achievement savedAchievement = this.achievementService.save(newAchievement);
        
        assertNotNull(savedAchievement.getId());
        assertNotEquals(0, savedAchievement.getId().intValue());
        
        int finalCount = this.achievementService.findAll().size();
        assertEquals(initialCount + 1, finalCount);
    }

    @Test
    @Transactional
    void shouldUpdateAchievement() {
        Achievement initialAchievement = createTestAchievement("InitialName", 5.0, Metric.GAMES_PLAYED); 
        Achievement savedAchievement = this.achievementService.save(initialAchievement);
        
        String newDescription = "Updated Description Test";
        savedAchievement.setDescription(newDescription);
        savedAchievement.setThreshold(15.0);
        
        Achievement updatedAchievement = this.achievementService.update(savedAchievement, savedAchievement.getId());
        
        assertEquals(newDescription, updatedAchievement.getDescription());
        assertEquals(15.0, updatedAchievement.getThreshold());
        
        Achievement retrievedAchievement = this.achievementService.findById(savedAchievement.getId());
        assertEquals(newDescription, retrievedAchievement.getDescription());
    }
    
    @Test
    @Transactional
    void shouldThrowExceptionOnUpdateIfNotFound() {
        Achievement nonExistentAchievement = createTestAchievement("NonExistentUpdate", 1.0, Metric.MAX_LINE_LENGTH);
        
        assertThrows(ResourceNotFoundException.class, () -> 
            this.achievementService.update(nonExistentAchievement, 9999)
        );
    }

    @Test
    @Transactional
    void shouldDeleteAchievementById() {
        Achievement newAchievement = createTestAchievement("ToDelete", 1.0, Metric.GAMES_PLAYED);
        Achievement savedAchievement = this.achievementService.save(newAchievement);
        int savedId = savedAchievement.getId();
        
        this.achievementService.deleteById(savedId);
        
        assertThrows(ResourceNotFoundException.class, () -> this.achievementService.findById(savedId));
    }
    
    @Test
    void shouldThrowExceptionOnDeleteIfNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> this.achievementService.deleteById(9999));
    }
    
    private Achievement createTestAchievement(String name, double threshold, Metric metric) {
        Achievement a = new Achievement();
        a.setName(name);
        a.setDescription("Test desc");
        a.setThreshold(threshold);
        a.setPoints(10);
        a.setMetric(metric); 
        return a;
    }
}