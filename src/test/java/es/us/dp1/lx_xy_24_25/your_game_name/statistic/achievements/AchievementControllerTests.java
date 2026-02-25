package es.us.dp1.lx_xy_24_25.your_game_name.statistic.achievements;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.us.dp1.lx_xy_24_25.your_game_name.configuration.SpringSecurityWebAuxTestConfiguration;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.BadRequestException;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.Metric;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;

@Epic("Statistics & Achievements Module")
@Feature("Achievement Management")
@Owner("DP1-tutors")
@WebMvcTest(controllers = AchievementRestController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class))
@Import({ SpringSecurityWebAuxTestConfiguration.class, AchievementControllerTests.TestExceptionHandler.class })
@EnableMethodSecurity(prePostEnabled = true)
class AchievementControllerTests {

    @TestConfiguration
    @RestControllerAdvice
    public static class TestExceptionHandler {
        @ExceptionHandler(ResourceNotFoundException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        public void handleNotFound() {
        }

        @ExceptionHandler({
                BadRequestException.class,
                MethodArgumentNotValidException.class
        })
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public void handleBadRequest() {
        }

        @ExceptionHandler(AccessDeniedException.class)
        @ResponseStatus(HttpStatus.FORBIDDEN)
        public void handleForbidden() {
        }
    }

    private static final int TEST_ACHIEVEMENT_ID = 10;
    private static final int NON_EXISTENT_ID = 99;
    private static final String TEST_ACHIEVEMENT_NAME = "FirstGame";
    private static final String BASE_URL = "/api/v1/achievements";
    private static final String REGULAR_USER_NAME = "player";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AchievementService achievementService;

    @MockBean
    private javax.sql.DataSource dataSource;
    @MockBean
    private es.us.dp1.lx_xy_24_25.your_game_name.user.UserService userService;
    @MockBean
    private es.us.dp1.lx_xy_24_25.your_game_name.user.AuthoritiesService authoritiesService;
    @MockBean
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    @MockBean
    private es.us.dp1.lx_xy_24_25.your_game_name.configuration.services.UserDetailsServiceImpl userDetailsService;
    @MockBean
    private es.us.dp1.lx_xy_24_25.your_game_name.configuration.jwt.AuthEntryPointJwt authEntryPointJwt;
    @MockBean
    private es.us.dp1.lx_xy_24_25.your_game_name.configuration.jwt.JwtUtils jwtUtils;

    private Achievement validAchievement, invalidAchievementData;

    @BeforeEach
    void setup() {
        validAchievement = new Achievement();
        validAchievement.setId(TEST_ACHIEVEMENT_ID);
        validAchievement.setName(TEST_ACHIEVEMENT_NAME);
        validAchievement.setDescription("Play games.");
        validAchievement.setThreshold(1.0);
        validAchievement.setMetric(Metric.GAMES_PLAYED);

        invalidAchievementData = new Achievement();
        invalidAchievementData.setId(TEST_ACHIEVEMENT_ID);
        invalidAchievementData.setName("");
        invalidAchievementData.setDescription("Valid description.");
        invalidAchievementData.setThreshold(1.0);
        invalidAchievementData.setMetric(Metric.VICTORIES);
    }

    @Test
    @WithMockUser(REGULAR_USER_NAME)
    void shouldFindAllAchievements() throws Exception {
        when(this.achievementService.findAll()).thenReturn(List.of(validAchievement));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));

        verify(achievementService).findAll();
    }

    @Test
    @WithMockUser(REGULAR_USER_NAME)
    void shouldFindAchievementById() throws Exception {
        when(this.achievementService.findById(TEST_ACHIEVEMENT_ID)).thenReturn(validAchievement);

        mockMvc.perform(get(BASE_URL + "/{id}", TEST_ACHIEVEMENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(validAchievement.getName()));
    }

    @Test
    @WithMockUser(REGULAR_USER_NAME)
    void shouldReturnNotFoundById() throws Exception {
        when(this.achievementService.findById(NON_EXISTENT_ID))
                .thenThrow(new ResourceNotFoundException("Achievement", "id", NON_EXISTENT_ID));

        mockMvc.perform(get(BASE_URL + "/{id}", NON_EXISTENT_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(REGULAR_USER_NAME)
    void shouldFindAchievementByName() throws Exception {
        when(this.achievementService.findByName(TEST_ACHIEVEMENT_NAME)).thenReturn(validAchievement);

        mockMvc.perform(get(BASE_URL + "/name/{name}", TEST_ACHIEVEMENT_NAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(TEST_ACHIEVEMENT_NAME));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    void shouldCreateAchievement_asAdmin() throws Exception {
        Achievement newAchievement = new Achievement();
        newAchievement.setName("New");
        newAchievement.setDescription("Desc");
        newAchievement.setThreshold(5.0);
        newAchievement.setMetric(Metric.TOTAL_PLAY_TIME);

        when(this.achievementService.save(any(Achievement.class))).thenReturn(validAchievement);

        mockMvc.perform(post(BASE_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newAchievement)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(validAchievement.getName()));

        verify(achievementService).save(any(Achievement.class));
    }

    @Test
    @WithMockUser(REGULAR_USER_NAME)
    void shouldReturnForbiddenOnCreate_asRegularUser() throws Exception {
        Achievement newAchievement = new Achievement();
        newAchievement.setName("New");
        newAchievement.setDescription("Desc");
        newAchievement.setThreshold(5.0);
        newAchievement.setMetric(Metric.VICTORIES);

        mockMvc.perform(post(BASE_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newAchievement)))
                .andExpect(status().isForbidden());

        verify(achievementService, never()).save(any(Achievement.class));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    void shouldReturnBadRequestOnCreate_ifInvalidData() throws Exception {
        mockMvc.perform(post(BASE_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAchievementData)))
                .andExpect(status().isBadRequest());

        verify(achievementService, never()).save(any(Achievement.class));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    void shouldUpdateAchievement_asAdmin() throws Exception {
        Achievement updateData = new Achievement();
        updateData.setId(TEST_ACHIEVEMENT_ID);
        updateData.setName("UPDATED");
        updateData.setDescription("Updated Desc");
        updateData.setThreshold(10.0);
        updateData.setMetric(Metric.AVERAGE_DURATION);

        when(this.achievementService.update(any(Achievement.class), anyInt())).thenReturn(updateData);

        mockMvc.perform(put(BASE_URL + "/{id}", TEST_ACHIEVEMENT_ID)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UPDATED"));

        verify(achievementService).update(any(Achievement.class), anyInt());
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    void shouldReturnBadRequestOnUpdate_ifIdInconsistent() throws Exception {
        Achievement updateData = new Achievement();
        updateData.setId(999);
        updateData.setName("Valid Name");
        updateData.setDescription("Valid Desc");
        updateData.setThreshold(1.0);
        updateData.setMetric(Metric.GAMES_PLAYED);

        mockMvc.perform(put(BASE_URL + "/{id}", TEST_ACHIEVEMENT_ID)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isBadRequest());

        verify(achievementService, never()).update(any(Achievement.class), anyInt());
    }

    @Test
    @WithMockUser(REGULAR_USER_NAME)
    void shouldReturnForbiddenOnUpdate_asRegularUser() throws Exception {
        Achievement updateData = new Achievement();
        updateData.setId(TEST_ACHIEVEMENT_ID);
        updateData.setName("Valid Name");
        updateData.setDescription("Valid Desc");
        updateData.setThreshold(1.0);
        updateData.setMetric(Metric.MAX_LINE_LENGTH);

        mockMvc.perform(put(BASE_URL + "/{id}", TEST_ACHIEVEMENT_ID)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isForbidden());

        verify(achievementService, never()).update(any(Achievement.class), anyInt());
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    void shouldDeleteAchievement_asAdmin() throws Exception {
        doNothing().when(this.achievementService).deleteById(TEST_ACHIEVEMENT_ID);

        mockMvc.perform(delete(BASE_URL + "/{id}", TEST_ACHIEVEMENT_ID)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Achievement deleted successfully"));

        verify(achievementService).deleteById(TEST_ACHIEVEMENT_ID);
    }
}
