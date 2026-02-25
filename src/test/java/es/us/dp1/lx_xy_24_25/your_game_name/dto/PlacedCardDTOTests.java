package es.us.dp1.lx_xy_24_25.your_game_name.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardTemplate;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardType;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Color;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Orientation;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.placedCard.PlacedCard;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.PlayerGameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.PowerName;

class PlacedCardDTOTests {

    @Test
    void constructor_WithFullPlacedCard_ShouldMapAllFields() {
        GameSession game = new GameSession();
        game.setId(1);

        Player player = new Player();
        player.setId(10);
        player.setUsername("testUser");

        PlayerGameSession pgs = new PlayerGameSession();
        pgs.setId(100);
        pgs.setPlayer(player);
        pgs.setPlayerColor(Color.RED);

        CardTemplate template = new CardTemplate();
        template.setId(5);
        template.setImageId(50);
        template.setType(CardType.LINE);
        template.setColor(Color.BLUE);
        template.setPower(PowerName.JUMP_LINE);
        template.setInitiative(3);

        PlacedCard card = new PlacedCard();
        card.setId(200);
        card.setRow(2);
        card.setCol(3);
        card.setOrientation(Orientation.N);
        card.setPlacedAt(LocalDateTime.now());
        card.setGameSession(game);
        card.setPlacedBy(pgs);
        card.setTemplate(template);

        PlacedCardDTO dto = new PlacedCardDTO(card);

        assertEquals(200, dto.getId());
        assertEquals(1, dto.getGameSessionId());
        assertEquals(2, dto.getRow());
        assertEquals(3, dto.getCol());
        assertEquals(Orientation.N, dto.getOrientation());
        assertNotNull(dto.getPlacedAt());
        assertEquals(100, dto.getPlacedByPlayerGameSessionId());
        assertEquals("testUser", dto.getPlacedByUsername());
        assertEquals(10, dto.getPlacedByPlayerId());
        assertEquals(Color.RED, dto.getPlacedByPlayerColor());
        assertEquals(5, dto.getCardTemplateId());
        assertEquals(50, dto.getImageId());
        assertEquals(CardType.LINE, dto.getCardType());
        assertEquals(Color.BLUE, dto.getCardColor());
        assertEquals(PowerName.JUMP_LINE, dto.getCardPower());
        assertEquals(3, dto.getInitiative());
    }

    @Test
    void constructor_WithNullCard_ShouldNotThrow() {
        PlacedCardDTO dto = new PlacedCardDTO(null);
        assertNull(dto.getId());
    }

    @Test
    void constructor_WithMinimalCard_ShouldHandleNulls() {
        PlacedCard card = new PlacedCard();
        card.setId(1);
        card.setRow(0);
        card.setCol(0);

        PlayerGameSession pgs = new PlayerGameSession();
        pgs.setId(1);
        card.setPlacedBy(pgs);

        PlacedCardDTO dto = new PlacedCardDTO(card);

        assertEquals(1, dto.getId());
        assertEquals(0, dto.getRow());
        assertEquals(0, dto.getCol());
    }

    @Test
    void noArgsConstructor_ShouldWork() {
        PlacedCardDTO dto = new PlacedCardDTO();
        assertNull(dto.getId());
    }

    @Test
    void settersAndGetters_ShouldWork() {
        PlacedCardDTO dto = new PlacedCardDTO();
        dto.setId(1);
        dto.setRow(5);
        dto.setCol(10);
        dto.setOrientation(Orientation.E);
        dto.setPlacedByUsername("user");
        dto.setCardType(CardType.ENERGY);

        assertEquals(1, dto.getId());
        assertEquals(5, dto.getRow());
        assertEquals(10, dto.getCol());
        assertEquals(Orientation.E, dto.getOrientation());
        assertEquals("user", dto.getPlacedByUsername());
        assertEquals(CardType.ENERGY, dto.getCardType());
    }
}
