package es.us.dp1.lx_xy_24_25.your_game_name.puzzle;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardTemplate;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardTemplateRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardType;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Color;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Orientation;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.placedCard.PlacedCard;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.placedCard.PlacedCardRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameMode;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSessionService;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameState;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.PlayerGameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.PlayerGameSessionService;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PuzzleFactoryService {
    private final Map<Integer, PuzzleDefinition> puzzles = new HashMap<>();

    private final GameSessionService gameService;
    private final PlacedCardRepository placedCardRepository;
    private final CardTemplateRepository cardTemplateRepository;
    private final PlayerGameSessionService playerGameSessionService;

    @PostConstruct
    public void initPuzzles() {
        
        List<PuzzleCardConfiguration> p1Obstacles = new ArrayList<>();
        p1Obstacles.add(new PuzzleCardConfiguration(2, 1));
        p1Obstacles.add(new PuzzleCardConfiguration(2, 3));
        puzzles.put(1, new PuzzleDefinition(1, "First Steps", p1Obstacles));

        
        List<PuzzleCardConfiguration> p2Obstacles = new ArrayList<>();
        p2Obstacles.add(new PuzzleCardConfiguration(1, 3));
        p2Obstacles.add(new PuzzleCardConfiguration(3, 1));
        puzzles.put(2, new PuzzleDefinition(2, "Cross Path", p2Obstacles));

        
        List<PuzzleCardConfiguration> p3Obstacles = new ArrayList<>();
        p3Obstacles.add(new PuzzleCardConfiguration(2, 3));
        p3Obstacles.add(new PuzzleCardConfiguration(2, 1));
        puzzles.put(3, new PuzzleDefinition(3, "Push Forward", p3Obstacles));

        
        List<PuzzleCardConfiguration> p4Obstacles = new ArrayList<>();
        p4Obstacles.add(new PuzzleCardConfiguration(3, 2));
        p4Obstacles.add(new PuzzleCardConfiguration(4, 2));
        puzzles.put(4, new PuzzleDefinition(4, "Straight Line", p4Obstacles));

        
        List<PuzzleCardConfiguration> p5Obstacles = new ArrayList<>();
        p5Obstacles.add(new PuzzleCardConfiguration(0, 2));
        p5Obstacles.add(new PuzzleCardConfiguration(1, 3));
        puzzles.put(5, new PuzzleDefinition(5, "Trapped", p5Obstacles));

        
        List<PuzzleCardConfiguration> p6Obstacles = new ArrayList<>();
        p6Obstacles.add(new PuzzleCardConfiguration(3, 3));
        p6Obstacles.add(new PuzzleCardConfiguration(4, 3));
        puzzles.put(6, new PuzzleDefinition(6, "Right Direction", p6Obstacles));

        
        List<PuzzleCardConfiguration> p7Obstacles = new ArrayList<>();
        p7Obstacles.add(new PuzzleCardConfiguration(1, 0));
        p7Obstacles.add(new PuzzleCardConfiguration(1, 1));
        puzzles.put(7, new PuzzleDefinition(7, "Ladder Climb", p7Obstacles));

        
        List<PuzzleCardConfiguration> p8Obstacles = new ArrayList<>();
        p8Obstacles.add(new PuzzleCardConfiguration(2, 0));
        p8Obstacles.add(new PuzzleCardConfiguration(3, 1));
        puzzles.put(8, new PuzzleDefinition(8, "Left Turn", p8Obstacles));

       
        List<PuzzleCardConfiguration> p9Obstacles = new ArrayList<>();
        p9Obstacles.add(new PuzzleCardConfiguration(2, 0));
        p9Obstacles.add(new PuzzleCardConfiguration(2, 1));
        puzzles.put(9, new PuzzleDefinition(9, "Balance", p9Obstacles));

        
        List<PuzzleCardConfiguration> p10Obstacles = new ArrayList<>();
        p10Obstacles.add(new PuzzleCardConfiguration(1, 1));
        p10Obstacles.add(new PuzzleCardConfiguration(0, 4));
        p10Obstacles.add(new PuzzleCardConfiguration(1, 3));
        puzzles.put(10, new PuzzleDefinition(10, "Zigzag", p10Obstacles));

        
        List<PuzzleCardConfiguration> p11Obstacles = new ArrayList<>();
        p11Obstacles.add(new PuzzleCardConfiguration(1, 0));
        p11Obstacles.add(new PuzzleCardConfiguration(1, 3));
        p11Obstacles.add(new PuzzleCardConfiguration(2, 1));
        puzzles.put(11, new PuzzleDefinition(11, "Snake", p11Obstacles));

        
        List<PuzzleCardConfiguration> p12Obstacles = new ArrayList<>();
        p12Obstacles.add(new PuzzleCardConfiguration(2, 3));
        p12Obstacles.add(new PuzzleCardConfiguration(2, 4));
        p12Obstacles.add(new PuzzleCardConfiguration(3, 2));
        puzzles.put(12, new PuzzleDefinition(12, "Inferno", p12Obstacles));
    }

    public PuzzleDefinition getPuzzleInfo(Integer id) {
        return puzzles.get(id);
    }
    
    public List<PuzzleDefinition> getAllPuzzles() {
        return new ArrayList<>(puzzles.values());
    }

    @Transactional
    public GameSession createPuzzleGame(Integer puzzleId, Player player) {
        
        PuzzleDefinition def = puzzles.get(puzzleId);
        if (def == null) throw new ResourceNotFoundException("Puzzle not found");

        GameSession game = new GameSession();
        game.setName(def.getName());
        game.setHost(player.getUsername());
        game.setGameMode(GameMode.SOLITARY_PUZZLE);
        game.setNumPlayers(1);
        game.setBoardSize(5);
        game.setPrivate(true);
        game.setState(GameState.ACTIVE);
        game.setStartTime(LocalDateTime.now());

        game = gameService.save(game);
        PlayerGameSession pgs;
        
        if (game.getPlayers() != null && !game.getPlayers().isEmpty()) {
            pgs = game.getPlayers().stream().findFirst().get(); 
        } else {
            pgs = new PlayerGameSession();
            pgs.setPlayer(player);
            pgs.setGameSession(game);
        }
        
        if(puzzleId <= 3) pgs.setPlayerColor(Color.ORANGE);
        else if(puzzleId <= 6) pgs.setPlayerColor(Color.GREEN);
        else if(puzzleId <= 9) pgs.setPlayerColor(Color.MAGENTA);
        else pgs.setPlayerColor(Color.VIOLET);

        pgs = playerGameSessionService.save(pgs);
        pgs.setTurnOrder(1);
        game.setGamePlayerTurnId(pgs.getId());

        gameService.initializePlayerDeck(pgs, 5);

        List<PlacedCard> cardsToSave = new ArrayList<>();

        CardTemplate startTemplate = cardTemplateRepository.findByType(CardType.START)
                .orElseThrow(() -> new RuntimeException("No START card found"));

        PlacedCard startCard = new PlacedCard();
        startCard.setGameSession(game);
        startCard.setPlacedBy(pgs);
        startCard.setTemplate(startTemplate);
        startCard.setRow(2); 
        startCard.setCol(2); 
        startCard.setOrientation(Orientation.N); 
        cardsToSave.add(startCard);

        CardTemplate obstacleTemplate = cardTemplateRepository.findByType(CardType.BACK) 
                .orElseThrow(() -> new RuntimeException("No obstacle card found"));

        for (PuzzleCardConfiguration obstacle : def.getObstacles()) {
            PlacedCard pc = new PlacedCard();
            pc.setGameSession(game);
            pc.setPlacedBy(pgs);
            pc.setTemplate(obstacleTemplate);
            pc.setRow(obstacle.getRow());
            pc.setCol(obstacle.getCol());
            pc.setOrientation(Orientation.N);
            cardsToSave.add(pc);
        }
        
        placedCardRepository.saveAll(cardsToSave);

        return gameService.getGameById(game.getId());
    }

    @Transactional
    public GameSession createSolitaireGame(Integer solitaireId, Player player) {
        
        PuzzleDefinition def = puzzles.get(solitaireId);
        if (def == null) throw new ResourceNotFoundException("Puzzle not found");

        GameSession game = new GameSession();
        game.setName(def.getName());
        game.setHost(player.getUsername());
        game.setGameMode(GameMode.SOLITAIRE);
        game.setNumPlayers(1);
        game.setBoardSize(5);
        game.setPrivate(true);
        game.setState(GameState.ACTIVE);
        game.setStartTime(LocalDateTime.now());

        game = gameService.save(game);
        PlayerGameSession pgs;
        
        if (game.getPlayers() != null && !game.getPlayers().isEmpty()) {
            pgs = game.getPlayers().stream().findFirst().get(); 
        } else {
            pgs = new PlayerGameSession();
            pgs.setPlayer(player);
            pgs.setGameSession(game);
        }
        
        if(solitaireId <= 3) pgs.setPlayerColor(Color.ORANGE);
        else if(solitaireId <= 6) pgs.setPlayerColor(Color.GREEN);
        else if(solitaireId <= 9) pgs.setPlayerColor(Color.MAGENTA);
        else pgs.setPlayerColor(Color.VIOLET);

        pgs = playerGameSessionService.save(pgs);         
        pgs.setTurnOrder(1);
        game.setGamePlayerTurnId(pgs.getId());

        gameService.initializePlayerDeck(pgs, 1);

        List<PlacedCard> cardsToSave = new ArrayList<>();

        CardTemplate startTemplate = cardTemplateRepository.findByType(CardType.START)
                .orElseThrow(() -> new RuntimeException("No START card found"));

        PlacedCard startCard = new PlacedCard();
        startCard.setGameSession(game);
        startCard.setPlacedBy(pgs);
        startCard.setTemplate(startTemplate);
        startCard.setRow(2); 
        startCard.setCol(2); 
        startCard.setOrientation(Orientation.N); 
        cardsToSave.add(startCard);

        CardTemplate obstacleTemplate = cardTemplateRepository.findByType(CardType.BACK) 
                .orElseThrow(() -> new RuntimeException("No obstacle card found"));

        for (PuzzleCardConfiguration obstacle : def.getObstacles()) {
            PlacedCard pc = new PlacedCard();
            pc.setGameSession(game);
            pc.setPlacedBy(pgs);
            pc.setTemplate(obstacleTemplate);
            pc.setRow(obstacle.getRow());
            pc.setCol(obstacle.getCol());
            pc.setOrientation(Orientation.N);
            cardsToSave.add(pc);
        }
        
        placedCardRepository.saveAll(cardsToSave);

        return gameService.getGameById(game.getId());
    }
}