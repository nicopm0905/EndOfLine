package es.us.dp1.lx_xy_24_25.your_game_name.social.chatMessage;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends CrudRepository<ChatMessage, Integer> {

    @Query("select m from ChatMessage m where m.playerGameSession.gameSession.id = :gameId order by m.id")
    List<ChatMessage> findAllMessagesByGameId(@Param("gameId") Integer gameId);
    
}
