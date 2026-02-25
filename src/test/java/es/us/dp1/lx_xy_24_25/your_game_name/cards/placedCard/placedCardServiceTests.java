package es.us.dp1.lx_xy_24_25.your_game_name.cards.placedCard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardTemplate;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardType;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Orientation;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.playerCard.PlayerCard;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.playerCard.PlayerCardRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.PlaceCardRequestDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.BadRequestException;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.PlayerGameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;

@ExtendWith(MockitoExtension.class)
class PlacedCardServiceTests {

    @Mock
    private PlacedCardRepository placedCardRepository;
    @Mock
    private PlayerCardRepository playerCardRepository;

    @InjectMocks
    private PlacedCardService service;

    private GameSession game;
    private PlayerGameSession pgs;
    private PlayerCard playerCard;
    private CardTemplate template;
    private PlaceCardRequestDTO request;

    @BeforeEach
    void setUp() {
        game = new GameSession();
        game.setId(1);
        game.setBoardSize(5);

        Player player = new Player();
        player.setId(1);

        pgs = new PlayerGameSession();
        pgs.setId(1);
        pgs.setPlayer(player);
        pgs.setGameSession(game);

        template = new CardTemplate();
        template.setId(1);
        template.setType(CardType.LINE);
        template.setDefaultEntrance(Orientation.N);
        template.setDefaultExits(Set.of(Orientation.S));

        playerCard = new PlayerCard();
        playerCard.setId(100);
        playerCard.setPlayer(pgs);
        playerCard.setTemplate(template);

        request = new PlaceCardRequestDTO();
        request.setPlayerCardId(100);
        request.setRow(2);
        request.setCol(2);
        request.setOrientation(Orientation.N);
    }

    @Test
    void placeCard_ShouldSuccess_FirstCardCenter() {
        when(playerCardRepository.findById(100)).thenReturn(Optional.of(playerCard));
        when(placedCardRepository.existsByRowAndColAndGameSessionId(2, 2, 1)).thenReturn(false);
       
        when(placedCardRepository.findByGameSessionId(1)).thenReturn(List.of());
        
        when(placedCardRepository.save(any(PlacedCard.class))).thenAnswer(i -> i.getArguments()[0]);

        PlacedCard result = service.placeCard(game, pgs, request);

        assertNotNull(result);
        assertEquals(2, result.getRow());
        assertEquals(2, result.getCol());
    }

    @Test
    void placeCard_ShouldFail_Occupied() {
        when(playerCardRepository.findById(100)).thenReturn(Optional.of(playerCard));
        when(placedCardRepository.existsByRowAndColAndGameSessionId(2, 2, 1)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> service.placeCard(game, pgs, request));
    }

    @Test
    void placeCard_ShouldFail_NotConnectingToOwn() {
        when(playerCardRepository.findById(100)).thenReturn(Optional.of(playerCard));
        when(placedCardRepository.existsByRowAndColAndGameSessionId(2, 2, 1)).thenReturn(false);
        
        PlacedCard otherCard = new PlacedCard();
        otherCard.setTemplate(template);
        otherCard.setPlacedBy(pgs);
        when(placedCardRepository.findByGameSessionIdAndPlacedByIdOrderByPlacedAtDesc(1, 1)).thenReturn(List.of(otherCard));
        
        when(placedCardRepository.findByRowAndColAndGameSessionId(anyInt(), anyInt(), eq(1))).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> service.placeCard(game, pgs, request));
    }

    @Test
    void placeCard_ShouldSuccess_ConnectingValid() {
        when(playerCardRepository.findById(100)).thenReturn(Optional.of(playerCard));
        when(placedCardRepository.existsByRowAndColAndGameSessionId(2, 2, 1)).thenReturn(false);

        PlacedCard startCard = new PlacedCard();
        startCard.setId(50);
        startCard.setTemplate(template);
        startCard.setPlacedBy(pgs);
        startCard.setRow(1);
        startCard.setCol(2);
        startCard.setOrientation(Orientation.N);

        when(placedCardRepository.findByGameSessionId(1)).thenReturn(List.of(startCard));

        when(placedCardRepository.findByRowAndColAndGameSessionId(1, 2, 1)).thenReturn(Optional.of(startCard));
        when(placedCardRepository.findByRowAndColAndGameSessionId(3, 2, 1)).thenReturn(Optional.empty());
        when(placedCardRepository.findByRowAndColAndGameSessionId(2, 1, 1)).thenReturn(Optional.empty());
        when(placedCardRepository.findByRowAndColAndGameSessionId(2, 3, 1)).thenReturn(Optional.empty());

        when(placedCardRepository.findByGameSessionIdAndPlacedByIdOrderByPlacedAtDesc(1, 1)).thenReturn(List.of(startCard));

        when(placedCardRepository.save(any(PlacedCard.class))).thenAnswer(i -> i.getArguments()[0]);

        PlacedCard result = service.placeCard(game, pgs, request);
        assertNotNull(result);
    }
}