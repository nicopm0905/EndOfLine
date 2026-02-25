package es.us.dp1.lx_xy_24_25.your_game_name.cards.placedCard;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardTemplate;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardType;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Orientation;
import es.us.dp1.lx_xy_24_25.your_game_name.configuration.SecurityConfiguration;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.PlaceCardRequestDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSessionService;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.PlayerGameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;

@WebMvcTest(controllers = PlacedCardController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityConfiguration.class)
public class placedCardControllerTests {

    private static final String BASE_URL = "/api/v1/gamesessions";
    private static final Integer GAME_ID = 1;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GameSessionService gameSessionService;

    @MockBean
    private PlacedCardRepository placedCardRepository;

    @MockBean
    private org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;

    private PlacedCard placedCard;
    private CardTemplate template;
    private PlayerGameSession pgs;

    @BeforeEach
    void setup() {
        template = new CardTemplate();
        template.setId(1);
        template.setType(CardType.LINE);
        template.setDefaultEntrance(Orientation.N);

        Player player = new Player();
        player.setUsername("player1");

        pgs = new PlayerGameSession();
        pgs.setId(1);
        pgs.setPlayer(player);

        placedCard = new PlacedCard();
        placedCard.setId(1);
        placedCard.setRow(2);
        placedCard.setCol(2);
        placedCard.setOrientation(Orientation.N);
        placedCard.setTemplate(template);
        placedCard.setPlacedBy(pgs);
        placedCard.setPlacedAt(LocalDateTime.now());
        placedCard.setGameSession(new GameSession());
    }

    @Test
    @WithMockUser(username = "player1", authorities = { "PLAYER" })
    void shouldPlaceCard() throws Exception {
        PlaceCardRequestDTO request = new PlaceCardRequestDTO();
        request.setPlayerCardId(10);
        request.setRow(2);
        request.setCol(2);
        request.setOrientation(Orientation.N);

        GameSession gameSession = new GameSession();
        gameSession.setId(GAME_ID);
        gameSession.setPlayers(new java.util.HashSet<>());
        gameSession.setPlacedCards(new java.util.ArrayList<>());

        when(gameSessionService.handleCardPlacement(eq(GAME_ID), anyString(), any(PlaceCardRequestDTO.class)))
                .thenReturn(placedCard);
        when(gameSessionService.getGameById(GAME_ID)).thenReturn(gameSession);

        mockMvc.perform(post(BASE_URL + "/{gameId}/place-card", GAME_ID)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.row").value(2))
                .andExpect(jsonPath("$.col").value(2))
                .andExpect(jsonPath("$.orientation").value("N"));
    }

    @Test
    @WithMockUser(username = "player1", authorities = { "PLAYER" })
    void shouldGetPlacedCards() throws Exception {
        when(placedCardRepository.findByGameSessionId(GAME_ID)).thenReturn(List.of(placedCard));

        mockMvc.perform(get(BASE_URL + "/{gameId}/placed-cards", GAME_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].row").value(2));
    }

    @Test
    @WithMockUser(username = "player1", authorities = { "PLAYER" })
    void shouldGetCardAtPosition() throws Exception {
        PlacedCardController.PositionRequestDTO positionRequest = new PlacedCardController.PositionRequestDTO();
        positionRequest.setRow(2);
        positionRequest.setCol(2);

        when(placedCardRepository.findByRowAndColAndGameSessionId(2, 2, GAME_ID))
                .thenReturn(Optional.of(placedCard));

        mockMvc.perform(get(BASE_URL + "/{gameId}/placed-cards/position", GAME_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(positionRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.row").value(2))
                .andExpect(jsonPath("$.col").value(2));
    }

    @Test
    @WithMockUser(username = "player1", authorities = { "PLAYER" })
    void shouldReturnNotFoundWhenNoCardAtPosition() throws Exception {
        PlacedCardController.PositionRequestDTO positionRequest = new PlacedCardController.PositionRequestDTO();
        positionRequest.setRow(5);
        positionRequest.setCol(5);

        when(placedCardRepository.findByRowAndColAndGameSessionId(5, 5, GAME_ID))
                .thenReturn(Optional.empty());

        mockMvc.perform(get(BASE_URL + "/{gameId}/placed-cards/position", GAME_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(positionRequest)))
                .andExpect(status().isNotFound());
    }
}