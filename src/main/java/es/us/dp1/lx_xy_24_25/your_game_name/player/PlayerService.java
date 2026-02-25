package es.us.dp1.lx_xy_24_25.your_game_name.player;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.playerStatistics.PlayerStatisticsService;
import jakarta.validation.Valid;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerStatisticsService playerStatisticsService;


    @Autowired
	public PlayerService(PlayerRepository playerRepository, PlayerStatisticsService playerStatisticsService) {
		this.playerRepository = playerRepository;
		this.playerStatisticsService = playerStatisticsService;
	}

    @Transactional
	public Player savePlayer(Player player) throws DataAccessException {
		Player newPlayer = playerRepository.save(player);
		playerStatisticsService.createForPlayer(newPlayer);
		return newPlayer;
	}


	@Transactional(readOnly = true)
	public Player findPlayer(String username) {
		return playerRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("Player", "username", username));
	}

	@Transactional(readOnly = true)
	public Player findPlayer(Integer id) {
		return playerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Player", "id", id));
	}

	@Transactional(readOnly = true)
	public Player findCurrentPlayer() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null)
			throw new ResourceNotFoundException("Nobody authenticated!");
		else
			return playerRepository.findByUsername(auth.getName())
					.orElseThrow(() -> new ResourceNotFoundException("Player", "Username", auth.getName()));
	}

	public Boolean existPlayer(String username) {
		return playerRepository.existsByUsername(username);
	}

	@Transactional(readOnly = true)
	public Iterable<Player> findAll() {
		return playerRepository.findAll();
	}

	public Iterable<Player> findAllByAuthority(String auth) {
		return playerRepository.findAllByAuthority(auth);
	}

	@Transactional
	public Player updatePlayer(@Valid Player player, Integer idToUpdate) {
		Player toUpdate = findPlayer(idToUpdate);
		BeanUtils.copyProperties(player, toUpdate, "id");
		playerRepository.save(toUpdate);

		return toUpdate;
	}

	@Transactional
	public void deletePlayer(Integer id) {
		Player toDelete = findPlayer(id);
		this.playerRepository.delete(toDelete);
	}

	public Player findByUsername(String username) {
    return playerRepository.findByUsername(username)
        .orElse(null);
	}
    
}
