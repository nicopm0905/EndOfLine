package es.us.dp1.lx_xy_24_25.your_game_name.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
public class PlayerAchievementDTO {
    private Integer playerId;
    private Integer achievementId;
    private String username;
    private Integer progress;
    private Double threshold;
    private Boolean completed;
}
