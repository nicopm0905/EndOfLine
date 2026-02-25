package es.us.dp1.lx_xy_24_25.your_game_name.dto;

import es.us.dp1.lx_xy_24_25.your_game_name.statistic.Metric;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerRankingDTO {
    private Integer playerId;
    private String username;
    private Integer avatarId;
    private Integer position;
    private Metric metric;
    private Number value;
    private Double winRate;
    private Integer gamesPlayed;
    
    public PlayerRankingDTO(Integer playerId, String username, Integer avatarId, Integer position, Metric metric, Number value) {
        this.playerId = playerId;
        this.username = username;
        this.avatarId = avatarId;
        this.position = position;
        this.metric = metric;
        this.value = value;
    }
}
