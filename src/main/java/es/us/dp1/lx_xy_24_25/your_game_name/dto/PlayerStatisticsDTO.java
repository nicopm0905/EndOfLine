package es.us.dp1.lx_xy_24_25.your_game_name.dto;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.Color;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.PowerName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerStatisticsDTO {
    private Integer playerId;
    private String username;
    private Integer gamesPlayed;
    private Integer victories;
    private Integer defeats;
    private Double winRate;
    private Long totalPlayTime;
    private Double averageDuration;
    private Long shortestGame;
    private Long longestGame;
    private Integer totalCardsUsed;
    private Integer powerCardsUsed;
    private PowerName powerMostUsed;
    private Color favoriteColor;
    private Double averageCardsPerGame;
    private Integer maxLineLength;
    private Integer totalLinesCompleted;
    private Double averageLineLength;
    private Integer totalScore;
    private Integer highestScore;
    private Integer lowestScore;
    private Double averageScore;
    private Integer completedPuzzles;
    private Integer highestScorePuzzle;
    private Integer messagesSent;
    private Integer friendsCount;
}