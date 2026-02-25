package es.us.dp1.lx_xy_24_25.your_game_name.gameSession;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardTemplate;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.EnergyActionRequestDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.GameSessionDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.SpectatorGameDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.GameStartDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.JoinRequestDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.PlayerGameSessionDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.JoinGameService;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.LeaveGameService;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.PlayerGameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.PlayerGameSessionService;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerService;
import es.us.dp1.lx_xy_24_25.your_game_name.puzzle.PuzzleDefinition;
import es.us.dp1.lx_xy_24_25.your_game_name.puzzle.PuzzleFactoryService;
import es.us.dp1.lx_xy_24_25.your_game_name.social.friendship.FriendshipService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;
import es.us.dp1.lx_xy_24_25.your_game_name.util.RestPreconditions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/gameList")
@Tag(name = "Game Sessions", description = "API to view active and finished games")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST })
public class GameSessionRestController {

    private static final Logger logger = LoggerFactory.getLogger(GameSessionRestController.class);

    private final GameSessionService gameService;
    private final PlayerGameSessionService playerGameSessionService;
    private final PlayerService playerService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;
    private final PuzzleFactoryService puzzleFactoryService;
    private final FriendshipService friendshipService;
    private final JoinGameService joinGameService;
    private final LeaveGameService leaveGameService;

    @Autowired
    public GameSessionRestController(GameSessionService gameService, PlayerGameSessionService playerGameSessionService,
            PlayerService playerService, UserService userService, SimpMessagingTemplate messagingTemplate,
            PuzzleFactoryService puzzleFactoryService, FriendshipService friendshipService,
            JoinGameService joinGameService, LeaveGameService leaveGameService) {
        this.gameService = gameService;
        this.playerGameSessionService = playerGameSessionService;
        this.playerService = playerService;
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
        this.puzzleFactoryService = puzzleFactoryService;
        this.friendshipService = friendshipService;
        this.joinGameService = joinGameService;
        this.leaveGameService = leaveGameService;
    }

    private void notifyLobbyUpdate(Integer gameId) {
        GameSession updated = gameService.getGameById(gameId);
        GameSessionDTO dto = new GameSessionDTO(updated);
        messagingTemplate.convertAndSend("/topic/lobby/" + gameId, dto);
    }

    public void notifyLobbyDeleted(Integer gameId) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("deleted", true);
        payload.put("gameId", gameId);
        messagingTemplate.convertAndSend("/topic/lobby/" + gameId, payload);
    }

    @GetMapping("/active")
    @Operation(summary = "Get active games", description = "Retrieves all games that are currently in progress.")
    @ApiResponse(responseCode = "200", description = "List of active games")
    public ResponseEntity<List<GameSessionDTO>> getActiveGames() {
        List<GameSession> games = gameService.getActiveGames();
        List<GameSessionDTO> gamesDTO = games.stream().map(GameSessionDTO::new).collect(Collectors.toList());
        return new ResponseEntity<>(gamesDTO, HttpStatus.OK);
    }

    @GetMapping("/finished")
    @Operation(summary = "Get finished games", description = "Retrieves all games that have completed.")
    @ApiResponse(responseCode = "200", description = "List of finished games")
    public ResponseEntity<List<GameSessionDTO>> getFinishedGames() {
        List<GameSession> games = gameService.getFinishedGames();
        List<GameSessionDTO> gamesDTO = games.stream().map(GameSessionDTO::new).collect(Collectors.toList());
        return new ResponseEntity<>(gamesDTO, HttpStatus.OK);
    }

    @GetMapping("/pending")
    @Operation(summary = "Get pending games", description = "Retrieves all games that are waiting for players to join.")
    @ApiResponse(responseCode = "200", description = "List of pending games")
    public ResponseEntity<List<GameSessionDTO>> getPendingGames() {
        List<GameSession> games = gameService.getPendingGames();
        List<GameSessionDTO> gamesDTO = games.stream().map(GameSessionDTO::new).collect(Collectors.toList());
        return new ResponseEntity<>(gamesDTO, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get game by ID", description = "Retrieves a specific game session by its unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Game found", content = @Content(schema = @Schema(implementation = GameSessionDTO.class))),
            @ApiResponse(responseCode = "404", description = "Game not found")
    })
    public ResponseEntity<GameSessionDTO> getGameById(
            @PathVariable("id") @Parameter(description = "ID of the game session") Integer id) {
        GameSession game = gameService.getGameById(id);
        if (game == null) {
            throw new ResourceNotFoundException("GameSession with id " + id + " not found!");
        }
        GameSessionDTO gameDTO = new GameSessionDTO(game);
        return new ResponseEntity<GameSessionDTO>(gameDTO, HttpStatus.OK);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new game", description = "Creates a new game session.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Game created successfully", content = @Content(schema = @Schema(implementation = GameSessionDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid game parameters")
    })
    public ResponseEntity<?> createGameSession(@Valid @RequestBody GameSession session) {

        GameSession created = gameService.save(session);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/api/v1/gameList/" + created.getId());

        GameSession createdWithPlayers = gameService.getGameById(created.getId());
        GameSessionDTO gameDTO = new GameSessionDTO(createdWithPlayers);

        return new ResponseEntity<>(gameDTO, headers, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update a game", description = "Updates an existing game session configuration.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Game updated successfully"),
            @ApiResponse(responseCode = "404", description = "Game not found")
    })
    public ResponseEntity<GameSessionDTO> editGameSession(
            @PathVariable("id") @Parameter(description = "ID of the game session to update") int id,
            @RequestBody @Valid GameSession game) {
        RestPreconditions.checkNotNull(gameService.getGameById(id), "GameSession", "Id", id);
        game.setBoardSize(gameService.calculateBoardSize(game.getGameMode(), game.getNumPlayers()));
        GameSession updatedGame = gameService.update(game, id);
        GameSession updatedWithPlayers = gameService.getGameById(updatedGame.getId());
        GameSessionDTO gameDTO = new GameSessionDTO(updatedWithPlayers);
        this.messagingTemplate.convertAndSend("/topic/game/" + id, gameDTO);
        return new ResponseEntity<GameSessionDTO>(gameDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a game", description = "Deletes a game session.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Game deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Game not found")
    })
    public ResponseEntity<Void> deleteGameSession(@PathVariable("id") @Parameter(description = "ID of the game session to delete") int id) {
         RestPreconditions.checkNotNull(gameService.getGameById(id), "GameSession", "Id", id);
        notifyLobbyDeleted(id);
        gameService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<?> joinGame(
            @PathVariable @Parameter(description = "ID of the game session to join") Integer id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody(required = false) JoinRequestDTO joinRequest) {
        return joinGameService.joinPlayerToGame(id, userDetails.getUsername(), joinRequest);
    }

    @PostMapping("/{id}/leave")
    public ResponseEntity<?> leave(@PathVariable @Parameter(description = "ID of the game session to leave") Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {

        return leaveGameService.leaveGame(id, userDetails.getUsername());
    }

    @PostMapping("/{id}/switchTeam")
    public ResponseEntity<?> switchTeam(@PathVariable @Parameter(description = "ID of the game session") Integer id,
            @RequestBody Map<String, Integer> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Integer teamNumber = body.get("teamNumber");
            playerGameSessionService.switchTeam(id, userDetails.getUsername(), teamNumber);
            notifyLobbyUpdate(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<GameStartDTO> startGame(
            @PathVariable @Parameter(description = "ID of the game session to start") Integer id,
            @RequestBody(required = false) GameSession gamesession) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.findByUsername(username);

        GameStartDTO dto = gameService.startGame(id, currentUser, gamesession);
        notifyLobbyUpdate(id);

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{gameId}/finish")
    @PreAuthorize("hasAuthority('PLAYER')")
    public ResponseEntity<GameSession> finishGame(
            @PathVariable @Parameter(description = "ID of the game session to finish") Integer gameId,
            @RequestParam @Parameter(description = "Username of the winning player") String winner,
            @AuthenticationPrincipal UserDetails userDetails) {

        GameSession game = gameService.getGameById(gameId);

        if (!game.getHost().equals(userDetails.getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only host can finish the game");
        }

        GameSession finishedGame = gameService.finishGame(gameId, winner);
        return ResponseEntity.ok(finishedGame);
    }

    @PostMapping("/{id}/energy")
    public ResponseEntity<PlayerGameSessionDTO> consumeEnergy(
            @PathVariable("id") @Parameter(description = "ID of the game session") Integer id,
            @Valid @RequestBody EnergyActionRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {

        PlayerGameSession updated = playerGameSessionService.consumeEnergy(
                id,
                userDetails.getUsername(),
                request.getActionId());

        GameSession updatedGame = gameService.getGameById(id);
        SpectatorGameDTO spectatorDTO = new SpectatorGameDTO(updatedGame);
        messagingTemplate.convertAndSend("/topic/spectate/" + id, spectatorDTO);

        return ResponseEntity.ok(new PlayerGameSessionDTO(updated));
    }

    @PostMapping("/{id}/discard")
    public ResponseEntity<?> discardCard(
            @PathVariable("id") @Parameter(description = "ID of the game session") Integer gameId,
            @RequestBody Map<String, Integer> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Integer cardId = body.get("cardId");
            playerGameSessionService.discardActiveCard(gameId, userDetails.getUsername(), cardId);
            GameSession updatedGame = gameService.getGameById(gameId);
            Player player = playerService.findPlayer(userDetails.getUsername());
            GameStartDTO gameDTO = new GameStartDTO(updatedGame, player);

            String destination = "/topic/game/" + gameId;
            this.messagingTemplate.convertAndSend(destination, gameDTO);
            logger.debug("Game state sent via WebSocket to: {}", destination);

            SpectatorGameDTO spectatorDTO = new SpectatorGameDTO(updatedGame);
            messagingTemplate.convertAndSend("/topic/spectate/" + gameId, spectatorDTO);

            return ResponseEntity.ok(gameDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/drawdeck")
    public ResponseEntity<?> drawFromDeck(
            @PathVariable("id") @Parameter(description = "ID of the game session") Integer gameId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Player player = playerService.findPlayer(userDetails.getUsername());
            playerGameSessionService.drawCardFromDeckAction(gameId, userDetails.getUsername());
            GameSession updatedGame = gameService.getGameById(gameId);
            GameStartDTO gameDTO = new GameStartDTO(updatedGame, player);
            this.messagingTemplate.convertAndSend("/topic/game/" + gameId, gameDTO);

            SpectatorGameDTO spectatorDTO = new SpectatorGameDTO(updatedGame);
            messagingTemplate.convertAndSend("/topic/spectate/" + gameId, spectatorDTO);

            return ResponseEntity.ok(gameDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/draw-discard")
    public ResponseEntity<?> drawFromDiscard(
            @PathVariable("id") @Parameter(description = "ID of the game session") Integer gameId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            playerGameSessionService.drawCardFromDiscardAction(gameId, userDetails.getUsername());
            GameSession updatedGame = gameService.getGameById(gameId);
            GameSessionDTO gameDTO = new GameSessionDTO(updatedGame);
            return ResponseEntity.ok(gameDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{gameSessionId}/player/{username}/initiative")
    @Operation(summary = "Get random card for initiative")
    public ResponseEntity<CardTemplate> getInitiativeCard(
            @PathVariable("gameSessionId") @Parameter(description = "ID of the game session") Integer gameSessionId,
            @PathVariable("username") @Parameter(description = "Username of the player") String username) {

        CardTemplate card = playerGameSessionService.drawFirstCardForInitiative(gameSessionId, username);
        return new ResponseEntity<>(card, HttpStatus.OK);
    }

    @PostMapping("/puzzle/{puzzleId}")
    public ResponseEntity<GameStartDTO> startPuzzle(
            @PathVariable @Parameter(description = "ID of the puzzle to start") Integer puzzleId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Player player = playerService.findPlayer(userDetails.getUsername());
        GameSession puzzleGame = puzzleFactoryService.createPuzzleGame(puzzleId, player);
        return new ResponseEntity<>(new GameStartDTO(puzzleGame, player), HttpStatus.CREATED);
    }

    @PostMapping("/solitaire/{solitaireId}")
    public ResponseEntity<GameStartDTO> startSolitaire(
            @PathVariable @Parameter(description = "ID of the solitaire game to start") Integer solitaireId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Player player = playerService.findPlayer(userDetails.getUsername());
        GameSession solitaireGame = puzzleFactoryService.createSolitaireGame(solitaireId, player);
        return new ResponseEntity<>(new GameStartDTO(solitaireGame, player), HttpStatus.CREATED);
    }

    @GetMapping("/puzzle")
    public ResponseEntity<List<PuzzleDefinition>> getPuzzleList() {
        return ResponseEntity.ok(puzzleFactoryService.getAllPuzzles());
    }

    @GetMapping("/{id}/spectate")
    public ResponseEntity<SpectatorGameDTO> spectate(
            @PathVariable("id") @Parameter(description = "ID of the game session to spectate") Integer id) {
        return ResponseEntity.ok(gameService.getSpectatorView(id));
    }

    @GetMapping("/active/friends")
    public ResponseEntity<List<GameSessionDTO>> getActiveGamesWithFriends(
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        Set<String> friends = new HashSet<>(friendshipService.getConfirmedFriendUsernames(username));
        List<GameSessionDTO> gamesDTO = gameService.getActiveGamesByFriends(friends);
        return ResponseEntity.ok(gamesDTO);
    }
}
