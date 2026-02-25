package es.us.dp1.lx_xy_24_25.your_game_name.cards.placedCard;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardTemplate;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Orientation;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.PlayerGameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@Table(name = "placed_cards")
public class PlacedCard extends BaseEntity { 

    @NotNull
    @Column(name = "row_index")
    private Integer row;

    @NotNull
    @Column(name = "col_index")
    private Integer col;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Orientation orientation;

    @Column(name = "placed_at")
    private LocalDateTime placedAt;
    
    private Boolean isLastPlacedByPlayer = false;
    
    private Boolean isPenultimatePlacedByPlayer = false;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "game_session_id")
    private GameSession gameSession;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "card_template_id")
    private CardTemplate template;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "player_game_session_id")
    private PlayerGameSession placedBy;

}