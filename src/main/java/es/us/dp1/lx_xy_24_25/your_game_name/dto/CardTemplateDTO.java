package es.us.dp1.lx_xy_24_25.your_game_name.dto;

import java.util.Set;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardTemplate;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardType;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Color;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Orientation;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.PowerName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CardTemplateDTO {
    private Integer id;
    private CardType type;
    private int initiative;
    private Orientation defaultEntrance;
    private Set<Orientation> defaultExits;
    private Integer imageId;
    private PowerName power;
    private Color color;

    public CardTemplateDTO(CardTemplate template) {
        if (template != null) {
            this.id = template.getId();
            this.type = template.getType();
            this.initiative = template.getInitiative();
            this.defaultEntrance = template.getDefaultEntrance();
            this.defaultExits = template.getDefaultExits();
            this.imageId = template.getImageId();
            this.power = template.getPower();
            this.color = template.getColor();
        }
    }
}
