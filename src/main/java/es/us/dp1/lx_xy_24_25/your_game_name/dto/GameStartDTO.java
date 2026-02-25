package es.us.dp1.lx_xy_24_25.your_game_name.dto;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GameStartDTO {

    private Integer gameId;
    private String gameName;
    private int boardSize;
    private Set<PlayerGameSessionDTO> players;
    private String currentUsername;
    private List<PlacedCardDTO> placedCards;
    private Integer gamePlayerTurnId;
    private Integer round;

    public GameStartDTO(GameSession game, User currentUser) {
        this.gameId = game.getId();
        this.gameName = game.getName();
        this.boardSize = game.getBoardSize();
        this.currentUsername = currentUser.getUsername();
        this.gamePlayerTurnId = game.getGamePlayerTurnId();
        this.round = game.getRound();
        if (game.getPlayers() != null) {
            this.players = game.getPlayers()
                .stream()
                .map(PlayerGameSessionDTO::new)
                .collect(Collectors.toSet());
        }
        if (game.getPlacedCards() != null) {
            this.placedCards = game.getPlacedCards()
                .stream()
                .map(PlacedCardDTO::new)
                .collect(Collectors.toList());
        }
    }
}
