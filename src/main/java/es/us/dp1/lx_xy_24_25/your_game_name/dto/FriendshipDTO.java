package es.us.dp1.lx_xy_24_25.your_game_name.dto;

import es.us.dp1.lx_xy_24_25.your_game_name.social.friendship.Friendship;
import es.us.dp1.lx_xy_24_25.your_game_name.social.friendship.FriendshipState;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FriendshipDTO {

    private Integer id;
    private PlayerDTO sender;
    private PlayerDTO receiver;
    private FriendshipState state;
    
    public FriendshipDTO(Friendship friendship){
        this.id = friendship.getId();
        if (friendship.getSender() != null) {
            this.sender = new PlayerDTO(friendship.getSender()); 
        }
        if (friendship.getReceiver() != null) {
            this.receiver = new PlayerDTO(friendship.getReceiver());
        }
        this.state = friendship.getState();
    }
}
