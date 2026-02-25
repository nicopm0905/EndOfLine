package es.us.dp1.lx_xy_24_25.your_game_name.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardState;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Color;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.PlayerGameSession;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlayerGameSessionDTO {
    private Integer id;
    private Integer energy;
    private Color playerColor;
    private Integer teamNumber;
    private PlayerDTO player;
    private Boolean hasRerolled;
    private Integer cardsPlacedThisRound;
    private String activeEnergyEffect;
    private Integer energyCardsToPlaceOverride;
    private Boolean energyAllowPenultimateStart;
    private Boolean energyPendingExtraDraw;
    private Integer lastEnergyRoundUsed;
    private List<CardTemplateDTO> discardPile;
    private Integer turnOrder;
    private Boolean spectator;
    private List<PlayerCardDTO> hand;

    public PlayerGameSessionDTO(PlayerGameSession pgs) {
        if (pgs != null) {
            this.id = pgs.getId();
            this.energy = pgs.getEnergy();
            this.playerColor = pgs.getPlayerColor();
            this.teamNumber = pgs.getTeamNumber();
            this.hasRerolled = pgs.getHasRerolled();
            this.cardsPlacedThisRound = pgs.getCardsPlacedThisRound();
            this.activeEnergyEffect = pgs.getActiveEnergyEffect() != null ? pgs.getActiveEnergyEffect().name() : null;
            this.energyCardsToPlaceOverride = pgs.getEnergyCardsToPlaceOverride();
            this.energyAllowPenultimateStart = pgs.getEnergyAllowPenultimateStart();
            this.energyPendingExtraDraw = pgs.getEnergyPendingExtraDraw();
            this.lastEnergyRoundUsed = pgs.getLastEnergyRoundUsed();
            this.turnOrder = pgs.getTurnOrder();
            if (pgs.getPlayer() != null) {
                this.player = new PlayerDTO(pgs.getPlayer());
            }
            this.discardPile = pgs.getDiscardPile() != null
                    ? pgs.getDiscardPile().stream()
                        .map(CardTemplateDTO::new)
                        .collect(Collectors.toList())
                    : new ArrayList<>();
            if (pgs.getCards() != null) {
                this.hand = pgs.getCards().stream()
                        .filter(c -> c.getLocation() == CardState.HAND && !c.getUsed())
                        .map(PlayerCardDTO::new)
                        .collect(Collectors.toList());
            } else {
                this.hand = new ArrayList<>();
            }
        }
    }
}
