package es.us.dp1.lx_xy_24_25.your_game_name.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Collection;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.playerStatistics.PlayerStatistics;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.playerStatistics.PlayerStatisticsService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.Authorities;
import es.us.dp1.lx_xy_24_25.your_game_name.user.AuthoritiesService;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;

@Epic("Users & Admin Module")
@Feature("Player Service Tests")
@Owner("DP1-tutors")
@SpringBootTest
@AutoConfigureTestDatabase
class PlayerServiceTests {

    private static final String EXISTING_USERNAME = "existingPlayerTest";
    private static final String TEST_USERNAME_404 = "notExistUser";

    @Autowired
    private PlayerService playerService;

    @Autowired
    private AuthoritiesService authService;

    @Autowired
    private PlayerStatisticsService playerStatisticsService;

    @Autowired
    private PlayerRepository playerRepository;

    private Integer existingPlayerId;

    private Player createValidPlayer(String username) {
        Player p = new Player();
        p.setUsername(username);
        p.setPassword("pass");
        p.setFirstName("Test");
        p.setLastName("User");
        p.setEmail(username + "@email.com");
        Authorities playerAuth = authService.findByAuthority("PLAYER");
        p.setAuthority(playerAuth);
        return p;
    }

    @BeforeEach
    @Transactional
    void setupAuthUser() {
        if (!playerService.existPlayer(EXISTING_USERNAME)) {
            Player p = createValidPlayer(EXISTING_USERNAME);
            existingPlayerId = playerRepository.save(p).getId();
        } else {
            existingPlayerId = playerService.findPlayer(EXISTING_USERNAME).getId();
        }
    }

    @Test
    @WithMockUser(username = EXISTING_USERNAME, roles = "PLAYER")
    void shouldFindCurrentPlayer() {
        Player currentPlayer = playerService.findCurrentPlayer();
        assertNotNull(currentPlayer);
        assertEquals(EXISTING_USERNAME, currentPlayer.getUsername());
    }

    @Test
    void shouldFindPlayerByUsername() {
        Player player = playerService.findPlayer(EXISTING_USERNAME);
        assertNotNull(player);
        assertEquals(EXISTING_USERNAME, player.getUsername());
    }

    @Test
    void shouldThrowExceptionWhenFindPlayerByUsernameNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> playerService.findPlayer(TEST_USERNAME_404));
    }

    @Test
    void shouldFindPlayerById() {
        Player player = playerService.findPlayer(existingPlayerId);
        assertNotNull(player);
        assertEquals(existingPlayerId, player.getId());
    }

    @Test
    void shouldExistPlayer() {
        assertTrue(playerService.existPlayer(EXISTING_USERNAME));
    }

    @Test
    @Transactional
    void shouldSavePlayerAndCreateStatistics() {
        int initialPlayerCount = ((Collection<Player>) playerService.findAll()).size();
        String newUsername = "newPlayerTest";
        Player newPlayer = createValidPlayer(newUsername);

        Player savedPlayer = playerService.savePlayer(newPlayer);

        assertNotNull(savedPlayer.getId());

        int finalPlayerCount = ((Collection<Player>) playerService.findAll()).size();
        assertEquals(initialPlayerCount + 1, finalPlayerCount);

        PlayerStatistics stats = playerStatisticsService.findByPlayerId(savedPlayer.getId());
        assertNotNull(stats);
    }

    @Test
    @Transactional
    void shouldUpdatePlayer() {
        String newName = "ChangeNameUpdate";
        Player player = playerService.findPlayer(existingPlayerId);

        Player updateData = new Player();
        updateData.setUsername(newName);
        updateData.setPassword(player.getPassword());
        updateData.setFirstName(player.getFirstName());
        updateData.setLastName(player.getLastName());
        updateData.setEmail(player.getEmail());
        updateData.setAuthority(player.getAuthority());

        Player updatedPlayer = playerService.updatePlayer(updateData, existingPlayerId);

        assertEquals(newName, updatedPlayer.getUsername());

        Player retrievedPlayer = playerService.findPlayer(existingPlayerId);
        assertEquals(newName, retrievedPlayer.getUsername());
    }

    @Test
    @Transactional
    void shouldDeletePlayer() {
        String usernameToDelete = "delete_test_user_unique_2";
        Player playerToDelete = createValidPlayer(usernameToDelete);
        Player savedPlayer = playerService.savePlayer(playerToDelete);

        int savedId = savedPlayer.getId();

        playerService.deletePlayer(savedId);

        assertThrows(ResourceNotFoundException.class, () -> playerService.findPlayer(savedId));
    }
}