package es.us.dp1.lx_xy_24_25.your_game_name.player;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.PlayerGameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.social.friendship.Friendship;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.playerAchievement.PlayerAchievement;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.playerStatistics.PlayerStatistics;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Player extends User {
    
    @JsonIgnore
	@OneToOne(mappedBy = "player", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private PlayerStatistics playerStatistics;
     
	@OneToMany(mappedBy = "player", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<PlayerAchievement> playerAchievements = new HashSet<>();

    @OneToMany(mappedBy = "player", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<PlayerGameSession> gameHistory = new HashSet<>();

    @OneToMany(mappedBy = "sender",fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Friendship> sentFriendShips = new HashSet<>();

    @OneToMany(mappedBy = "receiver",fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Friendship> receivedFriendships = new HashSet<>();
}
