package es.us.dp1.lx_xy_24_25.your_game_name.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import es.us.dp1.lx_xy_24_25.your_game_name.auth.payload.request.LoginRequest;
import es.us.dp1.lx_xy_24_25.your_game_name.auth.payload.request.SignupRequest;
import es.us.dp1.lx_xy_24_25.your_game_name.auth.payload.response.JwtResponse;
import es.us.dp1.lx_xy_24_25.your_game_name.auth.payload.response.MessageResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AuthControllerE2ETests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldRegisterAndLoginUser() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("e2eUser");
        signupRequest.setPassword("password123");
        signupRequest.setAuthority("2");
        signupRequest.setFirstName("E2E");
        signupRequest.setLastName("User");
        signupRequest.setEmail("e2e@example.com");

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<SignupRequest> signupEntity = new HttpEntity<>(signupRequest, headers);

        ResponseEntity<MessageResponse> signupResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/auth/signup"),
            HttpMethod.POST,
            signupEntity,
            MessageResponse.class
        );

        assertEquals(HttpStatus.OK, signupResponse.getStatusCode());
        assertEquals("User registered successfully!", signupResponse.getBody().getMessage());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("e2eUser");
        loginRequest.setPassword("password123");

        HttpEntity<LoginRequest> loginEntity = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<JwtResponse> loginResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/auth/signin"),
            HttpMethod.POST,
            loginEntity,
            JwtResponse.class
        );

        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertNotNull(loginResponse.getBody().getToken());
        assertEquals("e2eUser", loginResponse.getBody().getUsername());
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
