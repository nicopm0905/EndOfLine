package es.us.dp1.lx_xy_24_25.your_game_name.statistic.achievements;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import es.us.dp1.lx_xy_24_25.your_game_name.model.NamedEntity;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.Metric;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.playerAchievement.PlayerAchievement;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Achievement extends NamedEntity{

    @NotBlank
    @Column(name = "description")
    private String description;

    @Column(name = "badge_image")
    private String badgeImage;

    @NotNull
    @Min(0)
    @Column(name = "threshold")
    private double threshold;
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "metric")
    Metric metric;

    @Min(0)
    @Column(name = "points")
    private Integer points;

    @OneToMany(mappedBy = "achievement", cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnore
    private Set<PlayerAchievement> playerAchievements = new HashSet<>();
    
}
