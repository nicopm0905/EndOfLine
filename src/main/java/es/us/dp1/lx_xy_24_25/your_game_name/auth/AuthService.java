package es.us.dp1.lx_xy_24_25.your_game_name.auth;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import es.us.dp1.lx_xy_24_25.your_game_name.user.Authorities;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import es.us.dp1.lx_xy_24_25.your_game_name.auth.payload.request.SignupRequest;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.AuthoritiesService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;

@Service
public class AuthService {

	private final PasswordEncoder encoder;
	private final AuthoritiesService authoritiesService;
	private final UserService userService;
	private final PlayerService playerService;

	@Autowired
	public AuthService(PasswordEncoder encoder, AuthoritiesService authoritiesService, UserService userService,
			PlayerService playerService) {
		this.encoder = encoder;
		this.authoritiesService = authoritiesService;
		this.userService = userService;
		this.playerService = playerService;
	}

	@Transactional
	public void createUser(@Valid SignupRequest request) {
		Integer authority = Integer.valueOf(request.getAuthority());
		Authorities role;

		if (authority != null && authority == 2) {
			Player player = new Player();
			player.setFirstName(request.getFirstName());
			player.setLastName(request.getLastName());
			player.setEmail(request.getEmail());
			player.setUsername(request.getUsername());
			player.setPassword(encoder.encode(request.getPassword()));
			player.setAvatarId(request.getAvatarId());
			role = authoritiesService.findByAuthority("PLAYER");
			player.setAuthority(role);
			playerService.savePlayer(player);
		} else {
			User user = new User();
			user.setFirstName(request.getFirstName());
			user.setLastName(request.getLastName());
			user.setEmail(request.getEmail());
			user.setUsername(request.getUsername());
			user.setPassword(request.getPassword());
			user.setAvatarId(request.getAvatarId());
			role = authoritiesService.findByAuthority("ADMIN");
			user.setAuthority(role);
			userService.saveUser(user);
		}
	}

}
