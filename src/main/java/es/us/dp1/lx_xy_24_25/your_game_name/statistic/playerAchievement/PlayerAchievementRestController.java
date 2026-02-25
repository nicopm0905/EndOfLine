package es.us.dp1.lx_xy_24_25.your_game_name.statistic.playerAchievement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import es.us.dp1.lx_xy_24_25.your_game_name.auth.payload.response.MessageResponse;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.PlayerAchievementDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.BadRequestException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/player-achievements")
@Tag(name = "Player Achievements", description = "The Player Achievements management API")
@SecurityRequirement(name = "bearerAuth")
public class PlayerAchievementRestController {

    private final PlayerAchievementService playerAchievementService;

    @Autowired
    public PlayerAchievementRestController(PlayerAchievementService playerAchievementService) {
        this.playerAchievementService = playerAchievementService;
    }

    @GetMapping("/player/{playerId}")
    @Operation(summary = "Get player achievements", description = "Retrieves all achievements for a specific player.")
    @ApiResponse(responseCode = "200", description = "List of player achievements")
    public ResponseEntity<List<PlayerAchievement>> findByPlayerId(@PathVariable("playerId") @Parameter(description = "ID of the player") Integer playerId) {
        List<PlayerAchievement> achievements = playerAchievementService.findByPlayerId(playerId);
        return new ResponseEntity<>(achievements, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get player achievement by ID", description = "Retrieves a specific player achievement entry by its ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Player Achievement found"),
        @ApiResponse(responseCode = "404", description = "Player Achievement not found")
    })
    public ResponseEntity<PlayerAchievement> findPlayerAchievement(@PathVariable("id") @Parameter(description = "ID of the player achievement") Integer id) {
        PlayerAchievement achievement = playerAchievementService.findById(id);
        return new ResponseEntity<>(achievement, HttpStatus.OK);
    }

    @GetMapping("/player/{playerId}/completed")
    @Operation(summary = "Get completed achievements", description = "Retrieves a list of completed achievements for a specific player.")
    @ApiResponse(responseCode = "200", description = "List of completed achievements")
    public ResponseEntity<List<PlayerAchievement>> findCompletedByPlayerId(@PathVariable("playerId") @Parameter(description = "ID of the player") Integer playerId) {
        List<PlayerAchievement> achievements = playerAchievementService.findCompletedByPlayerId(playerId);
        return ResponseEntity.ok(achievements);
    }

    @GetMapping("/player/{playerId}/achievement/{achievementId}")
    @Operation(summary = "Get specific achievement progress", description = "Retrieves the progress of a player for a specific achievement.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Found"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<PlayerAchievement> findByPlayerAndAchievement(
            @PathVariable("playerId") @Parameter(description = "ID of the player") Integer playerId,
            @PathVariable("achievementId") @Parameter(description = "ID of the achievement") Integer achievementId) {
        PlayerAchievement achievement = playerAchievementService.findByPlayerIdAndAchievementId(playerId, achievementId);
        return new ResponseEntity<>(achievement, HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Create player achievement", description = "Creates a new player achievement tracking entry.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<PlayerAchievement> createPlayerAchievement(@RequestBody @Valid PlayerAchievement newPlayerAchievement,
                                                                   BindingResult br) {
        if (br.hasErrors())
            throw new BadRequestException(br.getAllErrors());

        PlayerAchievement result = playerAchievementService.save(newPlayerAchievement);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PutMapping("/player/{playerId}/achievement/{achievementId}/progress")
    @Operation(summary = "Update achievement progress", description = "Updates the progress value for a player's achievement.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Progress updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid progress value")
    })
    public ResponseEntity<PlayerAchievement> updateProgress(
            @PathVariable("playerId") @Parameter(description = "ID of the player") Integer playerId,
            @PathVariable("achievementId") @Parameter(description = "ID of the achievement") Integer achievementId,
            @RequestParam("progress") @Parameter(description = "New progress value") Integer progress) {

        if (progress < 0)
            throw new BadRequestException("Progress cannot be negative");

        PlayerAchievement updated = playerAchievementService.updateProgress(playerId, achievementId, progress);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update player achievement", description = "Updates an existing player achievement entry.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or ID mismatch"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<PlayerAchievement> modifyPlayerAchievement(@RequestBody @Valid PlayerAchievement newPlayerAchievement,
                                                                   BindingResult br,
                                                                   @PathVariable("id") @Parameter(description = "ID of the player achievement to update") Integer id) {
        if (br.hasErrors())
            throw new BadRequestException(br.getAllErrors());

        if (newPlayerAchievement.getId() != null && !newPlayerAchievement.getId().equals(id))
            throw new BadRequestException("PlayerAchievement id is not consistent with resource URL: " + id);

        PlayerAchievement result = playerAchievementService.modifyPlayerAchievement(id, newPlayerAchievement);
        
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete player achievement", description = "Deletes a player achievement entry.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<MessageResponse> deletePlayerAchievement(@PathVariable("id") @Parameter(description = "ID of the player achievement to delete") Integer id) {
        playerAchievementService.deleteById(id);
        return ResponseEntity.ok(new MessageResponse("Player achievement deleted successfully"));
    }

    @GetMapping("/achievement/{achievementId}/progress")
    @Operation(summary = "Get progress by achievement", description = "Retrieves progress for all players on a specific achievement.")
    @ApiResponse(responseCode = "200", description = "List of progress entries")
    public ResponseEntity<List<PlayerAchievementDTO>> getAchievementProgress(@PathVariable @Parameter(description = "ID of the achievement") Integer achievementId) {
        List<PlayerAchievementDTO> result = playerAchievementService.getAchievementProgress(achievementId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/player/{playerId}/progress")
    @Operation(summary = "Get progress by player", description = "Retrieves full progress for a specific player across all achievements.")
    @ApiResponse(responseCode = "200", description = "List of progress entries")
    public ResponseEntity<List<PlayerAchievementDTO>> getPlayerProgress(@PathVariable @Parameter(description = "ID of the player") Integer playerId) {
        List<PlayerAchievementDTO> result = playerAchievementService.getPlayerProgress(playerId);
        return ResponseEntity.ok(result);
    }
}