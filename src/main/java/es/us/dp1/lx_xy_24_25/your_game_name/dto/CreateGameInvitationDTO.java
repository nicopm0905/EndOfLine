package es.us.dp1.lx_xy_24_25.your_game_name.dto;

import es.us.dp1.lx_xy_24_25.your_game_name.social.gameInvitation.InvitationType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateGameInvitationDTO {
    @NotNull
    private Integer receiverId;

    @NotNull
    private Integer gameSessionId;

    @NotNull
    private InvitationType type;
}
