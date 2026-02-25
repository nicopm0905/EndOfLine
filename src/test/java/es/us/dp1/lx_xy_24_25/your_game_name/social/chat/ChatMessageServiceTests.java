package es.us.dp1.lx_xy_24_25.your_game_name.social.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.dto.ChatMessageDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.ChatRequestDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.BadRequestException;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.social.chatMessage.ChatMessageService;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;

@Epic("Social Module")
@Feature("Chat Message Service Tests")
@Owner("DP1-tutors")
@SpringBootTest
@AutoConfigureTestDatabase
class ChatMessageServiceTests {

    @Autowired
    private ChatMessageService chatMessageService;

    private static final Integer GAME_SESSION_ID = 3;
    private static final String PLAYER_USERNAME = "player3"; // Player with ID 6 in game session 3
    private static final String PLAYER2_USERNAME = "player2";
    private static final String NON_PARTICIPANT_USERNAME = "player1";
    private static final Integer NON_EXISTENT_GAME_ID = 9999;

    @Test
    @Transactional
    void shouldFindAllMessagesByGameId() {
        ChatRequestDTO request1 = new ChatRequestDTO();
        request1.setMessage("Hello everyone!");
        chatMessageService.createAndBroadcast(request1, GAME_SESSION_ID, PLAYER_USERNAME);

        ChatRequestDTO request2 = new ChatRequestDTO();
        request2.setMessage("Good game!");
        chatMessageService.createAndBroadcast(request2, GAME_SESSION_ID, PLAYER_USERNAME);

        List<ChatMessageDTO> messages = chatMessageService.findAllMessageDTOsByGameId(GAME_SESSION_ID);

        assertNotNull(messages);
        assertTrue(messages.size() >= 2);
    }

    @Test
    @Transactional
    void shouldSaveMessageSuccessfully() {
        ChatRequestDTO request = new ChatRequestDTO();
        request.setMessage("This is a test message");

        ChatMessageDTO savedMessage = chatMessageService.createAndBroadcast(request, GAME_SESSION_ID, PLAYER_USERNAME);

        assertNotNull(savedMessage);
        assertEquals("This is a test message", savedMessage.getMessage());
        assertNotNull(savedMessage.getColor());
        assertEquals("RED", savedMessage.getColor().toString());
    }

    @Test
    @Transactional
    void shouldThrowExceptionWhenSecondPlayerNotInGame() {
        // Test that a player not in the game session cannot send messages
        ChatRequestDTO request = new ChatRequestDTO();
        request.setMessage("Message from second player");

        assertThrows(BadRequestException.class,
                () -> chatMessageService.createAndBroadcast(request, GAME_SESSION_ID, PLAYER2_USERNAME));
    }

    @Test
    void shouldThrowExceptionWhenGameNotFound() {
        ChatRequestDTO request = new ChatRequestDTO();
        request.setMessage("Message in non-existent game");

        assertThrows(ResourceNotFoundException.class,
                () -> chatMessageService.createAndBroadcast(request, NON_EXISTENT_GAME_ID, PLAYER_USERNAME));
    }

    @Test
    void shouldThrowExceptionWhenPlayerNotFound() {
        ChatRequestDTO request = new ChatRequestDTO();
        request.setMessage("Message from non-existent user");

        assertThrows(ResourceNotFoundException.class,
                () -> chatMessageService.createAndBroadcast(request, GAME_SESSION_ID, "nonExistentUser"));
    }

    @Test
    @Transactional
    void shouldThrowExceptionWhenPlayerNotInGame() {
        ChatRequestDTO request = new ChatRequestDTO();
        request.setMessage("Message from player not in game");

        assertThrows(BadRequestException.class,
                () -> chatMessageService.createAndBroadcast(request, GAME_SESSION_ID, NON_PARTICIPANT_USERNAME));
    }

    @Test
    @Transactional
    void shouldThrowExceptionWhenMessageIsEmpty() {
        ChatRequestDTO request = new ChatRequestDTO();
        request.setMessage("");

        assertThrows(BadRequestException.class,
                () -> chatMessageService.createAndBroadcast(request, GAME_SESSION_ID, PLAYER_USERNAME));
    }

    @Test
    void shouldThrowExceptionWhenMessageIsTooLong() {
        ChatRequestDTO request = new ChatRequestDTO();
        String longMessage = "a".repeat(256);
        request.setMessage(longMessage);

        assertThrows(BadRequestException.class,
                () -> chatMessageService.createAndBroadcast(request, GAME_SESSION_ID, PLAYER_USERNAME));
    }
}
