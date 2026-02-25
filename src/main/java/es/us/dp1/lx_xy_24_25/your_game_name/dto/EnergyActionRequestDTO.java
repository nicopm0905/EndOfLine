package es.us.dp1.lx_xy_24_25.your_game_name.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnergyActionRequestDTO {
    @NotBlank
    private String actionId;
}
