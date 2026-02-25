package es.us.dp1.lx_xy_24_25.your_game_name.social.chatMessage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.dto.ChatMessageDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.ChatRequestDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.BadRequestException;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSessionService;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.PlayerGameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerService;

@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private GameSessionService gameSessionService;
    private PlayerService playerService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public ChatMessageService(ChatMessageRepository chatMessageRepository, GameSessionService gameSessionService,
            PlayerService playerService, SimpMessagingTemplate messagingTemplate) {
        this.chatMessageRepository = chatMessageRepository;
        this.gameSessionService = gameSessionService;
        this.playerService = playerService;
        this.messagingTemplate = messagingTemplate;
    }

   @Transactional
    public ChatMessageDTO createAndBroadcast(ChatRequestDTO chatRequest, Integer gameId, String username) {

        validateMessage(chatRequest);

        GameSession game = gameSessionService.getGameById(gameId);
        if (game == null) {
            throw new ResourceNotFoundException("GameSession", "id", gameId);
        }

        Player player = playerService.findPlayer(username);

        PlayerGameSession pgs = game.getPlayers().stream()
                .filter(p -> p.getPlayer().getId().equals(player.getId()))
                .findFirst()
                .orElseThrow(() ->
                        new BadRequestException("The player does not belong to the game"));

        ChatMessage message = new ChatMessage();
        message.setMessage(chatRequest.getMessage());
        message.setColor(pgs.getPlayerColor());
        message.setPlayerGameSession(pgs);

        ChatMessage saved = chatMessageRepository.save(message);
        ChatMessageDTO dto = new ChatMessageDTO(saved);

        messagingTemplate.convertAndSend("/topic/chat/" + gameId, dto);

        return dto;
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDTO> findAllMessageDTOsByGameId(Integer gameId) {
        return chatMessageRepository.findAllMessagesByGameId(gameId)
                .stream()
                .map(ChatMessageDTO::new)
                .toList();
    }

    private void validateMessage(ChatRequestDTO chatRequest) {
        if (chatRequest.getMessage() == null || chatRequest.getMessage().trim().isEmpty()) {
            throw new BadRequestException("Message cannot be empty");
        }
        if (chatRequest.getMessage().length() > 255) {
            throw new BadRequestException("Message is too long (max 255 characters)");
        }
    }

}
