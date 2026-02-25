package es.us.dp1.lx_xy_24_25.your_game_name.statistic.achievements;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.us.dp1.lx_xy_24_25.your_game_name.configuration.SecurityConfiguration;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.Metric;

@WebMvcTest(controllers = AchievementRestController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfiguration.class),
    excludeAutoConfiguration = org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class)
class AchievementRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AchievementService achievementService;

    private Achievement achievement;

    @BeforeEach
    void setUp() {
        achievement = new Achievement();
        achievement.setId(1);
        achievement.setName("First Win");
        achievement.setDescription("Win your first game");
        achievement.setMetric(Metric.VICTORIES);
        achievement.setThreshold(1);
    }

    @Test
    @WithMockUser
    void findAll_ShouldReturnAchievements() throws Exception {
        when(achievementService.findAll()).thenReturn(List.of(achievement));

        mockMvc.perform(get("/api/v1/achievements"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("First Win"));
    }

    @Test
    @WithMockUser
    void findAchievement_ShouldReturnAchievement() throws Exception {
        when(achievementService.findById(1)).thenReturn(achievement);

        mockMvc.perform(get("/api/v1/achievements/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    void findByName_ShouldReturnAchievement() throws Exception {
        when(achievementService.findByName("First Win")).thenReturn(achievement);

        mockMvc.perform(get("/api/v1/achievements/name/First Win"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("First Win"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void createAchievement_ShouldReturnCreated() throws Exception {
        when(achievementService.save(any(Achievement.class))).thenReturn(achievement);

        mockMvc.perform(post("/api/v1/achievements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(achievement)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("First Win"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void updateAchievement_ShouldReturnUpdated() throws Exception {
        when(achievementService.update(any(Achievement.class), any(Integer.class))).thenReturn(achievement);

        mockMvc.perform(put("/api/v1/achievements/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(achievement)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("First Win"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void deleteAchievement_ShouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/v1/achievements/1"))
            .andExpect(status().isOk());
    }
}
