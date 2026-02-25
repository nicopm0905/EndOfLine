package es.us.dp1.lx_xy_24_25.your_game_name.dto;

import es.us.dp1.lx_xy_24_25.your_game_name.social.friendship.UserPresenceStatus;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlayerDTO {
    private Integer id;
    private String username;
    private Integer avatarId;
    private UserPresenceStatus status;
    public PlayerDTO(User user) {
        if(user != null) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.avatarId = user.getAvatarId();
            this.status = UserPresenceStatus.OFFLINE;
        }
    }
}
