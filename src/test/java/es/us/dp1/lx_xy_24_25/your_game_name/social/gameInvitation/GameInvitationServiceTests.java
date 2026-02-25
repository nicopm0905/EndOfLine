package es.us.dp1.lx_xy_24_25.your_game_name.social.gameInvitation;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.BadRequestException;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSessionService;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSessionRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameState;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameMode;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerService;
import es.us.dp1.lx_xy_24_25.your_game_name.social.friendship.Friendship;
import es.us.dp1.lx_xy_24_25.your_game_name.social.friendship.FriendshipRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.social.friendship.FriendshipState;
import es.us.dp1.lx_xy_24_25.your_game_name.user.AuthoritiesService;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class GameInvitationServiceTests {

    @Autowired
    private GameInvitationService gameInvitationService;

    @Autowired
    private GameInvitationRepository gameInvitationRepository;

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private AuthoritiesService authoritiesService;

    @Autowired
    private GameSessionService gameSessionService;

    @Autowired
    private GameSessionRepository gameSessionRepository;

    private Player sender;
    private Player receiver;
    private GameSession game;

    @BeforeEach
    void setUp() {
        sender = new Player();
        sender.setUsername("sender" + System.nanoTime());
        sender.setPassword("password");
        sender.setFirstName("Sender");
        sender.setLastName("User");
        sender.setEmail(sender.getUsername() + "@example.com");
        sender.setAuthority(authoritiesService.findByAuthority("PLAYER"));
        playerService.savePlayer(sender);

        receiver = new Player();
        receiver.setUsername("receiver" + System.nanoTime());
        receiver.setPassword("password");
        receiver.setFirstName("Receiver");
        receiver.setLastName("User");
        receiver.setEmail(receiver.getUsername() + "@example.com");
        receiver.setAuthority(authoritiesService.findByAuthority("PLAYER"));
        playerService.savePlayer(receiver);

        game = new GameSession();
        game.setName("Test Game");
        game.setHost(sender.getUsername());
        game.setGameMode(GameMode.SOLITARY_PUZZLE);
        game.setNumPlayers(4);
        game.setBoardSize(5);
        game.setPrivate(false);
        game.setState(GameState.PENDING);
        game = gameSessionService.save(game);
    }

    private void makeFriends(Player p1, Player p2) {
        Friendship f = new Friendship();
        f.setSender(p1);
        f.setReceiver(p2);
        f.setState(FriendshipState.ACCEPTED);
        f.setRequestDate(LocalDateTime.now());
        friendshipRepository.save(f);
    }

    @Test
    void shouldCreateInvitationSuccessfully() {
        makeFriends(sender, receiver);
        
        GameInvitation result = gameInvitationService.createInvitation(sender, receiver, game, InvitationType.PLAYER);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(InvitationStatus.PENDING, result.getStatus());
        assertEquals(sender.getId(), result.getSender().getId());
        assertEquals(receiver.getId(), result.getReceiver().getId());
    }

    @Test
    void shouldFailCreateInvitationIfNotFriends() {
        assertThrows(BadRequestException.class, () -> 
            gameInvitationService.createInvitation(sender, receiver, game, InvitationType.PLAYER)
        );
    }

    @Test
    void shouldFailCreateInvitationIfPendingExists() {
        makeFriends(sender, receiver);
        gameInvitationService.createInvitation(sender, receiver, game, InvitationType.PLAYER);

        assertThrows(BadRequestException.class, () -> 
            gameInvitationService.createInvitation(sender, receiver, game, InvitationType.PLAYER)
        );
    }

    @Test
    void shouldFailCreateInvitationIfSelfInvite() {
        assertThrows(BadRequestException.class, () -> 
            gameInvitationService.createInvitation(sender, sender, game, InvitationType.PLAYER)
        );
    }

    @Test
    void shouldAcceptInvitationSuccessfully() {
        makeFriends(sender, receiver);
        GameInvitation invitation = gameInvitationService.createInvitation(sender, receiver, game, InvitationType.PLAYER);
        
        GameInvitation result = gameInvitationService.acceptInvitation(invitation.getId());

        assertEquals(InvitationStatus.ACCEPTED, result.getStatus());
        assertNotNull(result.getRespondedAt());
    }

    @Test
    void shouldFailAcceptIfGameFull() {
        makeFriends(sender, receiver);
        GameInvitation invitation = gameInvitationService.createInvitation(sender, receiver, game, InvitationType.PLAYER);

        game.setNumPlayers(0);
        gameSessionService.save(game);
        
        assertThrows(BadRequestException.class, () -> gameInvitationService.acceptInvitation(invitation.getId()));
    }

    @Test
    void shouldFailAcceptIfGameStarted() {
        makeFriends(sender, receiver);
        GameInvitation invitation = gameInvitationService.createInvitation(sender, receiver, game, InvitationType.PLAYER);

        game.setStartTime(LocalDateTime.now());
        gameSessionRepository.save(game);

        assertThrows(BadRequestException.class, () -> gameInvitationService.acceptInvitation(invitation.getId()));
        
        GameInvitation updated = gameInvitationRepository.findById(invitation.getId()).orElseThrow();
        assertEquals(InvitationStatus.EXPIRED, updated.getStatus());
    }

    @Test
    void shouldRejectInvitation() {
        makeFriends(sender, receiver);
        GameInvitation invitation = gameInvitationService.createInvitation(sender, receiver, game, InvitationType.PLAYER);

        GameInvitation result = gameInvitationService.rejectInvitation(invitation.getId());

        assertEquals(InvitationStatus.REJECTED, result.getStatus());
        assertNotNull(result.getRespondedAt());
    }

    @Test
    void shouldCancelInvitation() {
        makeFriends(sender, receiver);
        GameInvitation invitation = gameInvitationService.createInvitation(sender, receiver, game, InvitationType.PLAYER);

        GameInvitation result = gameInvitationService.cancelInvitation(invitation.getId());

        assertEquals(InvitationStatus.CANCELED, result.getStatus());
        assertNotNull(result.getRespondedAt());
    }
}
