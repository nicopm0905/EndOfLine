package es.us.dp1.lx_xy_24_25.your_game_name.configuration.services;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.Authorities;

class UserDetailsImplTests {

    @Test
    void testUserDetailsImpl() {
        Authorities authority = new Authorities();
        authority.setAuthority("ROLE_PLAYER");

        User user = new User();
        user.setId(1);
        user.setUsername("testuser");
        user.setPassword("password");
        user.setAuthority(authority);

        UserDetailsImpl details = UserDetailsImpl.build(user);

        assertEquals(1, details.getId());
        assertEquals("testuser", details.getUsername());
        assertEquals("password", details.getPassword());
        assertTrue(details.isAccountNonExpired());
        assertTrue(details.isAccountNonLocked());
        assertTrue(details.isCredentialsNonExpired());
        assertTrue(details.isEnabled());

        UserDetailsImpl details2 = new UserDetailsImpl(1, "testuser", "password", null, Collections.emptyList());
        assertEquals(details, details2);
        assertEquals(details.hashCode(), details2.hashCode());

        assertNotEquals(details, null);
        assertNotEquals(details, "string");

        UserDetailsImpl details3 = new UserDetailsImpl(2, "other", "pass", null, Collections.emptyList());
        assertNotEquals(details, details3);
    }
}
