package es.us.dp1.lx_xy_24_25.your_game_name.statistic.playerAchievement;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.achievements.Achievement;

@Getter
@Setter
@Entity
@Table( 
    name= "player_achievements",
    uniqueConstraints = @UniqueConstraint(columnNames = {"player_id", "achievement_id"})
)
public class PlayerAchievement extends BaseEntity {
    
    @NotNull
    @Min(0)
    @Column(name = "progress")
    private Integer progress = 0;

    @NotNull
    @Column(name = "completed")
    private Boolean completed = false;

    @Column(name = "unlocked_at")
    private LocalDateTime unlockedAt;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    @JsonIgnoreProperties({"playerAchievements", "playerStatistics", "password", "authority"})
    private Player player;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY) 
    @JoinColumn(name = "achievement_id")
    @JsonIgnoreProperties("playerAchievements")
    private Achievement achievement;

    @JsonProperty("achievementId")
    public Integer getAchievementIdJson() {
        return achievement != null ? achievement.getId() : null;
    }
}
