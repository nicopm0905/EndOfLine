package es.us.dp1.lx_xy_24_25.your_game_name.social.chatMessage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import es.us.dp1.lx_xy_24_25.your_game_name.dto.ChatMessageDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.ChatRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/chat")
@Tag(name = "Chat Messages", description = "API for sending and retrieving chat messages")
@SecurityRequirement(name = "bearerAuth")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @Autowired
    public ChatMessageController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    @PostMapping("{gameId}/message")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Send a message", description = "Sends a chat message to a specific game session.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Message sent successfully",
            content = @Content(schema = @Schema(implementation = ChatMessageDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid message content"),
        @ApiResponse(responseCode = "404", description = "Game session not found")
    })
    public ResponseEntity<ChatMessageDTO> createMessageInGame(
            @PathVariable @Parameter(description = "ID of the game session") Integer gameId,
            @RequestBody @Valid ChatRequestDTO chatRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        ChatMessageDTO dto =
                chatMessageService.createAndBroadcast(chatRequest, gameId, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping("{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get chat history", description = "Retrieves all chat messages for a specific game session.")
    @ApiResponse(responseCode = "200", description = "List of chat messages")
    public ResponseEntity<List<ChatMessageDTO>> getAllMessage(@PathVariable @Parameter(description = "ID of the game session") Integer gameId) {
        return ResponseEntity.ok(
                chatMessageService.findAllMessageDTOsByGameId(gameId)
        );
    }
}
