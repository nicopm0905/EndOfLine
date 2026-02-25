package es.us.dp1.lx_xy_24_25.your_game_name.dto;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.Orientation;

public class StartPositionDTO {
    public final int row;
    public final int col;
    public final Orientation orientation;

    public StartPositionDTO(int row, int col, String orientationStr) {
        this.row = row;
        this.col = col;
        this.orientation = Orientation.valueOf(orientationStr);
    }
}