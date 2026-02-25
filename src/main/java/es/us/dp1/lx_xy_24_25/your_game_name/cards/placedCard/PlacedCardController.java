package es.us.dp1.lx_xy_24_25.your_game_name.cards.placedCard;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.us.dp1.lx_xy_24_25.your_game_name.dto.PlaceCardRequestDTO; 
import es.us.dp1.lx_xy_24_25.your_game_name.dto.PlacedCardDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.SpectatorGameDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSessionService;
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
@RequestMapping("/api/v1/gamesessions")
@Tag(name = "Placed Cards", description = "API for managing cards placed on the board")
@SecurityRequirement(name = "bearerAuth")
public class PlacedCardController { 

    private final GameSessionService gameSessionService; 
    private final PlacedCardRepository placedCardRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public PlacedCardController(GameSessionService gameSessionService, PlacedCardRepository placedCardRepository, SimpMessagingTemplate messagingTemplate) {
        this.gameSessionService = gameSessionService;
        this.placedCardRepository = placedCardRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/{gameSessionId}/place-card")
    @Operation(summary = "Place a card", description = "Places a card on the game board.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Card placed successfully",
            content = @Content(schema = @Schema(implementation = PlacedCardDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid move or card placement rule violation"),
        @ApiResponse(responseCode = "404", description = "Game session not found")
    })
    public ResponseEntity<PlacedCardDTO> placeCard( @PathVariable @Parameter(description = "ID of the game session") Integer gameSessionId, @RequestBody @Valid PlaceCardRequestDTO request, Authentication authentication) {
        PlacedCard newPlacedCard = gameSessionService.handleCardPlacement(gameSessionId, authentication.getName(), request);
        PlacedCardDTO responseDTO = new PlacedCardDTO(newPlacedCard);
        
        GameSession updatedGame = gameSessionService.getGameById(gameSessionId);
        SpectatorGameDTO spectatorDTO = new SpectatorGameDTO(updatedGame);
        messagingTemplate.convertAndSend("/topic/spectate/" + gameSessionId, spectatorDTO);
        messagingTemplate.convertAndSend("/topic/game/" + gameSessionId, spectatorDTO);
        
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{gameSessionId}/placed-cards")
    @Operation(summary = "Get placed cards", description = "Retrieves all cards currently placed on the board for a specific game.")
    @ApiResponse(responseCode = "200", description = "List of placed cards")
    public ResponseEntity<List<PlacedCardDTO>> getPlacedCards(@PathVariable @Parameter(description = "ID of the game session") Integer gameSessionId, Authentication authentication) {
        List<PlacedCard> placedCards = placedCardRepository.findByGameSessionId(gameSessionId);
        
        List<PlacedCardDTO> response = placedCards.stream()
            .map(PlacedCardDTO::new)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{gameSessionId}/placed-cards/position")
    @Operation(summary = "Get card at position", description = "Retrieves the card placed at a specific row and column.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Card found",
            content = @Content(schema = @Schema(implementation = PlacedCardDTO.class))),
        @ApiResponse(responseCode = "404", description = "No card found at the specified position")
    })
    public ResponseEntity<PlacedCardDTO> getCardAtPosition( @PathVariable @Parameter(description = "ID of the game session") Integer gameSessionId, @RequestBody PositionRequestDTO position, Authentication authentication) {
        
        PlacedCard card = placedCardRepository
            .findByRowAndColAndGameSessionId(position.getRow(), position.getCol(), gameSessionId)
            .orElseThrow(() -> new ResourceNotFoundException("No card found at position (" + position.getRow() + ", " + position.getCol() + ")"));

        return ResponseEntity.ok(new PlacedCardDTO(card));
    }


    public static class PositionRequestDTO {
        private Integer row;
        private Integer col;

        public Integer getRow() { return row; }
        public void setRow(Integer row) { this.row = row; }
        public Integer getCol() { return col; }
        public void setCol(Integer col) { this.col = col; }
    }

}