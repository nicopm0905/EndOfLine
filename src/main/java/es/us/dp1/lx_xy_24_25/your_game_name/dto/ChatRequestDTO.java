package es.us.dp1.lx_xy_24_25.your_game_name.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRequestDTO {
    @NotBlank
    @Size(max = 500)
    private String message;
}
