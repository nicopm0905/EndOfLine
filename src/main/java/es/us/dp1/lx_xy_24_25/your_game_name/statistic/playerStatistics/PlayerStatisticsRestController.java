package es.us.dp1.lx_xy_24_25.your_game_name.statistic.playerStatistics;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import es.us.dp1.lx_xy_24_25.your_game_name.auth.payload.response.MessageResponse;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerService;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.Metric;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.PlayerRankingDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/statistics")
@Tag(name = "Player Statistics", description = "Player Statistics Management API including rankings")
@SecurityRequirement(name = "bearerAuth")
public class PlayerStatisticsRestController {
    
    private final PlayerStatisticsService playerStatisticsService;
    private final PlayerService playerService;

    @Autowired
    public PlayerStatisticsRestController(PlayerStatisticsService playerStatisticsService, PlayerService playerService){
        this.playerStatisticsService = playerStatisticsService;
        this.playerService = playerService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get statistics by ID", description = "Retrieves a specific statistics record by its ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics found"),
        @ApiResponse(responseCode = "404", description = "Statistics not found")
    })
    public ResponseEntity<PlayerStatistics> getStatisticsById(@PathVariable("id") @Parameter(description = "ID of the statistics record") int id){
        PlayerStatistics stats = playerStatisticsService.findById(id);
        if(stats==null)
            throw new ResourceNotFoundException("PlayerStatistics with id "+id+" not found!");  
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    @GetMapping("/player/{playerId}")
    @Operation(summary = "Get statistics by player", description = "Retrieves statistics for a specific player.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics found"),
        @ApiResponse(responseCode = "404", description = "Statistics not found")
    })
    public ResponseEntity<PlayerStatistics> getStatisticsByPlayerId(@PathVariable("playerId") @Parameter(description = "ID of the player") Integer playerId) {
        PlayerStatistics stats = playerStatisticsService.findByPlayerId(playerId);
        if(stats==null)
            throw new ResourceNotFoundException("PlayerStatistics with playerId "+playerId+" not found!");  
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Create statistics", description = "Creates a new statistics record.")
    @ApiResponse(responseCode = "201", description = "Statistics created successfully")
    public ResponseEntity<PlayerStatistics> createPlayerStatistics(@org.springframework.web.bind.annotation.RequestBody @Valid PlayerStatistics newStatistics){ 
        PlayerStatistics savedStats = playerStatisticsService.save(newStatistics);
        return new ResponseEntity<>(savedStats, HttpStatus.CREATED);
    }

    @PostMapping("/player/{playerId}")
    @Operation(summary = "Create statistics for player", description = "Creates a new statistics record for a specific player.")
    @ApiResponse(responseCode = "201", description = "Statistics created successfully")
    public ResponseEntity<PlayerStatistics> createStatisticsForPlayer(@PathVariable("playerId") @Parameter(description = "ID of the player") Integer playerId) {
        Player player = playerService.findPlayer(playerId);
        PlayerStatistics stats = playerStatisticsService.createForPlayer(player);
        return new ResponseEntity<PlayerStatistics>(stats, HttpStatus.CREATED);
    }

    @PutMapping("/player/{playerId}")
    @Operation(summary = "Update statistics", description = "Updates statistics for a specific player.")
    @ApiResponse(responseCode = "200", description = "Statistics updated successfully")
    public ResponseEntity<PlayerStatistics> updateStatistics(@PathVariable("playerId") @Parameter(description = "ID of the player") Integer playerId, @Valid @RequestBody PlayerStatistics playerStatistics) {
        PlayerStatistics updatedStats = playerStatisticsService.updateStatistics(playerId, playerStatistics);
        return new ResponseEntity<PlayerStatistics>(updatedStats, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete statistics", description = "Deletes a statistics record.")
    @ApiResponse(responseCode = "200", description = "Statistics deleted successfully")
    public ResponseEntity<MessageResponse> deleteStatistics(@PathVariable("id") @Parameter(description = "ID of the statistics record to delete") Integer id) {
        playerStatisticsService.deleteById(id);
        return ResponseEntity.ok(new MessageResponse("Statistics deleted successfully"));
    }

    @GetMapping("/ranking")
    @Operation(summary = "Get ranking by metric", description = "Retrieves a ranking list based on a specific metric.")
    @ApiResponse(responseCode = "200", description = "Ranking list")
    public ResponseEntity<List<PlayerRankingDTO>> getRankingByMetric(
            @RequestParam @Parameter(description = "Metric to rank by (e.g., TOTAL_SCORE, HIGHEST_SCORE)") Metric metric,
            @RequestParam(defaultValue = "10") @Parameter(description = "Limit of results to return") int limit) {
        List<PlayerRankingDTO> ranking = playerStatisticsService.getRankingByMetric(metric, limit);
        return ResponseEntity.ok(ranking);
    }

    @GetMapping("/ranking/total-score")
    @Operation(summary = "Get ranking by total score", description = "Retrieves ranking by total score.")
    public ResponseEntity<List<PlayerRankingDTO>> getRankingByTotalScore(
            @RequestParam(defaultValue = "10") @Parameter(description = "Limit of results to return") int limit) {
        List<PlayerRankingDTO> ranking = playerStatisticsService.getRankingByMetric(Metric.TOTAL_SCORE, limit);
        return ResponseEntity.ok(ranking);
    }

    @GetMapping("/ranking/highest-score")
    @Operation(summary = "Get ranking by highest score", description = "Retrieves ranking by single highest score.")
    public ResponseEntity<List<PlayerRankingDTO>> getRankingByHighestScore(
            @RequestParam(defaultValue = "10") @Parameter(description = "Limit of results to return") int limit) {
        List<PlayerRankingDTO> ranking = playerStatisticsService.getRankingByMetric(Metric.HIGHEST_SCORE, limit);
        return ResponseEntity.ok(ranking);
    }

    @GetMapping("/ranking/average-score")
    @Operation(summary = "Get ranking by average score", description = "Retrieves ranking by average score.")
    public ResponseEntity<List<PlayerRankingDTO>> getRankingByAverageScore(
            @RequestParam(defaultValue = "10") @Parameter(description = "Limit of results to return") int limit) {
        List<PlayerRankingDTO> ranking = playerStatisticsService.getRankingByMetric(Metric.AVERAGE_SCORE, limit);
        return ResponseEntity.ok(ranking);
    }

    @GetMapping("/ranking/lines-completed")
    @Operation(summary = "Get ranking by lines completed", description = "Retrieves ranking by total lines completed.")
    public ResponseEntity<List<PlayerRankingDTO>> getRankingByLinesCompleted(
            @RequestParam(defaultValue = "10") @Parameter(description = "Limit of results to return") int limit) {
        List<PlayerRankingDTO> ranking = playerStatisticsService.getRankingByMetric(Metric.TOTAL_LINES_COMPLETED, limit);
        return ResponseEntity.ok(ranking);
    }

    @GetMapping("/ranking/power-cards")
    @Operation(summary = "Get ranking by power cards", description = "Retrieves ranking by power cards used.")
    public ResponseEntity<List<PlayerRankingDTO>> getRankingByPowerCards(
            @RequestParam(defaultValue = "10") @Parameter(description = "Limit of results to return") int limit) {
        List<PlayerRankingDTO> ranking = playerStatisticsService.getRankingByMetric(Metric.POWER_CARDS_USED, limit);
        return ResponseEntity.ok(ranking);
    }

    @GetMapping("/ranking/social")
    @Operation(summary = "Get ranking by messages sent", description = "Retrieves ranking by number of messages sent.")
    public ResponseEntity<List<PlayerRankingDTO>> getRankingByMessages(
            @RequestParam(defaultValue = "10") @Parameter(description = "Limit of results to return") int limit) {
        List<PlayerRankingDTO> ranking = playerStatisticsService.getRankingByMetric(Metric.MESSAGES_SENT, limit);
        return ResponseEntity.ok(ranking);
    }

    @GetMapping("/ranking/friends")
    @Operation(summary = "Get ranking by friend count", description = "Retrieves ranking by number of friends.")
    public ResponseEntity<List<PlayerRankingDTO>> getRankingByFriends(
            @RequestParam(defaultValue = "10") @Parameter(description = "Limit of results to return") int limit) {
        List<PlayerRankingDTO> ranking = playerStatisticsService.getRankingByMetric(Metric.FRIENDS_COUNT, limit);
        return ResponseEntity.ok(ranking);
    }
}
