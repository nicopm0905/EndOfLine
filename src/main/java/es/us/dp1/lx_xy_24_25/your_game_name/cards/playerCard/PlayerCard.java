package es.us.dp1.lx_xy_24_25.your_game_name.cards.playerCard;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardState;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardTemplate;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Color;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Orientation;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.PlayerGameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "player_cards")
@Getter
@Setter
public class PlayerCard extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private CardState location;

    private Integer deckOrder;

    @Enumerated(EnumType.STRING)
    private Orientation orientation;

    @Enumerated(EnumType.STRING)
    private Color color;

    private Boolean used = false;

    @NotNull
    @ManyToOne
    private CardTemplate template;

    @NotNull
    @ManyToOne
    private PlayerGameSession player;
}
