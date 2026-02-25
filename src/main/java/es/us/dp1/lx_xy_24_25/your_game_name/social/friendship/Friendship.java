package es.us.dp1.lx_xy_24_25.your_game_name.social.friendship;

import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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

@Entity
@Setter
@Getter
@Table(name = "friendships")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Friendship extends BaseEntity{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Player sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private Player receiver;

    @NotNull
    @Enumerated(EnumType.STRING)
    private FriendshipState state;

    @Column(name = "request_date", nullable = false)
    private LocalDateTime requestDate;

    @Column(name = "start_date")
    private LocalDateTime startDate;

}
