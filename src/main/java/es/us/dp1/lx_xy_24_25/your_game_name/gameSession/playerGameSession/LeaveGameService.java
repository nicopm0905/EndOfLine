package es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.dto.GameSessionDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSessionService;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LeaveGameService {

    private final GameSessionService gameService;
    private final PlayerService playerService;
    private final PlayerGameSessionService playerGameSessionService;
    private final SimpMessagingTemplate messagingTemplate;

@Transactional
public ResponseEntity<?> leaveGame(Integer gameId, String username) {

    GameSession session = gameService.getGameById(gameId);
    if (session == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Game not found");
    }

    Player player = playerService.findPlayer(username);

    
    if (session.getHost().equals(username)) {
        notifyLobbyDeleted(gameId);
        gameService.delete(session.getId());
        return ResponseEntity.ok("Host left: game deleted and all players expelled");
    }

    notifyLobbyUpdate(gameId);

  
    PlayerGameSession pgs = session.getPlayers()
            .stream()
            .filter(x -> x.getPlayer() != null
                    && x.getPlayer().getId().equals(player.getId()))
            .findFirst()
            .orElse(null);

    if (pgs == null) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Player not in the game");
    }

    session.getPlayers().remove(pgs);

    playerGameSessionService.delete(pgs);

    notifyLobbyUpdate(gameId);

    return ResponseEntity.ok("Left successfully");
}


    private void notifyLobbyUpdate(Integer gameId) {
        GameSession updated = gameService.getGameById(gameId);
        messagingTemplate.convertAndSend(
                "/topic/lobby/" + gameId,
                new GameSessionDTO(updated)
        );
    }

    private void notifyLobbyDeleted(Integer gameId) {
        java.util.Map\u003cString, Object\u003e payload = new java.util.HashMap\u003c\u003e();
        payload.put("deleted", true);
        payload.put("gameId", gameId);
        messagingTemplate.convertAndSend(
                "/topic/lobby/" + gameId,
                payload
        );
    }
}

