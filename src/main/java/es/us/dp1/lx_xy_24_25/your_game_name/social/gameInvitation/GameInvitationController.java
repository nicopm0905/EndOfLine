package es.us.dp1.lx_xy_24_25.your_game_name.social.gameInvitation;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

import es.us.dp1.lx_xy_24_25.your_game_name.dto.CreateGameInvitationDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.GameInvitationResponseDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSessionService;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/invitations")
@Tag(name = "Game Invitations", description = "API for managing game invitations between players")
@SecurityRequirement(name = "bearerAuth")
public class GameInvitationController {
    private final GameInvitationService gameInvitationService;
    private final PlayerService playerService;
    private final GameSessionService gameSessionService;

    @Autowired
    public GameInvitationController(GameInvitationService gameInvitationService,
                                    PlayerService playerService,
                                    GameSessionService gameSessionService) {
        this.gameInvitationService = gameInvitationService;
        this.playerService = playerService;
        this.gameSessionService = gameSessionService;
    }

    @PostMapping
    @Operation(summary = "Create a new invitation", description = "Creates a new game invitation from the current user to another player.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Invitation created successfully", 
            content = @Content(schema = @Schema(implementation = GameInvitationResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input or business rule violation (e.g., already invited)"),
        @ApiResponse(responseCode = "404", description = "Receiver or Game Session not found")
    })
    public ResponseEntity<?> createInvitation(@Valid @RequestBody CreateGameInvitationDTO dto,
                                             BindingResult result,
                                             Authentication authentication) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        Player inviter = playerService.findByUsername(authentication.getName());
        Player invitee = playerService.findPlayer(dto.getReceiverId());
        GameSession gameSession = gameSessionService.getGameById(dto.getGameSessionId());

        GameInvitation invitation = gameInvitationService.createInvitation(inviter, invitee, gameSession, dto.getType());

        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(invitation));
    }

    @GetMapping("/received")
    @Operation(summary = "Get received invitations", description = "Retrieves all invitations received by the current user.")
    @ApiResponse(responseCode = "200", description = "List of received invitations")
    public ResponseEntity<List<GameInvitationResponseDTO>> getReceivedInvitations(Authentication authentication) {
        Player player = playerService.findByUsername(authentication.getName());
        List<GameInvitation> invitations = gameInvitationService.getReceivedInvitations(player);
        return ResponseEntity.ok(invitations.stream().map(this::mapToDTO).collect(Collectors.toList()));
    }

    @GetMapping("/received/pending")
    @Operation(summary = "Get pending received invitations", description = "Retrieves only the pending invitations received by the current user.")
    @ApiResponse(responseCode = "200", description = "List of pending invitations")
    public ResponseEntity<List<GameInvitationResponseDTO>> getPendingInvitations(Authentication authentication) {
        Player player = playerService.findByUsername(authentication.getName());
        List<GameInvitation> invitations = gameInvitationService.getPendingReceivedInvitations(player);
        return ResponseEntity.ok(invitations.stream().map(this::mapToDTO).collect(Collectors.toList()));
    }

    @GetMapping("/sent")
    @Operation(summary = "Get sent invitations", description = "Retrieves all invitations sent by the current user.")
    @ApiResponse(responseCode = "200", description = "List of sent invitations")
    public ResponseEntity<List<GameInvitationResponseDTO>> getSentInvitations(Authentication authentication) {
        Player player = playerService.findByUsername(authentication.getName());
        List<GameInvitation> invitations = gameInvitationService.getSentInvitations(player);
        return ResponseEntity.ok(invitations.stream().map(this::mapToDTO).collect(Collectors.toList()));
    }

    @GetMapping("/pending-count")
    @Operation(summary = "Get pending invitation count", description = "Returns the count of pending invitations for the current user.")
    @ApiResponse(responseCode = "200", description = "Count of pending invitations")
    public ResponseEntity<Long> getPendingCount(Authentication authentication) {
        Player player = playerService.findByUsername(authentication.getName());
        long count = gameInvitationService.getPendingInvitationCount(player);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{invitationId}/accept")
    @Operation(summary = "Accept an invitation", description = "Accepts a pending invitation and joins the player to the game.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Invitation accepted successfully"),
        @ApiResponse(responseCode = "404", description = "Invitation not found"),
        @ApiResponse(responseCode = "400", description = "Invitation is not pending or other violation")
    })
    public ResponseEntity<?> acceptInvitation(@PathVariable @Parameter(description = "ID of the invitation to accept") Integer invitationId,
                                             Authentication authentication) {
        GameInvitation invitation = gameInvitationService.acceptInvitation(invitationId);
        return ResponseEntity.ok(mapToDTO(invitation));
    }

    @PutMapping("/{invitationId}/reject")
    @Operation(summary = "Reject an invitation", description = "Rejects a pending invitation.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Invitation rejected successfully"),
        @ApiResponse(responseCode = "404", description = "Invitation not found")
    })
    public ResponseEntity<?> rejectInvitation(@PathVariable @Parameter(description = "ID of the invitation to reject") Integer invitationId,
                                             Authentication authentication) {
        GameInvitation invitation = gameInvitationService.rejectInvitation(invitationId);
        return ResponseEntity.ok(mapToDTO(invitation));
    }

    @PutMapping("/{invitationId}/cancel")
    @Operation(summary = "Cancel an invitation", description = "Cancels a sent invitation (can only be done by the sender).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Invitation canceled successfully"),
        @ApiResponse(responseCode = "404", description = "Invitation not found")
    })
    public ResponseEntity<?> cancelInvitation(@PathVariable @Parameter(description = "ID of the invitation to cancel") Integer invitationId,
                                             Authentication authentication) {
        GameInvitation invitation = gameInvitationService.cancelInvitation(invitationId);
        return ResponseEntity.ok(mapToDTO(invitation));
    }

    private GameInvitationResponseDTO mapToDTO(GameInvitation invitation) {
        return new GameInvitationResponseDTO(
            invitation.getId(),
            invitation.getSender().getUsername(),
            invitation.getReceiver().getUsername(),
            invitation.getGame().getId(),
            invitation.getGame().getName(),
            invitation.getStatus(),
            invitation.getInvitationType(),
            invitation.getCreatedAt(),
            invitation.getRespondedAt()
        );
    }
}
