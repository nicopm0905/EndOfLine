package es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardState;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardTemplate;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.EnergyEffectType;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.playerCard.PlayerCard;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.playerCard.PlayerCardRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.BadRequestException;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameMode;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSessionRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameState;

@ExtendWith(MockitoExtension.class)
class PlayerGameSessionServiceTest {

    @Mock
    private PlayerGameSessionRepository repository;

    @Mock
    private PlayerCardRepository playerCardRepository;

    @Mock
    private GameSessionRepository gameSessionRepository;

    @InjectMocks
    private PlayerGameSessionService service;

    private PlayerGameSession pgs;
    private GameSession gameSession;
    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player();
        player.setUsername("testUser");

        gameSession = new GameSession();
        gameSession.setId(1);
        gameSession.setRound(3);
        gameSession.setGameMode(GameMode.VERSUS);

        pgs = new PlayerGameSession();
        pgs.setId(1);
        pgs.setPlayer(player);
        pgs.setGameSession(gameSession);
        pgs.setEnergy(3);
        pgs.setCardsPlacedThisRound(0);
        pgs.setDiscardPile(new ArrayList<>());

        gameSession.setPlayers(new java.util.HashSet<>());
        gameSession.getPlayers().add(pgs);
    }

    @Test
    void shouldConsumeEnergySuccessfully() {
        when(repository.findByGameSessionIdAndPlayerUsername(1, "testUser")).thenReturn(Optional.of(pgs));
        when(repository.save(any(PlayerGameSession.class))).thenReturn(pgs);

        PlayerGameSession result = service.consumeEnergy(1, "testUser", "BOOST");

        assertNotNull(result);
        assertEquals(2, result.getEnergy());
        assertEquals(EnergyEffectType.BOOST, result.getActiveEnergyEffect());
        assertEquals(3, result.getEnergyCardsToPlaceOverride());
    }

    @Test
    void shouldThrowExceptionWhenConsumingEnergyWithoutEnoughEnergy() {
        pgs.setEnergy(0);
        when(repository.findByGameSessionIdAndPlayerUsername(1, "testUser")).thenReturn(Optional.of(pgs));

        assertThrows(ResponseStatusException.class, () -> service.consumeEnergy(1, "testUser", "BOOST"));
    }

    @Test
    void shouldThrowExceptionWhenConsumingEnergyInEarlyRounds() {
        gameSession.setRound(1);
        when(repository.findByGameSessionIdAndPlayerUsername(1, "testUser")).thenReturn(Optional.of(pgs));

        assertThrows(ResponseStatusException.class, () -> service.consumeEnergy(1, "testUser", "BOOST"));
    }

    @Test
    void shouldThrowExceptionWhenConsumingEnergyTwiceInSameRound() {
        pgs.setLastEnergyRoundUsed(3);
        when(repository.findByGameSessionIdAndPlayerUsername(1, "testUser")).thenReturn(Optional.of(pgs));

        assertThrows(ResponseStatusException.class, () -> service.consumeEnergy(1, "testUser", "BOOST"));
    }

    @Test
    void shouldDrawFirstCardForInitiative() {
        PlayerCard card = new PlayerCard();
        card.setLocation(CardState.DECK);
        card.setTemplate(new CardTemplate());
        pgs.setCards(java.util.Set.of(card));

        when(repository.findByGameSessionIdAndPlayerUsername(1, "testUser")).thenReturn(Optional.of(pgs));

        CardTemplate result = service.drawFirstCardForInitiative(1, "testUser");

        assertNotNull(result);
        assertNotNull(pgs.getInitiativeCard());
    }

    @Test
    void shouldDrawTopCard() {
        PlayerCard card = new PlayerCard();
        card.setLocation(CardState.DECK);

        when(playerCardRepository.findFirstByPlayerAndLocationOrderByDeckOrderAsc(pgs, CardState.DECK))
                .thenReturn(Optional.of(card));

        service.drawTopCard(pgs);

        assertEquals(CardState.HAND, card.getLocation());
        verify(playerCardRepository).save(card);
    }

    @Test
    void shouldDiscardActiveCard() {
        PlayerCard card = new PlayerCard();
        card.setId(100);
        card.setLocation(CardState.HAND);
        card.setPlayer(pgs);
        card.setTemplate(new CardTemplate());

        when(playerCardRepository.findById(100)).thenReturn(Optional.of(card));
        when(playerCardRepository.findFirstByPlayerAndLocationOrderByDeckOrderAsc(pgs, CardState.DECK))
                .thenReturn(Optional.of(new PlayerCard()));
        when(playerCardRepository.save(any(PlayerCard.class))).thenReturn(card);
        service.discardActiveCard(1, "testUser", 100);

        assertEquals(CardState.DISCARD, card.getLocation());
        assertEquals(1, pgs.getDiscardPile().size());
        verify(repository).save(pgs);
    }

    @Test
    void shouldDrawCardFromDiscardAction() {
        gameSession.setGameMode(GameMode.SOLITAIRE);
        CardTemplate template = new CardTemplate();
        template.setId(1);
        pgs.getDiscardPile().add(template);

        PlayerCard physicalCard = new PlayerCard();
        physicalCard.setTemplate(template);
        physicalCard.setLocation(CardState.DISCARD);

        when(gameSessionRepository.findById(1)).thenReturn(Optional.of(gameSession));
        when(playerCardRepository.countByPlayerAndLocationAndUsedFalse(pgs, CardState.HAND)).thenReturn(0L);
        when(playerCardRepository.findByPlayer(pgs)).thenReturn(List.of(physicalCard));

        service.drawCardFromDiscardAction(1, "testUser");

        assertEquals(CardState.HAND, physicalCard.getLocation());
        verify(repository).save(pgs);
    }

    @Test
    void shouldThrowExceptionWhenDrawFromDiscardActionInVersus() {
        gameSession.setGameMode(GameMode.VERSUS);
        when(gameSessionRepository.findById(1)).thenReturn(Optional.of(gameSession));

        assertThrows(BadRequestException.class, () -> service.drawCardFromDiscardAction(1, "testUser"));
    }

    @Test
    void shouldDrawCardFromDeckAction() {
        gameSession.setGameMode(GameMode.SOLITAIRE);
        when(gameSessionRepository.findById(1)).thenReturn(Optional.of(gameSession));
        when(playerCardRepository.countByPlayerAndLocationAndUsedFalse(pgs, CardState.HAND)).thenReturn(0L);
        PlayerCard card = new PlayerCard();
        card.setLocation(CardState.DECK);
        when(playerCardRepository.findFirstByPlayerAndLocationOrderByDeckOrderAsc(pgs, CardState.DECK))
                .thenReturn(Optional.of(card));

        service.drawCardFromDeckAction(1, "testUser");

        assertEquals(CardState.HAND, card.getLocation());
    }

    @Test
    void shouldSwitchTeamSuccessfully() {
        gameSession.setGameMode(GameMode.TEAMBATTLE);
        gameSession.setState(GameState.PENDING);
        gameSession.setNumPlayers(4);

        when(gameSessionRepository.findById(1)).thenReturn(Optional.of(gameSession));

        service.switchTeam(1, "testUser", 2);

        assertEquals(2, pgs.getTeamNumber());
        verify(repository).save(pgs);
    }

    @Test
    void shouldThrowExceptionWhenSwitchTeamLocked() {
        gameSession.setGameMode(GameMode.TEAMBATTLE);
        gameSession.setState(GameState.ACTIVE); 

        when(gameSessionRepository.findById(1)).thenReturn(Optional.of(gameSession));

        assertThrows(BadRequestException.class, () -> service.switchTeam(1, "testUser", 2));
    }
}
