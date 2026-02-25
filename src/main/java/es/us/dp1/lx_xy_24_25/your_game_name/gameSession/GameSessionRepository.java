package es.us.dp1.lx_xy_24_25.your_game_name.gameSession;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface GameSessionRepository extends CrudRepository<GameSession, Integer> {


List<GameSession> findAll();

List<GameSession> findByState(GameState state);

@Query("SELECT gs FROM GameSession gs LEFT JOIN FETCH gs.players WHERE gs.id = :id")
Optional<GameSession> findByIdWithPlayers(@Param("id") Integer id);

@Query("""
        SELECT DISTINCT gs FROM GameSession gs
        LEFT JOIN FETCH gs.players p
        LEFT JOIN FETCH gs.placedCards pc
        LEFT JOIN FETCH pc.template
        WHERE gs.id = :id
    """)
Optional<GameSession> findByIdWithPlayersAndPlacedCards(@Param("id") Integer id);

}
