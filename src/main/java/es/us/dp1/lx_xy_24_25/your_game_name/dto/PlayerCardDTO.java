package es.us.dp1.lx_xy_24_25.your_game_name.dto;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.*;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.playerCard.PlayerCard;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class PlayerCardDTO {

    private Integer id;
    private CardState location;
    private Orientation orientation;
    private Color color;
    private Integer deckOrder;
    private CardType type;
    private int initiative;
    private Orientation defaultEntrance;
    private Set<Orientation> defaultExits;
    private Integer imageId;

    public PlayerCardDTO(PlayerCard pc) {
        if (pc != null) {
            this.id = pc.getId();
            this.location = pc.getLocation();
            this.orientation = pc.getOrientation();
            this.color = pc.getColor();
            this.deckOrder = pc.getDeckOrder();

            CardTemplate ct = pc.getTemplate();
            if (ct != null) {
                this.type = ct.getType();
                this.initiative = ct.getInitiative();
                this.defaultEntrance = ct.getDefaultEntrance();
                this.defaultExits = ct.getDefaultExits();
                this.imageId = ct.getImageId();
            }
        }
    }
}
