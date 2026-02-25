package es.us.dp1.lx_xy_24_25.your_game_name.statistic.playerStatistics;

import com.fasterxml.jackson.annotation.JsonIgnore;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.Color;
import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.PowerName;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "player_statistics")
public class PlayerStatistics extends BaseEntity {
    
    @NotNull
    @Min(0)
    @Column(name = "games_played")
    private Integer gamesPlayed = 0;

    @NotNull
    @Min(0)
    @Column(name = "victories")
    private Integer victories = 0;

    @NotNull
    @Min(0)
    @Column(name = "defeats")
    private Integer defeats = 0;

    @NotNull
    @Min(0)
    @Column(name = "total_play_time")
    private Long totalPlayTime = 0L;

    @NotNull
    @Min(0)
    @Column(name = "shortest_game")
    private Long shortestGame = Long.MAX_VALUE;

    @NotNull
    @Min(0)
    @Column(name = "longest_game")
    private Long longestGame = 0L;

    @NotNull
    @Min(0)
    @Column(name = "total_cards_used")
    private Integer totalCardsUsed = 0;

    @NotNull
    @Min(0)
    @Column(name = "power_cards_used")
    private Integer powerCardsUsed = 0;

    @Column(name = "power_most_used")
    @Enumerated(EnumType.STRING)
    private PowerName powerMostUsed;

    @NotNull
    @Min(0)
    @Column(name = "favorite_color_uses")
    private Integer favoriteColorUses = 0;

    @Column(name = "favorite_color")
    @Enumerated(EnumType.STRING)
    private Color favoriteColor;

    @NotNull
    @Min(0)
    @Column(name = "max_line_length")
    private Integer maxLineLength = 0;

    @NotNull
    @Min(0)
    @Column(name = "total_lines_completed")
    private Integer totalLinesCompleted = 0;

    @NotNull
    @Min(0)
    @Column(name = "average_line_length")
    private Double averageLineLength = 0.0;

    @NotNull
    @Min(0)
    @Column(name = "highest_score_puzzle")
    private Integer highestScorePuzzle = 0;

    @NotNull
    @Min(0)
    @Column(name = "completed_puzzles")
    private Integer completedPuzzles = 0;

    @NotNull
    @Min(0)
    @Column(name = "total_puzzle_score")
    private Integer totalPuzzleScore = 0;

    @NotNull
    @Min(0)
    @Column(name = "total_score")
    private Integer totalScore = 0;

    @NotNull
    @Min(0)
    @Column(name = "highest_score")
    private Integer highestScore = 0;

    @NotNull
    @Min(0)
    @Column(name = "lowest_score")
    private Integer lowestScore = Integer.MAX_VALUE;

    @NotNull
    @Min(0)
    @Column(name = "messages_sent")
    private Integer messagesSent = 0;

    @NotNull
    @Min(0)
    @Column(name = "friends_count")
    private Integer friendsCount = 0;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", unique = true)
    private Player player;

    public Double getWinRate() {
        if (gamesPlayed == 0) return 0.0;
        return (victories * 100.0) / gamesPlayed;
    }
    
    public Double getAverageDuration() {
        if (gamesPlayed == 0) return 0.0;
        return totalPlayTime.doubleValue() / gamesPlayed;
    }

    
    public Double getAverageScore() {
        if (gamesPlayed == 0) return 0.0;
        return totalScore.doubleValue() / gamesPlayed;
    }

    public Double getAverageCardsPerGame() {
        if (gamesPlayed == 0) return 0.0;
        return totalCardsUsed.doubleValue() / gamesPlayed;
    }
}
