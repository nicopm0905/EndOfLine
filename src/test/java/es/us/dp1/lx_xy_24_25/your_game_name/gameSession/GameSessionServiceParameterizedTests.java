package es.us.dp1.lx_xy_24_25.your_game_name.gameSession;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;

class GameSessionServiceParameterizedTests {

    private final GameSessionService gameSessionService = new GameSessionService(null, null, null, null, null, null, null, null, null, null);

    @ParameterizedTest
    @CsvSource({
        "SOLITAIRE, 1, 5",
        "SOLITARY_PUZZLE, 1, 5",
        "VERSUS, 2, 7",
        "VERSUS, 3, 7",
        "BATTLE_ROYALE, 4, 9",
        "BATTLE_ROYALE, 5, 9",
        "BATTLE_ROYALE, 6, 11",
        "TEAMBATTLE, 7, 11",
        "TEAMBATTLE, 8, 13",
        ", 2, 0" 
    })
    void shouldCalculateCorrectBoardSize(GameMode mode, int maxPlayers, int expectedSize) {
        int result = gameSessionService.calculateBoardSize(mode, maxPlayers);
        assertEquals(expectedSize, result, "Board size calculation incorrect for mode " + mode + " and players " + maxPlayers);
    }
}
