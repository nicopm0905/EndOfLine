package es.us.dp1.lx_xy_24_25.your_game_name.cards.placedCard;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardTemplate;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardType;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Orientation;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.playerCard.PlayerCard;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.playerCard.PlayerCardRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.PlaceCardRequestDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.BadRequestException;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.PlayerGameSession;

@Service
public class PlacedCardService {

    private PlacedCardRepository placedCardRepository;
    private PlayerCardRepository playerCardRepository;

    public PlacedCardService(PlacedCardRepository placedCardRepository, PlayerCardRepository playerCardRepository) {
        this.placedCardRepository = placedCardRepository;
        this.playerCardRepository = playerCardRepository;
    }

    @Transactional
    public PlacedCard placeCard(GameSession gameSession, PlayerGameSession playerGameSession,
            PlaceCardRequestDTO request) {
        return placeCard(gameSession, playerGameSession, request, false);
    }

    @Transactional
    public PlacedCard placeCard(GameSession gameSession, PlayerGameSession playerGameSession,
            PlaceCardRequestDTO request, boolean allowJump) {
        PlayerCard playerCard = playerCardRepository.findById(request.getPlayerCardId())
                .orElseThrow(
                        () -> new IllegalArgumentException("PlayerCard not found or does not belong to the player"));

        if (!playerCard.getPlayer().equals(playerGameSession)) {
            throw new IllegalArgumentException("PlayerCard not found or does not belong to the player");
        }

        int normalizedRow = normalizeCoordinate(request.getRow(), gameSession.getBoardSize());
        int normalizedCol = normalizeCoordinate(request.getCol(), gameSession.getBoardSize());

        boolean cellOccupied = placedCardRepository.existsByRowAndColAndGameSessionId(normalizedRow, normalizedCol,
                gameSession.getId());
        if (cellOccupied) {
            throw new BadRequestException("This cell is already occupied");
        }

        if (!allowJump) {
            validateConnectionsWithOwnCards(gameSession, playerGameSession, normalizedRow, normalizedCol);

            PlaceCardRequestDTO normalizedRequest = new PlaceCardRequestDTO();
            normalizedRequest.setPlayerCardId(request.getPlayerCardId());
            normalizedRequest.setRow(normalizedRow);
            normalizedRequest.setCol(normalizedCol);
            normalizedRequest.setOrientation(request.getOrientation());

            if (!this.isValidConnection(gameSession, playerGameSession, playerCard.getTemplate(), normalizedRequest)) {
                throw new BadRequestException(
                        "The card does not connect correctly. You must connect your ENTRY with one of your EXITS.");
            }

            PlacedCard newPlacedCard = this.createAndSavePlacedCard(gameSession, playerGameSession,
                    playerCard.getTemplate(), normalizedRequest);
            if (playerCard.getPlayer().getCards() != null) {
                playerCard.getPlayer().getCards().remove(playerCard);
            }
            playerCardRepository.delete(playerCard);
            return newPlacedCard;
        }

        PlaceCardRequestDTO normalizedRequest = new PlaceCardRequestDTO();
        normalizedRequest.setPlayerCardId(request.getPlayerCardId());
        normalizedRequest.setRow(normalizedRow);
        normalizedRequest.setCol(normalizedCol);
        normalizedRequest.setOrientation(request.getOrientation());

        PlacedCard newPlacedCard = this.createAndSavePlacedCard(gameSession, playerGameSession,
                playerCard.getTemplate(), normalizedRequest);
        if (playerCard.getPlayer().getCards() != null) {
            playerCard.getPlayer().getCards().remove(playerCard);
        }
        playerCardRepository.delete(playerCard);
        return newPlacedCard;
    }

    @Transactional
    public PlacedCard save(PlacedCard placedCard) {
        return placedCardRepository.save(placedCard);
    }

    private PlacedCard createAndSavePlacedCard(GameSession gameSession, PlayerGameSession playerGameSession,
            CardTemplate template, PlaceCardRequestDTO request) {
        PlacedCard newPlacedCard = new PlacedCard();
        newPlacedCard.setGameSession(gameSession);
        newPlacedCard.setTemplate(template);
        newPlacedCard.setPlacedBy(playerGameSession);
        newPlacedCard.setRow(request.getRow());
        newPlacedCard.setCol(request.getCol());
        newPlacedCard.setOrientation(request.getOrientation());
        newPlacedCard.setPlacedAt(LocalDateTime.now());

        return placedCardRepository.save(newPlacedCard);
    }

    private void validateConnectionsWithOwnCards(GameSession gameSession, PlayerGameSession playerGameSession,
            Integer row, Integer col) {
        int boardSize = gameSession.getBoardSize();

        int[][] deltas = { { -1, 0 }, { 0, 1 }, { 1, 0 }, { 0, -1 } };
        Orientation[] directions = { Orientation.N, Orientation.E, Orientation.S, Orientation.W };

        boolean hasAdjacentOwnCardWithExit = false;

        for (int i = 0; i < deltas.length; i++) {
            int adjRow = normalizeCoordinate(row + deltas[i][0], boardSize);
            int adjCol = normalizeCoordinate(col + deltas[i][1], boardSize);

            Optional<PlacedCard> adjacentCard = placedCardRepository.findByRowAndColAndGameSessionId(
                    adjRow, adjCol, gameSession.getId());

            if (adjacentCard.isPresent()) {
                PlacedCard card = adjacentCard.get();

                if (!card.getPlacedBy().getId().equals(playerGameSession.getId())) {
                    continue;
                }

                Orientation neighborDir = directions[i];
                Orientation sideFacingMe = getOppositeOrientation(neighborDir);

                Integer connectorType = getConnector(card.getTemplate(), sideFacingMe, card.getOrientation());

                if (connectorType == 2) {
                    hasAdjacentOwnCardWithExit = true;
                }
            }
        }

        List<PlacedCard> playerCards = placedCardRepository.findByGameSessionIdAndPlacedByIdOrderByPlacedAtDesc(
                gameSession.getId(),
                playerGameSession.getId());

        long nonStartCards = playerCards.stream()
                .filter(c -> c.getTemplate().getType() != CardType.START)
                .count();

        if (nonStartCards > 0 && !hasAdjacentOwnCardWithExit) {
            throw new BadRequestException("You have to connect your card to an EXIT of your already placed cards");
        }
    }

    private boolean isValidConnection(GameSession gameSession, PlayerGameSession playerGameSession,
            CardTemplate newTemplate, PlaceCardRequestDTO request) {

        Orientation[] directions = { Orientation.N, Orientation.E, Orientation.S, Orientation.W };
        int[][] deltas = { { -1, 0 }, { 0, 1 }, { 1, 0 }, { 0, -1 } };

        if (placedCardRepository.findByGameSessionId(gameSession.getId()).isEmpty()) {
            return request.getRow() == gameSession.getBoardSize() / 2 &&
                    request.getCol() == gameSession.getBoardSize() / 2;
        }

        boolean foundValidConnection = false;

        for (int i = 0; i < directions.length; i++) {
            int[] neighborCoords = getNeighborCoordinates(
                    request.getRow(),
                    request.getCol(),
                    deltas[i][0],
                    deltas[i][1],
                    gameSession.getBoardSize());

            int neighborRow = neighborCoords[0];
            int neighborCol = neighborCoords[1];
            Orientation neighborDirection = directions[i];

            Optional<PlacedCard> neighborCardOpt = placedCardRepository.findByRowAndColAndGameSessionId(
                    neighborRow, neighborCol, gameSession.getId());

            if (neighborCardOpt.isPresent()) {
                PlacedCard neighborCard = neighborCardOpt.get();

                if (!neighborCard.getPlacedBy().getId().equals(playerGameSession.getId())) {
                    continue;
                }

                Integer newCardConnector = getConnector(newTemplate, neighborDirection, request.getOrientation());
                Orientation oppositeDirection = getOppositeOrientation(neighborDirection);
                Integer neighborCardConnector = getConnector(neighborCard.getTemplate(), oppositeDirection,
                        neighborCard.getOrientation());

                if (newCardConnector == 1 && neighborCardConnector == 2) {
                    foundValidConnection = true;
                    return true;
                }
            }
        }
        return foundValidConnection;
    }


    private Orientation getRelativeSide(Orientation requiredSide, Orientation cardRotation) {
        switch (cardRotation) {
            case N:
                return requiredSide;
            case E:
                switch (requiredSide) {
                    case N:
                        return Orientation.W;
                    case E:
                        return Orientation.N;
                    case S:
                        return Orientation.E;
                    case W:
                        return Orientation.S;
                }
            case S:
                switch (requiredSide) {
                    case N:
                        return Orientation.S;
                    case E:
                        return Orientation.W;
                    case S:
                        return Orientation.N;
                    case W:
                        return Orientation.E;
                }
            case W:
                switch (requiredSide) {
                    case N:
                        return Orientation.E;
                    case E:
                        return Orientation.S;
                    case S:
                        return Orientation.W;
                    case W:
                        return Orientation.N;
                }
            default:
                throw new BadRequestException("Invalid card rotation value.");
        }
    }

    private Integer getConnector(CardTemplate template, Orientation requiredSide, Orientation cardRotation) {
        Orientation relativeSide = getRelativeSide(requiredSide, cardRotation);
        if (relativeSide.equals(template.getDefaultEntrance()))
            return 1;
        else if (template.getDefaultExits().contains(relativeSide))
            return 2;
        else
            return 0;
    }

    private Orientation getOppositeOrientation(Orientation orientation) {
        switch (orientation) {
            case N:
                return Orientation.S;
            case S:
                return Orientation.N;
            case E:
                return Orientation.W;
            case W:
                return Orientation.E;
            default:
                throw new BadRequestException("Invalid orientation.");
        }
    }

    private int normalizeCoordinate(int coordinate, int boardSize) {
        return ((coordinate % boardSize) + boardSize) % boardSize;
    }

    private int[] getNeighborCoordinates(int row, int col, int deltaRow, int deltaCol, int boardSize) {
        int neighborRow = normalizeCoordinate(row + deltaRow, boardSize);
        int neighborCol = normalizeCoordinate(col + deltaCol, boardSize);
        return new int[] { neighborRow, neighborCol };
    }
}
