package es.us.dp1.lx_xy_24_25.your_game_name.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameMode;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSessionService;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameState;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.AuthoritiesService;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;

@Epic("Game Session Module")
@Feature("Game Session Service Integration Tests")
@Owner("DP1-tutors")
@SpringBootTest
@AutoConfigureTestDatabase
class GameSessionServiceTest {

    @Autowired
    private GameSessionService gameSessionService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private AuthoritiesService authoritiesService;

    private Player hostPlayer;
    private GameSession activeGame;

    @BeforeEach
    void setUp() {
        String username = "HostUser" + System.nanoTime();
        hostPlayer = new Player();
        hostPlayer.setUsername(username);
        hostPlayer.setPassword("password");
        hostPlayer.setFirstName("Host");
        hostPlayer.setLastName("User");
        hostPlayer.setEmail(username + "@example.com");
        hostPlayer.setAuthority(authoritiesService.findByAuthority("PLAYER"));
        playerService.savePlayer(hostPlayer);

        activeGame = new GameSession();
        activeGame.setHost(hostPlayer.getUsername());
        activeGame.setGameMode(GameMode.VERSUS);
        activeGame.setNumPlayers(2);
        activeGame.setState(GameState.ACTIVE);
        activeGame.setStartTime(LocalDateTime.now());
        gameSessionService.save(activeGame);
    }

    @Test
    @Transactional
    void shouldSaveGameSession() {
        GameSession newGame = new GameSession();
        newGame.setHost(hostPlayer.getUsername());
        newGame.setGameMode(GameMode.BATTLE_ROYALE);
        newGame.setNumPlayers(4);
        newGame.setName("New Battle");

        GameSession saved = gameSessionService.save(newGame);

        assertNotNull(saved.getId());
        assertEquals(GameState.PENDING, saved.getState());
        assertEquals(hostPlayer.getUsername(), saved.getHost());
    }

    @Test
    @Transactional
    void shouldFindGameById() {
        GameSession found = gameSessionService.getGameById(activeGame.getId());
        assertNotNull(found);
        assertEquals(activeGame.getId(), found.getId());
    }

    @Test
    void shouldNotFindNonExistentGame() {
        GameSession found = gameSessionService.getGameById(99999);
        assertNull(found);
    }

    @Test
    @Transactional
    void shouldUpdateGameSession() {
        GameSession toUpdate = gameSessionService.getGameById(activeGame.getId());
        toUpdate.setRound(5);
        toUpdate.setGameMode(GameMode.SOLITAIRE);

        GameSession updated = gameSessionService.update(toUpdate, activeGame.getId());

        assertEquals(5, updated.getRound());
        assertEquals(GameMode.SOLITAIRE, updated.getGameMode());
    }

    @Test
    @Transactional
    void shouldDeleteGameSession() {
        GameSession gameToDelete = new GameSession();
        gameToDelete.setHost(hostPlayer.getUsername());
        gameToDelete.setGameMode(GameMode.VERSUS);
        gameToDelete.setNumPlayers(2);
        gameSessionService.save(gameToDelete);
        Integer id = gameToDelete.getId();

        gameSessionService.delete(id);

        assertNull(gameSessionService.getGameById(id));
    }

    @Test
    @Transactional
    void shouldFilterGamesByState() {
        GameSession pendingGame = new GameSession();
        pendingGame.setHost(hostPlayer.getUsername());
        pendingGame.setGameMode(GameMode.VERSUS);
        pendingGame.setNumPlayers(2);
        pendingGame.setState(GameState.PENDING);
        gameSessionService.save(pendingGame);

        GameSession finishedGame = new GameSession();
        finishedGame.setHost(hostPlayer.getUsername());
        finishedGame.setGameMode(GameMode.VERSUS);
        finishedGame.setNumPlayers(2);
        finishedGame.setState(GameState.FINISHED);
        gameSessionService.save(finishedGame);

        List<GameSession> activeList = gameSessionService.getActiveGames();
        List<GameSession> pendingList = gameSessionService.getPendingGames();
        List<GameSession> finishedList = gameSessionService.getFinishedGames();

        assertTrue(activeList.stream().anyMatch(g -> g.getId().equals(activeGame.getId())));
        assertTrue(pendingList.stream().anyMatch(g -> g.getId().equals(pendingGame.getId())));
        assertTrue(finishedList.stream().anyMatch(g -> g.getId().equals(finishedGame.getId())));
        
        assertFalse(activeList.contains(pendingGame));
    }

    @Test
    @Transactional
    void shouldFinishGame() {
        String winnerUsername = hostPlayer.getUsername();
        
        GameSession finished = gameSessionService.finishGame(activeGame.getId(), winnerUsername);

        assertEquals(GameState.FINISHED, finished.getState());
        assertEquals(winnerUsername, finished.getWinner());
        assertNotNull(finished.getEndTime());
    }

    @Test
    void shouldCalculateBoardSizeCorrectly() {
        assertEquals(5, gameSessionService.calculateBoardSize(GameMode.SOLITAIRE, 1));
        assertEquals(7, gameSessionService.calculateBoardSize(GameMode.BATTLE_ROYALE, 3));
        assertEquals(9, gameSessionService.calculateBoardSize(GameMode.BATTLE_ROYALE, 4));
        assertEquals(9, gameSessionService.calculateBoardSize(GameMode.BATTLE_ROYALE, 5));
        assertEquals(13, gameSessionService.calculateBoardSize(GameMode.BATTLE_ROYALE, 8));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentGame() {
        GameSession dummy = new GameSession();
        dummy.setId(9999);
        assertThrows(ResourceNotFoundException.class, () -> {
            gameSessionService.update(dummy, 9999);
        });
    }
}