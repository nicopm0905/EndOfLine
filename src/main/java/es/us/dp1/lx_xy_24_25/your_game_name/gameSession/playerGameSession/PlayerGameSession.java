package es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession;

import org.hibernate.validator.constraints.Range;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardTemplate;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Color;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.EnergyEffectType;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.playerCard.PlayerCard;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.placedCard.PlacedCard;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.social.chatMessage.ChatMessage;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "player_game_sessions")
public class PlayerGameSession extends BaseEntity {

    @NotNull
    @Range(min = 0, max = 3)
    private Integer energy;

    @Enumerated(EnumType.STRING)
    private Color playerColor;

    @Column(name = "team_number")
    private Integer teamNumber;

    private Boolean hasRerolled = false;

    @Column(name = "cards_placed_this_round")
    private Integer cardsPlacedThisRound = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "active_energy_effect")
    private EnergyEffectType activeEnergyEffect;

    @Column(name = "energy_cards_override")
    private Integer energyCardsToPlaceOverride;

    @Column(name = "energy_allow_penultimate")
    private Boolean energyAllowPenultimateStart = false;

    @Column(name = "energy_pending_extra_draw")
    private Boolean energyPendingExtraDraw = false;

    @Column(name = "last_energy_round_used")
    private Integer lastEnergyRoundUsed;

    @Column(name = "turn_order")
    private Integer turnOrder;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private Player player;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "game_session_id")
    private GameSession gameSession;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PlayerCard> cards = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "player_discard_pile", joinColumns = @JoinColumn(name = "player_game_session_id"), inverseJoinColumns = @JoinColumn(name = "card_template_id"))
    private List<CardTemplate> discardPile = new ArrayList<>();

    @OneToMany(mappedBy = "placedBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlacedCard> placedCards = new ArrayList<>();

    @OneToMany(mappedBy = "playerGameSession", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> messages = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "initiative_card_id")
    private CardTemplate initiativeCard;

}
