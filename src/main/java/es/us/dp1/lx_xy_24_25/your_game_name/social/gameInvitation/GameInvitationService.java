package es.us.dp1.lx_xy_24_25.your_game_name.social.gameInvitation;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.dto.JoinRequestDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.BadRequestException;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gameSession.playerGameSession.JoinGameService;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.social.friendship.FriendshipService;

@Service
public class GameInvitationService {
    private final GameInvitationRepository gameInvitationRepository;
    private final FriendshipService friendshipService;
    private final JoinGameService joinGameService;

    @Autowired
    public GameInvitationService(GameInvitationRepository gameInvitationRepository,
            FriendshipService friendshipService,
            JoinGameService joinGameService) {
        this.gameInvitationRepository = gameInvitationRepository;
        this.friendshipService = friendshipService;
        this.joinGameService = joinGameService;
    }

    @Transactional
    public GameInvitation createInvitation(Player sender, Player receiver, GameSession game,
            InvitationType type) {
        if (!friendshipService.areFriends(sender.getId(), receiver.getId())) {
            throw new BadRequestException("You can only invite friends");
        }

        var existing = gameInvitationRepository.findBySenderAndReceiverAndGame(sender, receiver, game);
        if (existing.isPresent() && existing.get().getStatus() == InvitationStatus.PENDING) {
            throw new BadRequestException("There is already a pending invitation for this game between these players");
        }

        if (sender.getId().equals(receiver.getId())) {
            throw new BadRequestException("You cannot invite yourself to a game");
        }

        if (game.getStartTime() != null) {
            throw new BadRequestException("The game has already started");
        }

        GameInvitation invitation = new GameInvitation(sender, receiver, game, type);
        return gameInvitationRepository.save(invitation);
    }

    @Transactional
    public GameInvitation acceptInvitation(Integer invitationId) {
        GameInvitation invitation = gameInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation not found"));

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new BadRequestException("This invitation has already been responded to.");
        }

        if (invitation.getGame().getStartTime() != null) {
            invitation.setStatus(InvitationStatus.EXPIRED);
            gameInvitationRepository.save(invitation);
            throw new BadRequestException("The game has already started. Invitation expired.");
        }

        invitation.setStatus(InvitationStatus.ACCEPTED);
        invitation.setRespondedAt(LocalDateTime.now());

        
        Integer gameId = invitation.getGame().getId();
        String username = invitation.getReceiver().getUsername();
        
        if (invitation.getInvitationType() == InvitationType.PLAYER) {
            long currentActivePlayers = invitation.getGame().getPlayers().stream().count();

            if (currentActivePlayers >= invitation.getGame().getNumPlayers()) {
                throw new BadRequestException("The game is already full.");
            }
            JoinRequestDTO request = new JoinRequestDTO();  
            if (invitation.getGame().isPrivate()) {
                request.setPassword(invitation.getGame().getPassword());
            }
            joinGameService.joinPlayerToGame(gameId, username, request);
        }

        return gameInvitationRepository.save(invitation);
    }

    @Transactional
    public GameInvitation rejectInvitation(Integer invitationId) {
        GameInvitation invitation = gameInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation not found"));

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new BadRequestException("This invitation has already been responded to.");
        }

        invitation.setStatus(InvitationStatus.REJECTED);
        invitation.setRespondedAt(LocalDateTime.now());

        return gameInvitationRepository.save(invitation);
    }

    @Transactional
    public GameInvitation cancelInvitation(Integer invitationId) {
        GameInvitation invitation = gameInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation not found"));

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new BadRequestException("You can only cancel pending invitations.");
        }

        invitation.setStatus(InvitationStatus.CANCELED);
        invitation.setRespondedAt(LocalDateTime.now());

        return gameInvitationRepository.save(invitation);
    }

    @Transactional(readOnly = true)
    public List<GameInvitation> getReceivedInvitations(Player receiver) {
        return gameInvitationRepository.findByReceiver(receiver);
    }

    @Transactional(readOnly = true)
    public List<GameInvitation> getPendingReceivedInvitations(Player receiver) {
        return gameInvitationRepository.findByReceiverAndStatus(receiver, InvitationStatus.PENDING);
    }

    @Transactional(readOnly = true)
    public List<GameInvitation> getSentInvitations(Player sender) {
        return gameInvitationRepository.findBySender(sender);
    }

    @Transactional(readOnly = true)
    public long getPendingInvitationCount(Player receiver) {
        return gameInvitationRepository.countByReceiverAndStatus(receiver, InvitationStatus.PENDING);
    }

    @Transactional(readOnly = true)
    public GameInvitation getInvitation(Integer invitationId) {
        return gameInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation not found"));
    }
}
