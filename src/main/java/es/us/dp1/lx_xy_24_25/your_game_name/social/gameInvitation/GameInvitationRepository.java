package es.us.dp1.lx_xy_24_25.your_game_name.social.gameInvitation;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;

@Repository
public interface GameInvitationRepository extends CrudRepository<GameInvitation, Integer> {
    List<GameInvitation> findByReceiverAndStatus(Player receiver, InvitationStatus status);

    List<GameInvitation> findByReceiver(Player receiver);

    List<GameInvitation> findByGameAndStatus(GameSession game, InvitationStatus status);

    Optional<GameInvitation> findBySenderAndReceiverAndGame(Player sender, Player receiver, GameSession game);

    List<GameInvitation> findBySender(Player sender);

    long countByReceiverAndStatus(Player receiver, InvitationStatus status);
}
