package es.us.dp1.lx_xy_24_25.your_game_name.gameSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertFalse;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.BadRequestException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import jakarta.persistence.EntityManager;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardState;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Color;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.playerCard.PlayerCardRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.PlayerGameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.PlayerGameSessionRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.AuthoritiesService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.PlaceCardRequestDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Orientation;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.placedCard.PlacedCard;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.placedCard.PlacedCardRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.GameSessionDTO;

@SpringBootTest
@AutoConfigureTestDatabase
class GameSessionServiceTests {

    @Autowired
    private GameSessionService gameSessionService;

    @Autowired
    private GameSessionRepository gameSessionRepository;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthoritiesService authoritiesService;

    @Autowired
    private PlayerGameSessionRepository playerGameSessionRepository;

    @Autowired
    private PlayerCardRepository playerCardRepository;

    @Autowired
    private PlacedCardRepository placedCardRepository;

    @Autowired
    private EntityManager entityManager;

    private User hostUser;
    private Player hostPlayer;

    @BeforeEach
    void setUp() {
        String username = "HostUser" + System.nanoTime();

        hostPlayer = new Player();
        hostPlayer.setUsername(username);
        hostPlayer.setPassword("password");
        hostPlayer.setFirstName("Host");
        hostPlayer.setLastName("Test");
        hostPlayer.setEmail(username + "@example.com");
        hostPlayer.setAuthority(authoritiesService.findByAuthority("PLAYER"));
        hostPlayer.setAvatarId(1);

        playerService.savePlayer(hostPlayer);
        hostUser = hostPlayer;
    }

    @Test
    @Transactional
    void testCreateGameSession() {
        GameSession session = new GameSession();
        session.setGameMode(GameMode.VERSUS);
        session.setHost(hostUser.getUsername());
        session.setNumPlayers(2);
        session.setName("Test Game");

        GameSession saved = gameSessionService.save(session);

        assertNotNull(saved.getId());
        assertEquals(GameState.PENDING, saved.getState());
        assertEquals(1, saved.getPlayers().size());
        assertEquals(hostUser.getUsername(), saved.getPlayers().iterator().next().getPlayer().getUsername());
    }

    @Test
    void testCalculateBoardSize() {
        assertEquals(5, gameSessionService.calculateBoardSize(GameMode.SOLITAIRE, 1));
        assertEquals(7, gameSessionService.calculateBoardSize(GameMode.VERSUS, 2));
        assertEquals(9, gameSessionService.calculateBoardSize(GameMode.BATTLE_ROYALE, 4));
        assertEquals(11, gameSessionService.calculateBoardSize(GameMode.BATTLE_ROYALE, 6));
        assertEquals(13, gameSessionService.calculateBoardSize(GameMode.BATTLE_ROYALE, 8));
    }

    @Test
    @Transactional
    void testStartGame_Versus() {
        GameSession session = new GameSession();
        session.setGameMode(GameMode.VERSUS);
        session.setHost(hostUser.getUsername());
        session.setNumPlayers(2);
        session.setName("Versus Game");
        session.setPlayers(new HashSet<>());
        GameSession savedGame = gameSessionService.save(session);

        String p2Name = "Player2_" + System.nanoTime();
        createDummyPlayer(p2Name);
        Player p2 = playerService.findPlayer(p2Name);

        PlayerGameSession pgs2 = new PlayerGameSession();
        pgs2.setPlayer(p2);
        pgs2.setGameSession(savedGame);
        pgs2.setEnergy(3);
        pgs2.setPlayerColor(Color.RED);
        savedGame.getPlayers().add(pgs2);
        gameSessionRepository.save(savedGame);

        gameSessionService.startGame(savedGame.getId(), hostUser, savedGame);

        GameSession started = gameSessionService.getGameById(savedGame.getId());

        assertEquals(GameState.ACTIVE, started.getState());
        assertNotNull(started.getStartTime());
        assertEquals(7, started.getBoardSize());
        assertNotNull(started.getGamePlayerTurnId());

        PlayerGameSession hostPgs = started.getPlayers().stream()
                .filter(p -> p.getPlayer().getUsername().equals(hostUser.getUsername())).findFirst().orElseThrow();
        long cardsInHand = playerCardRepository.findByPlayer(hostPgs).stream()
                .filter(c -> c.getLocation() == CardState.HAND).count();
        assertEquals(5, cardsInHand);
    }

    @Test
    @Transactional
    void testStartGame_NeedsMorePlayers() {
        GameSession session = new GameSession();
        session.setGameMode(GameMode.VERSUS);
        session.setHost(hostUser.getUsername());
        session.setNumPlayers(2);
        session.setName("Pending Game");
        GameSession saved = gameSessionService.save(session);

        assertThrows(ResponseStatusException.class, () -> gameSessionService.startGame(saved.getId(), hostUser, saved));
    }

    @Test
    @Transactional
    void testFinishGame() {
        GameSession session = new GameSession();
        session.setGameMode(GameMode.VERSUS);
        session.setHost(hostUser.getUsername());
        session.setNumPlayers(2);
        session.setName("Finishing Game");
        session.setState(GameState.ACTIVE);
        session.setStartTime(LocalDateTime.now().minusMinutes(10));

        PlayerGameSession pgs1 = new PlayerGameSession();
        pgs1.setPlayer(hostPlayer);
        pgs1.setGameSession(session);
        pgs1.setEnergy(3);
        session.getPlayers().add(pgs1);

        GameSession saved = gameSessionRepository.save(session);

        gameSessionService.finishGame(saved.getId(), hostUser.getUsername());

        GameSession finished = gameSessionService.getGameById(saved.getId());
        assertEquals(GameState.FINISHED, finished.getState());
        assertEquals(hostUser.getUsername(), finished.getWinner());
        assertNotNull(finished.getEndTime());
        assertTrue(finished.getDuration() >= 0);
    }

    @Test
    @Transactional
    void testUpdateGame() {
        GameSession session = new GameSession();
        session.setGameMode(GameMode.VERSUS);
        session.setHost(hostUser.getUsername());
        session.setNumPlayers(2);
        session.setName("Original Name");
        GameSession saved = gameSessionService.save(session);

        GameSession updateInfo = new GameSession();
        updateInfo.setName("Updated Name");
        updateInfo.setGameMode(GameMode.VERSUS);
        updateInfo.setNumPlayers(2);
        updateInfo.setHost(hostUser.getUsername());
        updateInfo.setPlayers(new HashSet<>());

        GameSession updated = gameSessionService.update(updateInfo, saved.getId());

        assertEquals("Updated Name", updated.getName());
        assertEquals(saved.getId(), updated.getId());
    }

    @Test
    @Transactional
    void testDeleteGame() {
        GameSession session = new GameSession();
        session.setGameMode(GameMode.VERSUS);
        session.setHost(hostUser.getUsername());
        session.setNumPlayers(2);
        session.setName("To Delete");
        GameSession saved = gameSessionService.save(session);

        gameSessionService.delete(saved.getId());

        assertTrue(gameSessionRepository.findById(saved.getId()).isEmpty());
    }

    @Test
    @Transactional
    void testHandleCardPlacement_Success() {
        GameSession game = createActiveGame();
        PlayerGameSession pgs = game.getPlayers().iterator().next();

        PlaceCardRequestDTO request = new PlaceCardRequestDTO();
        request.setRow(1);
        request.setCol(2);
        request.setOrientation(Orientation.N);

        long handCardId = playerCardRepository.findByPlayer(pgs).stream()
                .filter(c -> c.getLocation() == CardState.HAND)
                .findFirst().orElseThrow().getId();
        request.setPlayerCardId((int) handCardId);

        PlacedCard placed = gameSessionService.handleCardPlacement(game.getId(), hostUser.getUsername(), request);

        assertNotNull(placed);
        assertEquals(1, placed.getRow());
        assertEquals(2, placed.getCol());
        assertTrue(placedCardRepository.findByGameSessionId(game.getId()).size() > 1);
    }

    @Test
    @Transactional
    void testFindGamesByState() {
        GameSession active = new GameSession();
        active.setGameMode(GameMode.VERSUS);
        active.setHost(hostUser.getUsername());
        active.setNumPlayers(2);
        active.setName("Active Game");
        active.setState(GameState.ACTIVE);
        gameSessionService.save(active);

        GameSession finished = new GameSession();
        finished.setGameMode(GameMode.VERSUS);
        finished.setHost(hostUser.getUsername());
        finished.setNumPlayers(2);
        finished.setName("Finished Game");
        finished.setState(GameState.FINISHED);
        gameSessionService.save(finished);

        List<GameSession> activeGames = gameSessionService.getActiveGames();
        assertTrue(activeGames.stream().anyMatch(g -> "Active Game".equals(g.getName())));
        assertFalse(activeGames.stream().anyMatch(g -> "Finished Game".equals(g.getName())));

        List<GameSession> finishedGames = gameSessionService.getFinishedGames();
        assertTrue(finishedGames.stream().anyMatch(g -> "Finished Game".equals(g.getName())));
        assertFalse(finishedGames.stream().anyMatch(g -> "Active Game".equals(g.getName())));

        List<GameSession> pendingGames = gameSessionService.getPendingGames();
        assertNotNull(pendingGames);
    }

    @Test
    @Transactional
    void testStartGame_InvalidConstraints() {
        // Battle Royale < 2 players
        GameSession br = new GameSession();
        br.setGameMode(GameMode.BATTLE_ROYALE);
        br.setHost(hostUser.getUsername());
        br.setNumPlayers(4);
        br.setPlayers(new HashSet<>());
        GameSession savedBr = gameSessionService.save(br);
        // Only host is added by default, so 1 player
        assertThrows(ResponseStatusException.class,
                () -> gameSessionService.startGame(savedBr.getId(), hostUser, savedBr));

        // Team Battle < 4 players
        GameSession tb = new GameSession();
        tb.setGameMode(GameMode.TEAMBATTLE);
        tb.setHost(hostUser.getUsername());
        tb.setNumPlayers(4);
        tb.setPlayers(new HashSet<>());
        GameSession savedTb = gameSessionService.save(tb);
        // Only host
        assertThrows(ResponseStatusException.class,
                () -> gameSessionService.startGame(savedTb.getId(), hostUser, savedTb));
    }

    @Test
    @Transactional
    void testUpdateGame_NotFound() {
        GameSession updateInfo = new GameSession();
        updateInfo.setName("New Name");
        assertThrows(ResourceNotFoundException.class, () -> gameSessionService.update(updateInfo, 999999));
    }

    @Test
    void testFinishGame_Invalid() {
        // Game not active
        GameSession session = new GameSession();
        session.setHost(hostUser.getUsername());
        session.setGameMode(GameMode.VERSUS);
        session.setNumPlayers(2);
        session.setState(GameState.PENDING);
        session = gameSessionRepository.save(session);

        final int sessionId = session.getId();
        assertThrows(ResponseStatusException.class,
                () -> gameSessionService.finishGame(sessionId, hostUser.getUsername()));

        // Invalid winner - Need active game & player
        session.setState(GameState.ACTIVE);

        PlayerGameSession pgs = new PlayerGameSession();
        pgs.setPlayer(hostPlayer);
        pgs.setGameSession(session);
        pgs.setEnergy(0);
        session.getPlayers().add(pgs);

        gameSessionRepository.save(session);

        assertThrows(ResourceNotFoundException.class,
                () -> gameSessionService.finishGame(sessionId, "NonExistentUser"));
    }

    @Test
    @Transactional
    void testStartGame_Solitaire() {
        GameSession session = new GameSession();
        session.setGameMode(GameMode.SOLITAIRE);
        session.setHost(hostUser.getUsername());
        session.setNumPlayers(1);
        session.setName("Solitaire Game");
        session.setPlayers(new HashSet<>());
        GameSession savedGame = gameSessionService.save(session);

        gameSessionService.startGame(savedGame.getId(), hostUser, savedGame);

        GameSession started = gameSessionService.getGameById(savedGame.getId());
        assertEquals(GameState.ACTIVE, started.getState());
        assertEquals(5, started.getBoardSize());
    }

    @Test
    @Transactional
    void testStartGame_TeamBattle() {
        GameSession session = new GameSession();
        session.setGameMode(GameMode.TEAMBATTLE);
        session.setHost(hostUser.getUsername());
        session.setNumPlayers(4);
        session.setName("Team Battle Game");
        session.setPlayers(new HashSet<>());

        // The service.save() will add the host as the first player
        GameSession savedGame = gameSessionService.save(session);

        // Add 3 more dummy players to reach 4
        for (int i = 0; i < 3; i++) {
            String pName = "TBPlayer" + i + "_" + System.nanoTime();
            createDummyPlayer(pName);
            Player p = playerService.findPlayer(pName);
            PlayerGameSession pgs = new PlayerGameSession();
            pgs.setPlayer(p);
            pgs.setEnergy(0);
            pgs.setGameSession(savedGame);
            pgs.setPlayerColor(i == 0 ? Color.BLUE : i == 1 ? Color.RED : Color.YELLOW);
            pgs.setTeamNumber((i % 2 == 0) ? 2 : 1); // Host is team 1, so indices 0, 2 -> team 2, indices 1 -> team 1
            pgs = playerGameSessionRepository.save(pgs);
            savedGame.getPlayers().add(pgs);
        }
        gameSessionRepository.save(savedGame);
        entityManager.flush();
        entityManager.clear();

        gameSessionService.startGame(savedGame.getId(), hostUser, savedGame);

        GameSession started = gameSessionService.getGameById(savedGame.getId());
        assertEquals(GameState.ACTIVE, started.getState());

        // Verify team assignment
        long totalPlayers = started.getPlayers().size();
        assertEquals(4, totalPlayers);

        long t1 = started.getPlayers().stream().filter(p -> Integer.valueOf(1).equals(p.getTeamNumber())).count();
        long t2 = started.getPlayers().stream().filter(p -> Integer.valueOf(2).equals(p.getTeamNumber())).count();
        assertEquals(2, t1);
        assertEquals(2, t2);
    }

    @Test
    @Transactional
    void testHandleCardPlacement_AlreadyOccupied() {
        GameSession game = createActiveGame();

        PlaceCardRequestDTO request = new PlaceCardRequestDTO();
        request.setRow(2); // Start card is at (2,2)
        request.setCol(2);
        request.setOrientation(Orientation.N);

        PlayerGameSession pgs = game.getPlayers().iterator().next();
        long handCardId = playerCardRepository.findByPlayer(pgs).stream()
                .filter(c -> c.getLocation() == CardState.HAND)
                .findFirst().orElseThrow().getId();
        request.setPlayerCardId((int) handCardId);

        assertThrows(BadRequestException.class,
                () -> gameSessionService.handleCardPlacement(game.getId(), hostUser.getUsername(), request));
    }

    @Test
    @Transactional
    void testHandleCardPlacement_MaxCardsReached() {
        GameSession game = createActiveGame();
        PlayerGameSession pgs = game.getPlayers().iterator().next();
        pgs.setCardsPlacedThisRound(5); // Assuming max is < 5
        playerGameSessionRepository.save(pgs);

        PlaceCardRequestDTO request = new PlaceCardRequestDTO();
        request.setRow(1);
        request.setCol(2);
        request.setPlayerCardId(1); // Dummy

        assertThrows(BadRequestException.class,
                () -> gameSessionService.handleCardPlacement(game.getId(), hostUser.getUsername(), request));
    }

    @Test
    @Transactional
    void testFinishGame_InvalidWinner() {
        GameSession session = createActiveGame();
        String otherUser = "Other_" + System.nanoTime();
        createDummyPlayer(otherUser);

        // This should throw ResourceNotFoundException because otherUser is not in the
        // game
        assertThrows(ResourceNotFoundException.class,
                () -> gameSessionService.finishGame(session.getId(), otherUser));
    }

    @Test
    void testCalculateBoardSize_EdgeCases() {
        assertEquals(0, gameSessionService.calculateBoardSize(null, 2));
        assertEquals(0, gameSessionService.calculateBoardSize(GameMode.VERSUS, 10)); // Case > 8 not handled explicitly
    }

    private GameSession createActiveGame() {
        GameSession session = new GameSession();
        session.setGameMode(GameMode.SOLITAIRE);
        session.setHost(hostUser.getUsername());
        session.setNumPlayers(1);
        session.setName("Active Game");
        GameSession saved = gameSessionService.save(session);
        gameSessionService.startGame(saved.getId(), hostUser, saved);
        return gameSessionService.getGameById(saved.getId());
    }

    private void createDummyPlayer(String username) {
        Player p = new Player();
        p.setUsername(username);
        p.setPassword("pass");
        p.setFirstName("Fn");
        p.setLastName("Ln");
        p.setEmail(username + "@test.com");
        p.setAuthority(authoritiesService.findByAuthority("PLAYER"));
        p.setAvatarId(1);
        playerService.savePlayer(p);
    }

    @Test
    void testGetSpectatorView_NotFound() {
        assertThrows(ResponseStatusException.class, () -> gameSessionService.getSpectatorView(999999));
    }

    @Test
    @Transactional
    void testGetActiveGamesByFriends() {
        GameSession game = createActiveGame();
        String friendName = hostUser.getUsername();

        List<GameSessionDTO> result = gameSessionService.getActiveGamesByFriends(new HashSet<>(List.of(friendName)));

        assertNotNull(result);
        assertTrue(result.stream().anyMatch(g -> g.getId().equals(game.getId())));

        List<GameSessionDTO> emptyResult = gameSessionService
                .getActiveGamesByFriends(new HashSet<>(List.of("NonFriend")));
        assertTrue(emptyResult.isEmpty());
    }
}
