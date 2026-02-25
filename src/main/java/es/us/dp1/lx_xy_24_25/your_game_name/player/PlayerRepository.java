package es.us.dp1.lx_xy_24_25.your_game_name.player;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Integer> {
    Optional<Player> findByUsername(String username);

	Boolean existsByUsername(String username);

	@Query("SELECT COUNT(*) > 0 FROM Player p WHERE p.id = ?1 AND p.authority.authority = 'PLAYER'")
    public Boolean existsPlayerById(Integer id);

	Optional<Player> findById(Integer id);

	@Query("SELECT u FROM Player u WHERE u.authority.authority = :auth")
	Iterable<Player> findAllByAuthority(String auth);

}
    

