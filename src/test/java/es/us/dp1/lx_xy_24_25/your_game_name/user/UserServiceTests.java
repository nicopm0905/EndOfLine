package es.us.dp1.lx_xy_24_25.your_game_name.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;

@Epic("Users & Admin Module")
@Feature("Users Management")
@Owner("DP1-tutors")
@SpringBootTest
@AutoConfigureTestDatabase
class UserServiceTests {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthoritiesService authService;

    @Test
    @WithMockUser(username = "player1", password = "0wn3r")
    void shouldFindCurrentUser() {
        User user = this.userService.findCurrentUser();
        assertEquals("player1", user.getUsername());
    }

    @Test
    @WithMockUser(username = "prueba")
    void shouldNotFindCorrectCurrentUser() {
        assertThrows(ResourceNotFoundException.class, () -> this.userService.findCurrentUser());
    }

    @Test
    void shouldNotFindAuthenticated() {
        assertThrows(ResourceNotFoundException.class, () -> this.userService.findCurrentUser());
    }

    @Test
    void shouldFindAllUsers() {
        List<User> users = (List<User>) this.userService.findAll();
        assertFalse(users.isEmpty());
    }

    @Test
    void shouldFindUsersByUsername() {
        User user = this.userService.findUser("player1");
        assertEquals("player1", user.getUsername());
    }

    @Test
    void shouldFindUsersByAuthority() {
        List<User> owners = (List<User>) this.userService.findAllByAuthority("PLAYER");
        assertFalse(owners.isEmpty(), "Should find at least one PLAYER");

        List<User> admins = (List<User>) this.userService.findAllByAuthority("ADMIN");
        assertFalse(admins.isEmpty(), "Should find at least one ADMIN");

        List<User> vets = (List<User>) this.userService.findAllByAuthority("VET");
        assertTrue(vets.isEmpty(), "Should not find any VET if not seeded");
    }

    @Test
    void shouldNotFindUserByIncorrectUsername() {
        assertThrows(ResourceNotFoundException.class, () -> this.userService.findUser("usernotexists"));
    }

    @Test
    void shouldFindSingleUser() {
        User existing = this.userService.findUser("player1");
        User foundById = this.userService.findUser(existing.getId());
        assertEquals("player1", foundById.getUsername());
    }

    @Test
    void shouldNotFindSingleUserWithBadID() {
        assertThrows(ResourceNotFoundException.class, () -> this.userService.findUser(Integer.MAX_VALUE));
    }

    @Test
    void shouldExistUser() {
        assertTrue(this.userService.existsUser("player1"));
    }

    @Test
    void shouldNotExistUser() {
        assertFalse(this.userService.existsUser("player10000"));
    }

    @Test
    @Transactional
    void shouldUpdateUser() {
        User userToUpdate = this.userService.findUser("player1");
        Integer idToUpdate = userToUpdate.getId();
        
        String newName = "Change";
        userToUpdate.setUsername(newName);
        
        userService.updateUser(userToUpdate, idToUpdate);
        
        User updatedUser = this.userService.findUser(idToUpdate);
        assertEquals(newName, updatedUser.getUsername());
    }

    @Test
    @Transactional
    void shouldInsertUser() {
        int count = ((Collection<User>) this.userService.findAll()).size();

        User user = new User();
        user.setUsername("Sam");
        user.setPassword("password");
        user.setFirstName("Sam");
        user.setLastName("Doe");
        user.setEmail("sam@example.com");
        user.setAuthority(authService.findByAuthority("ADMIN"));

        this.userService.saveUser(user);
        
        assertNotNull(user.getId());
        assertNotEquals(0, user.getId().longValue());

        int finalCount = ((Collection<User>) this.userService.findAll()).size();
        assertEquals(count + 1, finalCount);
    }
}