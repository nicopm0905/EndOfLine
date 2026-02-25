package es.us.dp1.lx_xy_24_25.your_game_name.social.gameInvitation;

import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class GameInvitation extends BaseEntity {
    @ManyToOne
    @NotNull
    @JoinColumn(name = "sender_id")
    private Player sender;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "receiver_id")
    private Player receiver;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "game_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private GameSession game;

    @Enumerated(EnumType.STRING)
    private InvitationType invitationType;

    @Enumerated(EnumType.STRING)
    @NotNull
    private InvitationStatus status;

    @NotNull
    private LocalDateTime createdAt;

    private LocalDateTime respondedAt;

    public GameInvitation(Player sender, Player receiver, GameSession game, InvitationType invitationType) {
        this.sender = sender;
        this.receiver = receiver;
        this.game = game;
        this.invitationType = invitationType;
        this.status = InvitationStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
}
