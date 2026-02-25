package es.us.dp1.lx_xy_24_25.your_game_name.dto;

import es.us.dp1.lx_xy_24_25.your_game_name.social.friendship.FriendshipState;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FriendshipRequestDTO {
    private Integer sender;
    private Integer receiver;
    private FriendshipState state;
}
