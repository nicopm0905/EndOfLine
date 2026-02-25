package es.us.dp1.lx_xy_24_25.your_game_name.configuration.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import es.us.dp1.lx_xy_24_25.your_game_name.configuration.services.UserDetailsImpl;
import es.us.dp1.lx_xy_24_25.your_game_name.user.Authorities;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTests {

    @InjectMocks
    private JwtUtils jwtUtils;

    private String secret = "testSecretKeyForJwtMustBeLongEnoughToSurviveHS512AlgorithmRequirementsAndTests1234567890";
    private int expirationMs = 3600000;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", secret);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", expirationMs);
    }

    @Test
    void generateJwtToken_ShouldGenerateToken() {
        Authentication auth = mock(Authentication.class);
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        Collection<? extends GrantedAuthority> authorities = Collections
                .singletonList(new SimpleGrantedAuthority("PLAYER"));

        when(auth.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testUser");
        when(userDetails.getAuthorities()).thenAnswer(invocation -> authorities); // thenAnswer to avoid unchecked cast
                                                                                  // warning if possible
        when(userDetails.getAvatarId()).thenReturn(1);

        String token = jwtUtils.generateJwtToken(auth);

        assertNotNull(token);
        assertTrue(token.length() > 0);
        assertTrue(jwtUtils.validateJwtToken(token));
        assertEquals("testUser", jwtUtils.getUserNameFromJwtToken(token));
    }

    @Test
    void generateTokenFromUsername_ShouldGenerateToken() {
        Authorities auth = new Authorities();
        auth.setAuthority("PLAYER");

        String token = jwtUtils.generateTokenFromUsername("testUser", auth, 1);

        assertNotNull(token);
        assertTrue(jwtUtils.validateJwtToken(token));
        assertEquals("testUser", jwtUtils.getUserNameFromJwtToken(token));
    }

    @Test
    void validateJwtToken_ShouldReturnFalse_WhenInvalidToken() {
        assertFalse(jwtUtils.validateJwtToken("invalidToken"));
    }

    @Test
    void validateJwtToken_ShouldReturnFalse_WhenEmptyToken() {
        assertFalse(jwtUtils.validateJwtToken(""));
    }

    @Test
    void validateJwtToken_ShouldReturnFalse_WhenNull() {
        assertFalse(jwtUtils.validateJwtToken(null));
    }
}
