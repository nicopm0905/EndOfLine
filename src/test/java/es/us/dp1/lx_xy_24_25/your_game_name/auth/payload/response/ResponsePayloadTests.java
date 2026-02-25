package es.us.dp1.lx_xy_24_25.your_game_name.auth.payload.response;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.List;

class ResponsePayloadTests {

    @Test
    void testJwtResponse() {
        JwtResponse response = new JwtResponse("token", 1, "user", List.of("ROLE_PLAYER"));

        assertEquals("token", response.getToken());
        assertEquals(1, response.getId());
        assertEquals("user", response.getUsername());
        assertEquals(List.of("ROLE_PLAYER"), response.getRoles());
        assertEquals("Bearer", response.getType());

        response.setToken("new-token");
        assertEquals("new-token", response.getToken());

        response.setType("Basic");
        assertEquals("Basic", response.getType());

        response.setId(2);
        assertEquals(2, response.getId());

        response.setUsername("new-user");
        assertEquals("new-user", response.getUsername());

        response.setRoles(List.of("ROLE_ADMIN"));
        assertEquals(List.of("ROLE_ADMIN"), response.getRoles());

        assertNotNull(response.toString());
    }

    @Test
    void testMessageResponse() {
        MessageResponse response = new MessageResponse("Success");
        assertEquals("Success", response.getMessage());

        response.setMessage("Error");
        assertEquals("Error", response.getMessage());
    }
}
