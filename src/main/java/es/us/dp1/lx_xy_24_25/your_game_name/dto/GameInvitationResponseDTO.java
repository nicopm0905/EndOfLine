package es.us.dp1.lx_xy_24_25.your_game_name.dto;

import java.time.LocalDateTime;

import es.us.dp1.lx_xy_24_25.your_game_name.social.gameInvitation.InvitationStatus;
import es.us.dp1.lx_xy_24_25.your_game_name.social.gameInvitation.InvitationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameInvitationResponseDTO {
    private Integer id;
    private String senderUsername;
    private String receiverUsername;
    private Integer gameSessionId;
    private String gameSessionName;
    private InvitationStatus status;
    private InvitationType type;
    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;
}
