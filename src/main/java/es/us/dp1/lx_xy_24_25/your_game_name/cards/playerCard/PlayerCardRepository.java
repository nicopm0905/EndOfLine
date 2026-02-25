package es.us.dp1.lx_xy_24_25.your_game_name.cards.playerCard;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardState;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.PlayerGameSession;

@Repository
public interface PlayerCardRepository extends CrudRepository<PlayerCard, Integer> {

    List<PlayerCard> findByPlayer(PlayerGameSession player);

    List<PlayerCard> findByPlayerAndLocation(PlayerGameSession player, CardState location);

    long countByPlayer(PlayerGameSession pgs);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    void deleteAllByPlayer(PlayerGameSession pgs);

    Optional<PlayerCard> findFirstByPlayerAndLocationOrderByDeckOrderAsc(PlayerGameSession player, CardState location);

    @Query("SELECT COUNT(c) FROM PlayerCard c WHERE c.player = :player AND c.location = :location AND c.used = false")
    long countByPlayerAndLocationAndUsedFalse(@Param("player") PlayerGameSession player, @Param("location") CardState location);
}