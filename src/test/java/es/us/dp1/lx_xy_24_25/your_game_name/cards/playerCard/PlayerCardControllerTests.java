package es.us.dp1.lx_xy_24_25.your_game_name.cards.playerCard;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import es.us.dp1.lx_xy_24_25.your_game_name.configuration.SecurityConfiguration;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.PlayerCardDTO;

@WebMvcTest(controllers = PlayerCardController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfiguration.class),
    excludeAutoConfiguration = org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class)
class PlayerCardControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlayerCardService playerCardService;

    @Test
    @WithMockUser
    void getPlayerHand_ShouldReturnCards() throws Exception {
        PlayerCardDTO card = new PlayerCardDTO();
        when(playerCardService.getPlayerHand(1)).thenReturn(List.of(card));

        mockMvc.perform(get("/api/v1/cards/player-hand/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser
    void rerollHand_ShouldReturnSuccess() throws Exception {
        mockMvc.perform(put("/api/v1/cards/1/reroll-hand"))
            .andExpect(status().isOk())
            .andExpect(content().string("Hand rerolled successfully"));
    }
}
