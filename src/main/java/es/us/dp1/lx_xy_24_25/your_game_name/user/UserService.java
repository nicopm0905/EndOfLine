package es.us.dp1.lx_xy_24_25.your_game_name.user;

import jakarta.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerService;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthoritiesService authoritiesService;
	private final PlayerService playerService;

	@Autowired
	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
			AuthoritiesService authoritiesService, PlayerService playerService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.authoritiesService = authoritiesService;
		this.playerService = playerService;
	}

	@Transactional
	public User saveUser(User user) throws DataAccessException {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		Authorities authority = authoritiesService.findByAuthority(user.getAuthority().getAuthority());

		if ("PLAYER".equals(authority.getAuthority())) {
			Player player = new Player();
			BeanUtils.copyProperties(user, player, "password", "authority");
			player.setPassword(user.getPassword());
			player.setAuthority(authority);
			return playerService.savePlayer(player);
		} else {
			user.setAuthority(authority);
			userRepository.save(user);
			return user;
		}
	}

	@Transactional(readOnly = true)
	public User findUser(String username) {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
	}

	@Transactional(readOnly = true)
	public User findUser(Integer id) {
		return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
	}

	@Transactional(readOnly = true)
	public User findCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null)
			throw new ResourceNotFoundException("Nobody authenticated!");
		else
			return userRepository.findByUsername(auth.getName())
					.orElseThrow(() -> new ResourceNotFoundException("User", "Username", auth.getName()));
	}

	public Boolean existsUser(String username) {
		return userRepository.existsByUsername(username);
	}

	@Transactional(readOnly = true)
	public Iterable<User> findAll() {
		return userRepository.findAll();
	}

	public Iterable<User> findAllByAuthority(String auth) {
		return userRepository.findAllByAuthority(auth);
	}

	@Transactional
	public User updateUser(@Valid User user, Integer idToUpdate) {
		User toUpdate = findUser(idToUpdate);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		BeanUtils.copyProperties(user, toUpdate, "id");
		userRepository.save(toUpdate);
		return toUpdate;
	}

	@Transactional
	public void deleteUser(Integer id) {
		User toDelete = findUser(id);
		this.userRepository.delete(toDelete);
	}

	public User findByUsername(String username) {
		return userRepository.findByUsername(username)
				.orElse(null);
	}

}
