package es.us.dp1.lx_xy_24_25.your_game_name.cards.playerCard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardState;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardTemplate;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardTemplateRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardType;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Color;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Orientation;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.placedCard.PlacedCard;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.placedCard.PlacedCardRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.PlayerCardDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameMode;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSessionRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameState;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.PlayerGameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.PlayerGameSessionRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.user.Authorities;
import es.us.dp1.lx_xy_24_25.your_game_name.user.AuthoritiesService;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
public class PlayerCardServiceTests {

    @Autowired
    private PlayerCardService playerCardService;

    @Autowired
    private GameSessionRepository gameSessionRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private AuthoritiesService authoritiesService;

    @Autowired
    private CardTemplateRepository cardTemplateRepository;

    @Autowired
    private PlayerGameSessionRepository playerGameSessionRepository;

    @Autowired
    private PlacedCardRepository placedCardRepository;

    @Autowired
    private PlayerCardRepository playerCardRepository;

    private GameSession gameSession;
    private PlayerGameSession playerGameSession;
    private CardTemplate movementTemplate;
    private CardTemplate startTemplate;

    @BeforeEach
    void setup() {
        playerCardRepository.deleteAll();
        placedCardRepository.deleteAll();
        cardTemplateRepository.deleteAll();

        Authorities playerAuth = authoritiesService.findByAuthority("PLAYER");
        String username = "testCardPlayer";

        Player player = playerRepository.findByUsername(username).orElseGet(() -> {
            Player newPlayer = new Player();
            newPlayer.setUsername(username);
            newPlayer.setPassword("password");
            newPlayer.setFirstName("Test");
            newPlayer.setLastName("Card");
            newPlayer.setEmail("testcard@example.com");
            newPlayer.setAuthority(playerAuth);
            return playerRepository.save(newPlayer);
        });

        gameSession = new GameSession();
        gameSession.setGameMode(GameMode.VERSUS);
        gameSession.setState(GameState.ACTIVE);
        gameSession.setBoardSize(5);
        gameSession.setHost(username);
        gameSession.setNumPlayers(1);
        gameSession.setHost(player.getUsername());
        gameSession.setRound(1);
        gameSession = gameSessionRepository.save(gameSession);

        playerGameSession = new PlayerGameSession();
        playerGameSession.setGameSession(gameSession);
        playerGameSession.setPlayer(player);
        playerGameSession.setEnergy(0);
        playerGameSession.setPlayerColor(Color.RED);
        playerGameSession.setHasRerolled(false);
        playerGameSession = playerGameSessionRepository.save(playerGameSession);

        movementTemplate = new CardTemplate();
        movementTemplate.setType(CardType.LINE);
        movementTemplate.setDefaultEntrance(Orientation.N);
        movementTemplate.setDefaultExits(Set.of(Orientation.S));
        movementTemplate.setColor(Color.RED);
        movementTemplate.setInitiative(5);
        movementTemplate.setImageId(1);
        movementTemplate = cardTemplateRepository.save(movementTemplate);

        for (int i = 0; i < 10; i++) {
            CardTemplate dummy = new CardTemplate();
            dummy.setType(CardType.LINE);
            dummy.setDefaultEntrance(Orientation.N);
            dummy.setDefaultExits(Set.of(Orientation.S));
            dummy.setColor(Color.RED);
            dummy.setInitiative(1);
            dummy.setImageId(i + 2);
            cardTemplateRepository.save(dummy);
        }

        startTemplate = new CardTemplate();
        startTemplate.setType(CardType.START);
        startTemplate.setDefaultEntrance(Orientation.N);
        startTemplate.setDefaultExits(Set.of(Orientation.S));
        startTemplate.setInitiative(0);
        startTemplate.setImageId(100);
        startTemplate = cardTemplateRepository.save(startTemplate);
    }

    @Test
    void shouldGetPlayerHand() {
        createCard(playerGameSession, movementTemplate, CardState.HAND);
        createCard(playerGameSession, movementTemplate, CardState.HAND);
        createCard(playerGameSession, movementTemplate, CardState.HAND);
        createCard(playerGameSession, movementTemplate, CardState.DECK);
        List<PlayerCardDTO> hand = playerCardService.getPlayerHand(playerGameSession.getId());

        assertNotNull(hand);
        assertEquals(3, hand.size());
    }

    @Test
    void shouldThrowNotFoundWhenGettingHandForInvalidSession() {
        assertThrows(ResponseStatusException.class, () -> {
            playerCardService.getPlayerHand(9999);
        });
    }

    @Test
    void shouldRerollHandSuccessfully() {

        PlayerCard c1 = createCard(playerGameSession, movementTemplate, CardState.HAND);
        PlayerCard c2 = createCard(playerGameSession, movementTemplate, CardState.DECK);

        playerCardService.rerollHand(playerGameSession.getId());

        PlayerGameSession updatedPgs = playerGameSessionRepository.findById(playerGameSession.getId()).get();
        assertTrue(updatedPgs.getHasRerolled());

        assertTrue(playerCardRepository.findById(c1.getId()).isEmpty());
        assertTrue(playerCardRepository.findById(c2.getId()).isEmpty());

        List<PlayerCard> newHand = playerCardRepository.findByPlayerAndLocation(updatedPgs, CardState.HAND);
        assertFalse(newHand.isEmpty());
        assertEquals(5, newHand.size());
    }

    @Test
    void shouldFailRerollIfRoundIsNotOne() {
        gameSession.setRound(2);
        gameSessionRepository.save(gameSession);

        assertThrows(ResponseStatusException.class, () -> {
            playerCardService.rerollHand(playerGameSession.getId());
        });
    }

    @Test
    void shouldFailRerollIfAlreadyRerolled() {
        playerGameSession.setHasRerolled(true);
        playerGameSessionRepository.save(playerGameSession);

        assertThrows(ResponseStatusException.class, () -> {
            playerCardService.rerollHand(playerGameSession.getId());
        });
    }

    @Test
    void shouldFailRerollIfPlayerHasPlacedLineCard() {
        PlacedCard placedCard = new PlacedCard();
        placedCard.setGameSession(gameSession);
        placedCard.setPlacedBy(playerGameSession);
        placedCard.setTemplate(movementTemplate);
        placedCard.setRow(0);
        placedCard.setCol(0);
        placedCard.setOrientation(Orientation.N);
        placedCard.setPlacedAt(LocalDateTime.now());
        placedCardRepository.save(placedCard);

        gameSession.getPlacedCards().add(placedCard);
        gameSessionRepository.save(gameSession);

        assertThrows(ResponseStatusException.class, () -> {
            playerCardService.rerollHand(playerGameSession.getId());
        });
    }

    @Test
    void shouldAllowRerollIfPlayerHasPlacedStartCardOnly() {
        PlacedCard placedCard = new PlacedCard();
        placedCard.setGameSession(gameSession);
        placedCard.setPlacedBy(playerGameSession);
        placedCard.setTemplate(startTemplate);
        placedCard.setRow(0);
        placedCard.setCol(0);
        placedCard.setOrientation(Orientation.N);
        placedCard.setPlacedAt(LocalDateTime.now());
        placedCardRepository.save(placedCard);

        gameSession.getPlacedCards().add(placedCard);
        gameSessionRepository.save(gameSession);

        createCard(playerGameSession, movementTemplate, CardState.HAND);

        playerCardService.rerollHand(playerGameSession.getId());

        PlayerGameSession updatedPgs = playerGameSessionRepository.findById(playerGameSession.getId()).get();
        assertTrue(updatedPgs.getHasRerolled());
    }

    @Test
    void shouldFailRerollIfDeckIsEmpty() {
        playerCardRepository.deleteAll();
        assertEquals(0, playerCardRepository.countByPlayer(playerGameSession));

        assertThrows(ResponseStatusException.class, () -> {
            playerCardService.rerollHand(playerGameSession.getId());
        });
    }

    private PlayerCard createCard(PlayerGameSession pgs, CardTemplate template, CardState state) {
        PlayerCard pc = new PlayerCard();
        pc.setPlayer(pgs);
        pc.setTemplate(template);
        pc.setLocation(state);
        pc.setUsed(false);
        return playerCardRepository.save(pc);
    }
}