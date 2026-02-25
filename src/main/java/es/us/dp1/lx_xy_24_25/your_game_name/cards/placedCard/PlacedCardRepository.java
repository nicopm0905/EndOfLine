package es.us.dp1.lx_xy_24_25.your_game_name.cards.placedCard;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlacedCardRepository extends CrudRepository<PlacedCard, Integer> {

       @Query("SELECT p FROM PlacedCard p " +
           "LEFT JOIN FETCH p.template " +
           "LEFT JOIN FETCH p.placedBy pb " +
           "LEFT JOIN FETCH pb.player " +
           "WHERE p.gameSession.id = :gameSessionId")
       List<PlacedCard> findByGameSessionId(@Param("gameSessionId") Integer gameSessionId);
    
       @Query("SELECT p FROM PlacedCard p " +
           "LEFT JOIN FETCH p.template " +
           "LEFT JOIN FETCH p.placedBy pb " +
           "LEFT JOIN FETCH pb.player " +
           "WHERE p.row = :row AND p.col = :col AND p.gameSession.id = :gameSessionId")
       Optional<PlacedCard> findByRowAndColAndGameSessionId(@Param("row") Integer row, @Param("col") Integer col, @Param("gameSessionId") Integer gameSessionId);

       boolean existsByRowAndColAndGameSessionId(Integer row, Integer col, Integer gameSessionId);


       List<PlacedCard> findByGameSessionIdAndPlacedByIdOrderByPlacedAtDesc(Integer gameSessionId, Integer placedById);

       @Query("SELECT COUNT(p) FROM PlacedCard p WHERE p.gameSession.id = :gameSessionId")
       long countByGameSessionId(Integer gameSessionId);




}