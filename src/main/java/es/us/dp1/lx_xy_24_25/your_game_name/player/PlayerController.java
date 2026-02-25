package es.us.dp1.lx_xy_24_25.your_game_name.player;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import es.us.dp1.lx_xy_24_25.your_game_name.auth.payload.response.MessageResponse;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.PlayerDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.AccessDeniedException;
import es.us.dp1.lx_xy_24_25.your_game_name.user.Authorities;
import es.us.dp1.lx_xy_24_25.your_game_name.user.AuthoritiesService;
import es.us.dp1.lx_xy_24_25.your_game_name.util.RestPreconditions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/players")
@Tag(name = "Players", description = "API for managing player profiles and information")
@SecurityRequirement(name = "bearerAuth")
public class PlayerController {
    
    private final PlayerService playerService;
	private final AuthoritiesService authService;

	@Autowired
	public PlayerController(PlayerService playerService, AuthoritiesService authService) {
		this.playerService = playerService;
		this.authService = authService;
	}

    @GetMapping
    @Operation(summary = "Get all players", description = "Retrieves a list of all players. Can be filtered by authority.")
    @ApiResponse(responseCode = "200", description = "List of players")
	public ResponseEntity<List<Player>> findAll(@RequestParam(required = false) @Parameter(description = "Authority to filter by (e.g., PLAYER, ADMIN)") String auth) {
		List<Player> res;
		if (auth != null) {
			res = (List<Player>) playerService.findAllByAuthority(auth);
		} else
			res = (List<Player>) playerService.findAll();
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	@GetMapping("authorities")
    @Operation(summary = "Get all authorities", description = "Retrieves all available user authorities.")
    @ApiResponse(responseCode = "200", description = "List of authorities")
	public ResponseEntity<List<Authorities>> findAllAuths() {
		List<Authorities> res = (List<Authorities>) authService.findAll();
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	@GetMapping(value = "{id}")
    @Operation(summary = "Get player by ID", description = "Retrieves a specific player by their ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Player found"),
        @ApiResponse(responseCode = "404", description = "Player not found")
    })
	public ResponseEntity<Player> findById(@PathVariable("id") @Parameter(description = "ID of the player") Integer id) {
		return new ResponseEntity<>(playerService.findPlayer(id), HttpStatus.OK);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a player", description = "Creates a new player profile.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Player created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
	public ResponseEntity<Player> create(@RequestBody @Valid Player player) {
		Player savedPlayer = playerService.savePlayer(player);
		return new ResponseEntity<>(savedPlayer, HttpStatus.CREATED);
	}

	@PutMapping(value = "{playerId}")
	@ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update a player", description = "Updates an existing player's profile.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Player updated successfully"),
        @ApiResponse(responseCode = "404", description = "Player not found")
    })
	public ResponseEntity<Player> update(@PathVariable("playerId") @Parameter(description = "ID of the player to update") Integer id, @RequestBody @Valid Player player) {
		RestPreconditions.checkNotNull(playerService.findPlayer(id), "Player", "ID", id);
		return new ResponseEntity<>(this.playerService.updatePlayer(player, id), HttpStatus.OK);
	}

	@DeleteMapping(value = "{playerId}")
	@ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete a player", description = "Deletes a player profile. Requires ADMIN authority or self-deletion.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Player deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Player not found")
    })
	public ResponseEntity<MessageResponse> delete(@PathVariable("playerId") @Parameter(description = "ID of the player to delete") int id) {
		    RestPreconditions.checkNotNull(playerService.findPlayer(id), "Player", "ID", id);
    
		Player currentPlayer = playerService.findCurrentPlayer();
		
		if (currentPlayer.hasAuthority("ADMIN") || currentPlayer.getId().equals(id)) {
			playerService.deletePlayer(id);
			return new ResponseEntity<>(new MessageResponse("Player deleted!"), HttpStatus.OK);
		} else {
			throw new AccessDeniedException("You do not have permission to delete this user");
		}
	}

	@GetMapping(value = "username/{username}")
    @Operation(summary = "Get player by username", description = "Retrieves a specific player by their username.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Player found"),
        @ApiResponse(responseCode = "404", description = "Player not found")
    })
	public ResponseEntity<PlayerDTO> findbyUsername(@PathVariable("username") @Parameter(description = "Username of the player") String username) {
		Player player = playerService.findByUsername(username);
		if(player != null){
			PlayerDTO dto = new PlayerDTO(player);
			return new ResponseEntity<>(dto, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
}
