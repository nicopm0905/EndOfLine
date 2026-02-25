package es.us.dp1.lx_xy_24_25.your_game_name.dto;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.Color;
import es.us.dp1.lx_xy_24_25.your_game_name.social.chatMessage.ChatMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageDTO {

    private Color color;
    private String message;
    private String username;

    public ChatMessageDTO(ChatMessage chatMessage) {
        this.color = chatMessage.getColor();
        this.message = chatMessage.getMessage();
        if (chatMessage.getPlayerGameSession() != null) { 
            this.username = chatMessage.getPlayerGameSession().getPlayer().getUsername();
        }
    }
}
