package es.us.dp1.lx_xy_24_25.your_game_name.dto;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.Orientation;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceCardRequestDTO {
    @NotNull
    private Integer playerCardId; 

    @NotNull
    private Integer row;

    @NotNull
    private Integer col;

    @NotNull
    private Orientation orientation;
}