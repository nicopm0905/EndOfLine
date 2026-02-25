package es.us.dp1.lx_xy_24_25.your_game_name.cards.playerCard;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.us.dp1.lx_xy_24_25.your_game_name.dto.PlayerCardDTO;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/api/v1/cards")
@Tag(name = "Player Cards", description = "API for managing player hands and cards")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class PlayerCardController {

    
    private final PlayerCardService playerCardService;

    @GetMapping("/player-hand/{playerGameSessionId}")
    @Operation(summary = "Get player hand", description = "Retrieves the cards currently in the player's hand.")
    @ApiResponse(responseCode = "200", description = "List of cards in hand")
    public ResponseEntity<List<PlayerCardDTO>> getPlayerHand(@PathVariable @Parameter(description = "ID of the player's game session") Integer playerGameSessionId) {
        List<PlayerCardDTO> hand = playerCardService.getPlayerHand(playerGameSessionId);
        return ResponseEntity.ok(hand);
    }

    @PutMapping("/{playerGameSessionId}/reroll-hand")
    @Operation(summary = "Reroll hand", description = "Discards current hand and draws new cards (specific game mechanism).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Hand rerolled successfully"),
        @ApiResponse(responseCode = "404", description = "Player game session not found")
    })
    public ResponseEntity<?> rerollHand(@PathVariable @Parameter(description = "ID of the player's game session") Integer playerGameSessionId) {
        playerCardService.rerollHand(playerGameSessionId);
        return ResponseEntity.ok("Hand rerolled successfully");
    }
}
