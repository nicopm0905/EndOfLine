package es.us.dp1.lx_xy_24_25.your_game_name.social.gameInvitation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSessionService;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameState;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameMode;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.AuthoritiesService;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;

@Epic("Social Module")
@Feature("Game Invitation Repository Tests")
@Owner("DP1-tutors")
@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class GameInvitationRepositoryTests {

    @Autowired
    private GameInvitationRepository gameInvitationRepository;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private AuthoritiesService authoritiesService;

    @Autowired
    private GameSessionService gameSessionService;

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
        game.setNumPlayers(2);
        game.setBoardSize(5);
        game.setPrivate(false);
        game.setState(GameState.PENDING);
        game = gameSessionService.save(game);
    }

    @Test
    void shouldFindInvitationBySenderAndReceiverAndGame() {
        GameInvitation invitation = new GameInvitation(sender, receiver, game, InvitationType.PLAYER);
        gameInvitationRepository.save(invitation);

        Optional<GameInvitation> found = gameInvitationRepository.findBySenderAndReceiverAndGame(sender, receiver, game);
        assertTrue(found.isPresent());
        assertEquals(invitation.getId(), found.get().getId());
    }

    @Test
    void shouldFindByReceiver() {
        GameInvitation invitation1 = new GameInvitation(sender, receiver, game, InvitationType.PLAYER);
        gameInvitationRepository.save(invitation1);

        GameInvitation invitation2 = new GameInvitation(sender, receiver, game, InvitationType.SPECTATOR);
        gameInvitationRepository.save(invitation2);

        List<GameInvitation> invitations = gameInvitationRepository.findByReceiver(receiver);
        assertEquals(2, invitations.size());
    }

    @Test
    void shouldFindByReceiverAndStatus() {
        GameInvitation invitation1 = new GameInvitation(sender, receiver, game, InvitationType.PLAYER);
        gameInvitationRepository.save(invitation1);

        GameInvitation invitation2 = new GameInvitation(sender, receiver, game, InvitationType.SPECTATOR);
        invitation2.setStatus(InvitationStatus.ACCEPTED);
        gameInvitationRepository.save(invitation2);

        List<GameInvitation> pendingInvitations = gameInvitationRepository.findByReceiverAndStatus(receiver, InvitationStatus.PENDING);
        assertEquals(1, pendingInvitations.size());
        assertEquals(invitation1.getId(), pendingInvitations.get(0).getId());
    }
    
    @Test
    void shouldFindBySender() {
        GameInvitation invitation = new GameInvitation(sender, receiver, game, InvitationType.PLAYER);
        gameInvitationRepository.save(invitation);

        List<GameInvitation> sentInvitations = gameInvitationRepository.findBySender(sender);
        assertEquals(1, sentInvitations.size());
        assertEquals(invitation.getId(), sentInvitations.get(0).getId());
    }

    @Test
    void shouldCountByReceiverAndStatus() {
        GameInvitation invitation1 = new GameInvitation(sender, receiver, game, InvitationType.PLAYER);
        gameInvitationRepository.save(invitation1);

        GameInvitation invitation2 = new GameInvitation(sender, receiver, game, InvitationType.SPECTATOR);
        invitation2.setStatus(InvitationStatus.REJECTED);
        gameInvitationRepository.save(invitation2);

        long count = gameInvitationRepository.countByReceiverAndStatus(receiver, InvitationStatus.PENDING);
        assertEquals(1, count);
    }

    @Test
    void shouldFindByGameAndStatus() {
        GameInvitation invitation1 = new GameInvitation(sender, receiver, game, InvitationType.PLAYER);
        gameInvitationRepository.save(invitation1);

        GameInvitation invitation2 = new GameInvitation(sender, receiver, game, InvitationType.SPECTATOR);
        invitation2.setStatus(InvitationStatus.ACCEPTED);
        gameInvitationRepository.save(invitation2);

        List<GameInvitation> pendingGameInvitations = gameInvitationRepository.findByGameAndStatus(game, InvitationStatus.PENDING);
        assertEquals(1, pendingGameInvitations.size());
        assertEquals(invitation1.getId(), pendingGameInvitations.get(0).getId());
    }
}
