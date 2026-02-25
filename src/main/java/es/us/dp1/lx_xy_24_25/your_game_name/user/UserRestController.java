package es.us.dp1.lx_xy_24_25.your_game_name.user;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.AccessDeniedException;
import es.us.dp1.lx_xy_24_25.your_game_name.util.RestPreconditions;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.UserDTO;

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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "API for managing user accounts")
@SecurityRequirement(name = "bearerAuth")
class UserRestController {

	private final UserService userService;
	private final AuthoritiesService authService;

	@Autowired
	public UserRestController(UserService userService, AuthoritiesService authService) {
		this.userService = userService;
		this.authService = authService;
	}

	@GetMapping
    @Operation(summary = "Get all users", description = "Retrieves a list of all users. Can be filtered by authority.")
    @ApiResponse(responseCode = "200", description = "List of users")
	public ResponseEntity<List<UserDTO>> findAll(@RequestParam(required = false) @Parameter(description = "Authority to filter by (e.g., PLAYER, ADMIN)") String auth) {
		Iterable<User> users;
		if (auth != null) {
			users = userService.findAllByAuthority(auth);
		} else {
			users = userService.findAll();
		}
		List<UserDTO> res = StreamSupport.stream(users.spliterator(), false)
				.map(UserDTO::new)
				.collect(Collectors.toList());
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	@GetMapping("authorities")
    @Operation(summary = "Get authorities", description = "Retrieves all available authorities.")
    @ApiResponse(responseCode = "200", description = "List of authorities")
	public ResponseEntity<List<Authorities>> findAllAuths() {
		List<Authorities> res = (List<Authorities>) authService.findAll();
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	@GetMapping(value = "{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves a specific user by their ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
	public ResponseEntity<UserDTO> findById(@PathVariable("id") @Parameter(description = "ID of the user") Integer id) {
		return new ResponseEntity<>(new UserDTO(userService.findUser(id)), HttpStatus.OK);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create user", description = "Creates a new user account.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
	public ResponseEntity<UserDTO> create(@RequestBody @Valid User user) {
		User savedUser = userService.saveUser(user);
		return new ResponseEntity<>(new UserDTO(savedUser), HttpStatus.CREATED);
	}

	@PutMapping(value = "{userId}")
	@ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update user", description = "Updates an existing user account.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
	public ResponseEntity<UserDTO> update(@PathVariable("userId") @Parameter(description = "ID of the user to update") Integer id, @RequestBody @Valid User user) {
		RestPreconditions.checkNotNull(userService.findUser(id), "User", "ID", id);
		return new ResponseEntity<>(new UserDTO(this.userService.updateUser(user, id)), HttpStatus.OK);
	}

	@DeleteMapping(value = "{userId}")
	@ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete user", description = "Deletes a user account. Cannot delete self.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Cannot delete self or access denied"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
	public ResponseEntity<MessageResponse> delete(@PathVariable("userId") @Parameter(description = "ID of the user to delete") int id) {
		RestPreconditions.checkNotNull(userService.findUser(id), "User", "ID", id);

		User currentUser = userService.findCurrentUser();

		if (currentUser.getId().equals(id)) {
			throw new AccessDeniedException("You cannot delete your own user account");
		}
		userService.deleteUser(id);
		return new ResponseEntity<>(new MessageResponse("User deleted!"), HttpStatus.OK);
	}

	@GetMapping(value = "username/{username}")
	@PreAuthorize("hasAnyAuthority('ADMIN') or #username == authentication.principal.username")
    @Operation(summary = "Get user by username", description = "Retrieves a specific user by their username.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
	public ResponseEntity<UserDTO> findbyUsername(@PathVariable("username") @Parameter(description = "Username of the user") String username) {
		User user = userService.findByUsername(username);
		if (user != null) {
			return new ResponseEntity<>(new UserDTO(user), HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

}
