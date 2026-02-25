package es.us.dp1.lx_xy_24_25.your_game_name.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameMode;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameState;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SpectatorGameDTO {
    private Integer gameId;
    private String gameName;
    private GameMode gameMode;
    private Integer boardSize;
    private Integer round;
    private GameState state;
    private String winner;
    private Integer gamePlayerTurnId;
    private LocalDateTime startTime;
    private Set<PlayerGameSessionDTO> players;
    private List<PlacedCardDTO> placedCards;

    public SpectatorGameDTO(GameSession game) {
        this.gameId = game.getId();
        this.gameName = game.getName();
        this.gameMode = game.getGameMode();
        this.boardSize = game.getBoardSize();
        this.round = game.getRound();
        this.state = game.getState();
        this.winner = game.getWinner();
        this.gamePlayerTurnId = game.getGamePlayerTurnId();
        this.startTime = game.getStartTime();

        if (game.getPlayers() != null) {
            this.players = game.getPlayers().stream()
                .map(PlayerGameSessionDTO::new)
                .collect(Collectors.toSet());
        }
        if (game.getPlacedCards() != null) {
            this.placedCards = game.getPlacedCards().stream()
                .map(PlacedCardDTO::new)
                .collect(Collectors.toList());
        }
    }
}
