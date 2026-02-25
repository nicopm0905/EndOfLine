package es.us.dp1.lx_xy_24_25.your_game_name.statistic.achievements;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import es.us.dp1.lx_xy_24_25.your_game_name.auth.payload.response.MessageResponse;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.BadRequestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/achievements")
@Tag(name = "Achievements", description = "The Achievements management API")
@SecurityRequirement(name = "bearerAuth")
public class AchievementRestController {
    
    private final AchievementService achievementService;

    @Autowired
    public AchievementRestController(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    @GetMapping
    @Operation(summary = "Get all achievements", description = "Retrieves a list of all defined achievements.")
    @ApiResponse(responseCode = "200", description = "List of achievements")
    public ResponseEntity<List<Achievement>> findAll() {
        return ResponseEntity.ok(achievementService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get achievement by ID", description = "Retrieves a specific achievement by its ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Achievement found"),
        @ApiResponse(responseCode = "404", description = "Achievement not found")
    })
    public ResponseEntity<Achievement> findAchievement(@PathVariable("id") @Parameter(description = "ID of the achievement") Integer id) {
        return ResponseEntity.ok(achievementService.findById(id));
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get achievement by name", description = "Retrieves a specific achievement by its name.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Achievement found"),
        @ApiResponse(responseCode = "404", description = "Achievement not found")
    })
    public ResponseEntity<Achievement> findByName(@PathVariable("name") @Parameter(description = "Name of the achievement") String name) {
        return ResponseEntity.ok(achievementService.findByName(name));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Create achievement", description = "Creates a new achievement definition. Requires ADMIN authority.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Achievement created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or validation error"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Achievement> createAchievement(
            @RequestBody @Valid Achievement newAchievement, 
            BindingResult br) { 
        if (br.hasErrors())
            throw new BadRequestException(br.getAllErrors());
        
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(achievementService.save(newAchievement));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Update achievement", description = "Updates an existing achievement. Requires ADMIN authority.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Achievement updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or ID mismatch"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Achievement not found")
    })
    public ResponseEntity<Achievement> modifyAchievement(
            @RequestBody @Valid Achievement newAchievement, 
            BindingResult br,
            @PathVariable("id") @Parameter(description = "ID of the achievement to update") Integer id) {
        if (br.hasErrors())
            throw new BadRequestException(br.getAllErrors());
        
        if (newAchievement.getId() != null && !newAchievement.getId().equals(id))
            throw new BadRequestException("Achievement id is not consistent with resource URL: " + id);
        
        return ResponseEntity.ok(achievementService.update(newAchievement, id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Delete achievement", description = "Deletes an achievement. Requires ADMIN authority.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Achievement deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Achievement not found")
    })
    public ResponseEntity<MessageResponse> deleteAchievement(@PathVariable("id") @Parameter(description = "ID of the achievement to delete") Integer id) {
        achievementService.deleteById(id);
        return ResponseEntity.ok(new MessageResponse("Achievement deleted successfully"));
    }
}
