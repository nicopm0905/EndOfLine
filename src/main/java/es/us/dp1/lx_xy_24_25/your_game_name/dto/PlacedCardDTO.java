package es.us.dp1.lx_xy_24_25.your_game_name.dto;

import java.time.LocalDateTime;

import org.hibernate.Hibernate;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardTemplate;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardType;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Color;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Orientation;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.placedCard.PlacedCard;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.PowerName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlacedCardDTO {
    private Integer id;
    private Integer gameSessionId;
    private Integer row;
    private Integer col;
    private Orientation orientation;
    private LocalDateTime placedAt;
    
    private String placedByUsername;
    private Integer placedByPlayerId;
    private Color placedByPlayerColor;
    
    private Integer cardTemplateId;
    private Integer imageId;
    private CardType cardType;
    private Color cardColor;
    private PowerName cardPower;
    private Integer initiative;
    private Integer placedByPlayerGameSessionId;

    
    public PlacedCardDTO(PlacedCard card) {
        if (card != null) {
            this.id = card.getId();
            this.row = card.getRow();
            this.col = card.getCol();
            this.orientation = card.getOrientation();
            this.placedAt = card.getPlacedAt();
            this.placedByPlayerGameSessionId = card.getPlacedBy().getId();
            
            if (card.getGameSession() != null) {
                Hibernate.initialize(card.getGameSession()); 
                this.gameSessionId = card.getGameSession().getId();
            }
                
            CardTemplate template = card.getTemplate();
            if (template != null) {
                Hibernate.initialize(template); 
                this.cardTemplateId = template.getId();
                this.imageId = template.getImageId();
                this.cardType = template.getType();
                this.cardColor = template.getColor();
                this.cardPower = template.getPower();
                this.initiative = template.getInitiative();
            }

            if (card.getPlacedBy() != null) {
                Hibernate.initialize(card.getPlacedBy());
                this.placedByPlayerColor = card.getPlacedBy().getPlayerColor();
                if (card.getPlacedBy().getPlayer() != null) {
                    Hibernate.initialize(card.getPlacedBy().getPlayer());
                    this.placedByUsername = card.getPlacedBy().getPlayer().getUsername();
                    this.placedByPlayerId = card.getPlacedBy().getPlayer().getId();
                }
            }
        }
    }
}