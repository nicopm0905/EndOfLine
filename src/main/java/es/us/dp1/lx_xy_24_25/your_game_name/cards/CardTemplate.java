package es.us.dp1.lx_xy_24_25.your_game_name.cards;

import java.util.Set;

import org.hibernate.validator.constraints.Range;

import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.PowerName;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "card_template")
public class CardTemplate extends BaseEntity {

    @NotNull
    @Enumerated(EnumType.STRING)
    private CardType type;

    @Range(min = 0, max = 5)
    private int initiative; 

    @NotNull
    @Enumerated(EnumType.STRING)
    private Orientation defaultEntrance;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "card_template_exits", joinColumns = @JoinColumn(name = "card_template_id"))
    @Column(name = "exit_orientation")
    @Enumerated(EnumType.STRING)
    private Set<Orientation> defaultExits;

    private Integer imageId;

    @Enumerated(EnumType.STRING)
    private PowerName power; 

    @Enumerated(EnumType.STRING)
    private Color color;
}
