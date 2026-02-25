package es.us.dp1.lx_xy_24_25.your_game_name.puzzle;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PuzzleDefinition {
    private Integer id;
    private String name;
    private List<PuzzleCardConfiguration> obstacles;
}
