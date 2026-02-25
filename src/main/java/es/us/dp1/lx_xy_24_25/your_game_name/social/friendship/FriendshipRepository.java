package es.us.dp1.lx_xy_24_25.your_game_name.social.friendship;

import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

@Repository
public interface FriendshipRepository extends CrudRepository<Friendship, Integer>{
    
    @Query("SELECT f FROM Friendship f WHERE f.sender.id = ?1 OR f.receiver.id = ?1")
    Iterable<Friendship> findAllFriendshipsByPlayerId(Integer id);
    
    @Query("SELECT f FROM Friendship f WHERE (f.sender.id = ?1 AND f.receiver.id = ?2) OR (f.sender.id = ?2 AND f.receiver.id = ?1)")
    Optional<Friendship> findFriendshipBySenderAndReceiver(Integer sender_id, Integer receiver_id);

    @Query("SELECT COUNT(f) > 0 FROM Friendship f WHERE (f.sender.id = :userId AND f.receiver.id = :friendId) OR (f.sender.id = :friendId AND f.receiver.id = :userId) AND f.state = 'ACCEPTED'")
    boolean areTheyFriends(@Param("userId") Integer userId, @Param("friendId") Integer friendId);
}