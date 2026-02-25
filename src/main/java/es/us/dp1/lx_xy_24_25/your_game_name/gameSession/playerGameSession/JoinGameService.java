package es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.Color;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.GameSessionDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.JoinRequestDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.BadRequestException;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSessionService;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameState;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameMode;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JoinGameService {

    private final GameSessionService gameService;
    private final PlayerService playerService;
    private final PlayerGameSessionService playerGameSessionService;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public ResponseEntity<?> joinPlayerToGame(Integer gameId, String username, JoinRequestDTO joinRequest) {
        GameSession session = gameService.getGameById(gameId);
        if (session == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Game not found");
        }

        if (session.getState() != GameState.PENDING) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Game is not open for joining");
        }

        Player player = playerService.findPlayer(username);
        
        boolean alreadyJoined = session.getPlayers()
                .stream()
                .anyMatch(pgs -> pgs.getPlayer().equals(player));

        if (alreadyJoined) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Player already joined");
        }

        if (session.getPlayers().size() >= session.getNumPlayers()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Game is full");
        }
        if (playerGameSessionService.playerHasActiveGame(player)) {
            throw new BadRequestException(
        "Player is already participating in another game"
            );
        }

        if (session.isPrivate()) {
            if (joinRequest == null || joinRequest.getPassword() == null || joinRequest.getPassword().isBlank()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"message\": \"Password required for this private game\"}");
            }

            if (!session.getPassword().equals(joinRequest.getPassword())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("{\"message\": \"Invalid password\"}");
            }
        }

        Color nextAvailableColor = getNextAvailableColor(session);

        PlayerGameSession pgs = playerGameSessionService.createPlayerForGame(session, player, nextAvailableColor);

        if (session.getGameMode() == GameMode.TEAMBATTLE) {
            long team1Count = session.getPlayers().stream()
                    .filter(p -> p.getTeamNumber() != null && p.getTeamNumber() == 1)
                    .count();
            long team2Count = session.getPlayers().stream()
                    .filter(p -> p.getTeamNumber() != null && p.getTeamNumber() == 2)
                    .count();

            Integer team = (team1Count <= team2Count) ? 1 : 2;
            pgs.setTeamNumber(team);
        }

        playerGameSessionService.save(pgs);

        notifyLobbyUpdate(gameId);

        return ResponseEntity.ok("Joined successfully");
    }

    private Color getNextAvailableColor(GameSession session) {
        Set<Color> used = session.getPlayers().stream()
                .map(PlayerGameSession::getPlayerColor)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (Color c : Color.values()) {
            if (!used.contains(c)) {
                return c;
            }
        }
        return null;
    }

    private void notifyLobbyUpdate(Integer gameId) {
        GameSession updated = gameService.getGameById(gameId);
        GameSessionDTO dto = new GameSessionDTO(updated);
        messagingTemplate.convertAndSend("/topic/lobby/" + gameId, dto);
    }
}
