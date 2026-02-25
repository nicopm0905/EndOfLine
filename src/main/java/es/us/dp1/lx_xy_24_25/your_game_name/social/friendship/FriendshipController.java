package es.us.dp1.lx_xy_24_25.your_game_name.social.friendship;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import es.us.dp1.lx_xy_24_25.your_game_name.dto.FriendshipRequestDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.FriendshipDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/friendships")
@Tag(name = "Friendships", description = "API for managing friendships between players")
@SecurityRequirement(name = "bearerAuth")
public class FriendshipController {
    
    @Autowired
    FriendshipService friendshipService;

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all friendships", description = "Retrieves a list of all friendships involving the current user.")
    @ApiResponse(responseCode = "200", description = "List of friendships")
    public ResponseEntity<List<FriendshipDTO>> getAllFriendships() {
        return new ResponseEntity<>(friendshipService.findAllDtosWithStatus(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get friendship by ID", description = "Retrieves a specific friendship by its ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Friendship found"),
        @ApiResponse(responseCode = "404", description = "Friendship not found")
    })
    public ResponseEntity<FriendshipDTO> findById(@PathVariable @Parameter(description = "ID of the friendship") Integer id) {
        return new ResponseEntity<>(friendshipService.findDtoById(id), HttpStatus.OK);
    }
    
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create friendship", description = "Send a friendship request or create a friendship.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Friendship created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<FriendshipDTO> createFriendship(@Valid @RequestBody FriendshipRequestDTO requestDTO) {
        return new ResponseEntity<>(friendshipService.createDto(requestDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update friendship", description = "Updates a friendship (e.g., accept/reject request).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Friendship updated successfully"),
        @ApiResponse(responseCode = "404", description = "Friendship not found")
    })
    public ResponseEntity<FriendshipDTO> update(@PathVariable @Parameter(description = "ID of the friendship") Integer id, @RequestBody @Valid FriendshipRequestDTO requestDTO) {
        return new ResponseEntity<>(friendshipService.updateDto(id, requestDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete friendship", description = "Deletes/Removes a friendship.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Friendship deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Friendship not found")
    })
    public void delete(@PathVariable @Parameter(description = "ID of the friendship") Integer id) {
        friendshipService.delete(id);
    }
}
