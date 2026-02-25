package es.us.dp1.lx_xy_24_25.your_game_name.cards.playerCard;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardState;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardType;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.PlayerCardDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameMode;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSessionService;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameState;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.PlayerGameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.PlayerGameSessionRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlayerCardService {

    private final PlayerCardRepository playerCardRepository;
    private final PlayerGameSessionRepository playerGameSessionRepository;
    private final GameSessionService gameSessionService;
    

    public List<PlayerCardDTO> getPlayerHand(Integer playerGameSessionId) {
        var playerSession = playerGameSessionRepository.findById(playerGameSessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PlayerGameSession not found"));
        List<PlayerCard> cards = playerCardRepository
            .findByPlayerAndLocation(playerSession, CardState.HAND);

        return cards.stream()
                .limit(6)
                .map(PlayerCardDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void rerollHand(Integer playerGameSessionId) {
    PlayerGameSession pgs = playerGameSessionRepository.findById(playerGameSessionId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PlayerGameSession not found"));

    GameSession game = pgs.getGameSession();
    if (game.getRound() > 1 || game.getState() != GameState.ACTIVE || pgs.getHasRerolled() == true)  {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot reroll hand after round 1");
    }

    boolean hasPlacedLineCard = game.getPlacedCards() != null &&
        game.getPlacedCards().stream()
        .filter(pc -> pc.getTemplate() != null && pc.getTemplate().getType() == CardType.LINE)
        .anyMatch(pc -> pc.getPlacedBy() != null && pc.getPlacedBy().getId().equals(pgs.getId()));

    if (hasPlacedLineCard) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot reroll hand after placing a card");
    }

    long numCards = playerCardRepository.countByPlayer(pgs);
    if (numCards == 0) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No deck found to reroll");
    }
    if (pgs.getCards() != null) {
        pgs.getCards().clear();
    }
    playerCardRepository.deleteAllByPlayer(pgs);

    int handSize = 5;
    if (game.getGameMode() == GameMode.SOLITAIRE || game.getGameMode() == GameMode.SOLITARY_PUZZLE) {
        handSize = 1;
    }
    gameSessionService.initializePlayerDeck(pgs, handSize);
    pgs.setHasRerolled(true);
    playerGameSessionRepository.save(pgs);
}

}

