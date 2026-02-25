package es.us.dp1.lx_xy_24_25.your_game_name.gameSession;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.placedCard.PlacedCard;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.PlayerGameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.model.NamedEntity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Entity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import java.time.LocalDateTime;
import java.time.Duration;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.PowerName;

@Entity
@Getter
@Setter
@Table(name = "game_sessions")
public class GameSession extends NamedEntity {

    @NotBlank
    private String host;

    private String password;

    @Min(0)
    private Integer round = 0;

    @Enumerated(EnumType.STRING)
    private PowerName effect;

    @Min(5)
    private Integer boardSize;

    @NotNull
    @Min(value = 1, message = "There must be at least 1 player")
    @Max(value = 8, message = "Cannot have more than 8 players")
    @Column(name = "max_players")
    private Integer numPlayers;

    @NotNull
    @Enumerated(EnumType.STRING)
    private GameMode gameMode;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    private Integer gamePlayerTurnId;

    private Long duration;

    @Enumerated(EnumType.STRING)
    private GameState state;

    private boolean isPrivate;

    private String winner;

    private Integer winnerScore;

    @NotNull
    @OneToMany(mappedBy = "gameSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<PlayerGameSession> players = new HashSet<>();

    @OneToMany(mappedBy = "gameSession", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlacedCard> placedCards = new ArrayList<>();

    @JsonProperty("duration_formatted")
    public String getDurationFormatted() {
        if (startTime != null && endTime != null) {
            Duration duration = Duration.between(startTime, endTime);
            long hours = duration.toHours();
            long minutes = duration.toMinutes() % 60;
            long seconds = duration.getSeconds() % 60;
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        return "Not available";
    }

    @JsonProperty("player_count")
    public int getPlayersCount() {
        return this.players.size();
    }

}
