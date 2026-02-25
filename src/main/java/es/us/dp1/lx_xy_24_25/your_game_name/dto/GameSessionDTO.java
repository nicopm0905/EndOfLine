package es.us.dp1.lx_xy_24_25.your_game_name.dto;

import java.util.Set;
import java.util.stream.Collectors;

import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameMode;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameState;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GameSessionDTO {
    private Integer id;
    private String name;
    private String host;
    private String password;
    private GameMode gameMode;
    private Integer numPlayers;
    private boolean isPrivate;
    private GameState state;
    private Set<PlayerGameSessionDTO> players;
    private Integer boardSize;
    private Integer gamePlayerTurnId;
    private Integer round;
    private String winner;
    private Integer winnerScore;
    @JsonProperty("duration_formatted")
    private String durationFormatted;

    public GameSessionDTO(GameSession gs) {
            this.id = gs.getId();
            this.name = gs.getName();
            this.host = gs.getHost();
            this.password = gs.getPassword();
            this.gameMode = gs.getGameMode();
            this.numPlayers = gs.getNumPlayers();
            this.isPrivate = gs.isPrivate();
            this.state = gs.getState();
            this.boardSize = gs.getBoardSize();
            this.gamePlayerTurnId = gs.getGamePlayerTurnId();
            this.round = gs.getRound();
            this.winner = gs.getWinner();
            this.winnerScore = gs.getWinnerScore();
            this.durationFormatted = gs.getDurationFormatted();
            if(gs.getPlayers() != null) {
                this.players = gs.getPlayers().stream().map(PlayerGameSessionDTO::new).collect(Collectors.toSet());
            }
    }
}
