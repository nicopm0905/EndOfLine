package es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession;

import org.springframework.stereotype.Repository;

import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameState;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

@Repository
public interface PlayerGameSessionRepository extends CrudRepository<PlayerGameSession, Integer> {
    Optional<PlayerGameSession> findByGameSessionIdAndPlayerUsername(Integer gameSessionId, String username);


    @Query("""
    SELECT COUNT(pgs) > 0
    FROM PlayerGameSession pgs
    WHERE pgs.player.id = :playerId
      AND pgs.gameSession.state IN (:states)
""")
boolean existsActiveGameForPlayer(
    @Param("playerId") Integer playerId,
    @Param("states") Collection<GameState> states
);

}
