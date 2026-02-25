package es.us.dp1.lx_xy_24_25.your_game_name.social.friendship;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.configuration.WebSocketEventListener;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.FriendshipDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.FriendshipRequestDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.BadRequestException;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.playerStatistics.StatisticsUpdateService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.dao.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class FriendshipService {

    private static final Logger logger = LoggerFactory.getLogger(FriendshipService.class);

    FriendshipRepository friendshipRepository;
    PlayerRepository playerRepository;
    ObjectProvider<WebSocketEventListener> webSocketEventListenerProvider;
    StatisticsUpdateService statisticsUpdateService;

    @Autowired
    public FriendshipService(FriendshipRepository friendshipRepository, PlayerRepository playerRepository,
            ObjectProvider<WebSocketEventListener> webSocketEventListenerProvider,
            StatisticsUpdateService statisticsUpdateService) {
        this.friendshipRepository = friendshipRepository;
        this.playerRepository = playerRepository;
        this.webSocketEventListenerProvider = webSocketEventListenerProvider;
        this.statisticsUpdateService = statisticsUpdateService;
    }

    @Transactional(readOnly = true)
    public Friendship findById(Integer id) throws DataAccessException {
        return friendshipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Friendship", "id", id));
    }

    @Transactional(readOnly = true)
    public List<Friendship> findAll() throws DataAccessException {
        Iterable<Friendship> friends = friendshipRepository.findAll();
        return StreamSupport.stream(friends.spliterator(), false).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FriendshipDTO> findAllDtosWithStatus() throws DataAccessException {
        List<FriendshipDTO> dtos = findAll().stream().map(FriendshipDTO::new).collect(Collectors.toList());
        WebSocketEventListener webSocketEventListener = webSocketEventListenerProvider.getIfAvailable();
        for (FriendshipDTO f : dtos) {
            if (f.getSender() != null) {
                f.getSender().setStatus(webSocketEventListener != null
                        ? webSocketEventListener.getStatus(f.getSender().getUsername())
                        : UserPresenceStatus.OFFLINE);
            }
            if (f.getReceiver() != null) {
                f.getReceiver().setStatus(webSocketEventListener != null
                        ? webSocketEventListener.getStatus(f.getReceiver().getUsername())
                        : UserPresenceStatus.OFFLINE);
            }
        }
        return dtos;
    }

    @Transactional(readOnly = true)
    public Iterable<Friendship> findAllFriendshipsByPlayerId(Integer id, FriendshipState friendState)
            throws DataAccessException {
        Iterable<Friendship> friendships = friendshipRepository.findAllFriendshipsByPlayerId(id);
        return StreamSupport.stream(friendships.spliterator(), false)
                .filter(friendship -> friendship.getState().equals(friendState))
                .collect(Collectors.toList());
    }

    private Boolean checkFriendship(Integer sender_id, Integer receiver_id) throws DataAccessException {
        if (sender_id.equals(receiver_id))
            throw new BadRequestException("You cannot create a friendship with yourself.");

        if (!playerRepository.existsPlayerById(sender_id) || !playerRepository.existsPlayerById(receiver_id))
            throw new BadRequestException("Player with id " + sender_id + " or " + receiver_id + " does not exist.");

        Optional<Friendship> optionalFriendship = friendshipRepository.findFriendshipBySenderAndReceiver(sender_id,
                receiver_id);
        if (optionalFriendship.isPresent()) {
            FriendshipState friendStatus = optionalFriendship.get().getState();
            switch (friendStatus) {
                case PENDING:
                    throw new BadRequestException("There is already a pending friendship request with this player.");
                default:
                    throw new BadRequestException("You are already friends with this player.");
            }
        }
        return true;
    }

    @Transactional(readOnly = true)
    public FriendshipDTO findDtoById(Integer id) throws DataAccessException {
        Friendship friendship = findById(id);
        return new FriendshipDTO(friendship);
    }

    @Transactional
    public Friendship create(FriendshipRequestDTO requestDTO) throws DataAccessException {
        checkFriendship(requestDTO.getSender(), requestDTO.getReceiver());
        Friendship f = new Friendship();
        Player sender = playerRepository.findById(requestDTO.getSender())
                .orElseThrow(() -> new BadRequestException(
                        "Sender Player with id " + requestDTO.getSender() + " does not exist."));
        Player receiver = playerRepository.findById(requestDTO.getReceiver())
                .orElseThrow(() -> new BadRequestException(
                        "Receiver Player with id " + requestDTO.getReceiver() + " does not exist."));
        f.setSender(sender);
        f.setReceiver(receiver);
        f.setState(FriendshipState.PENDING);
        f.setRequestDate(LocalDateTime.now());
        return friendshipRepository.save(f);
    }

    @Transactional
    public FriendshipDTO createDto(FriendshipRequestDTO requestDTO) throws DataAccessException {
        Friendship newFriendship = create(requestDTO);
        return new FriendshipDTO(newFriendship);
    }

    @Transactional
    public Friendship update(Integer id, FriendshipRequestDTO requestDTO) throws DataAccessException {
        Friendship f = findById(id);
        FriendshipState previousState = f.getState();
        FriendshipState next = requestDTO.getState();
        f.setState(next);

        if (next == FriendshipState.ACCEPTED && previousState != FriendshipState.ACCEPTED) {
            if (f.getStartDate() == null) {
                f.setStartDate(LocalDateTime.now());
            }
            statisticsUpdateService.updateFriendsCount(f.getSender().getId());
            statisticsUpdateService.updateFriendsCount(f.getReceiver().getId());
            
            WebSocketEventListener webSocketEventListener = webSocketEventListenerProvider.getIfAvailable();
            if (webSocketEventListener != null) {
                String senderUsername = f.getSender().getUsername();
                String receiverUsername = f.getReceiver().getUsername();
                
                logger.info("Friendship accepted between {} and {}. Notifying both users", senderUsername, receiverUsername);
                
                webSocketEventListener.notifyFriendsAboutUser(receiverUsername);
                
                webSocketEventListener.notifyFriendsAboutUser(senderUsername);
            }
        }

        return friendshipRepository.save(f);
    }

    @Transactional
    public FriendshipDTO updateDto(Integer id, FriendshipRequestDTO requestDTO) throws DataAccessException {
        Friendship updatedFriendship = update(id, requestDTO);
        return new FriendshipDTO(updatedFriendship);
    }

    @Transactional
    public void delete(Integer id) throws DataAccessException {
        Friendship f = findById(id);
        if (f.getState() == FriendshipState.ACCEPTED) {
            statisticsUpdateService.decrementFriendsCount(f.getSender().getId());
            statisticsUpdateService.decrementFriendsCount(f.getReceiver().getId());
        }
        friendshipRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<String> getConfirmedFriendUsernames(String username) {
        Player currentPlayer = playerRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Player", "username", username));

        Iterable<Friendship> friendships = findAllFriendshipsByPlayerId(currentPlayer.getId(),
                FriendshipState.ACCEPTED);
        return StreamSupport.stream(friendships.spliterator(), false)
                .map(friendship -> {
                    if (friendship.getSender().getId().equals(currentPlayer.getId())) {
                        return friendship.getReceiver().getUsername();
                    } else {
                        return friendship.getSender().getUsername();
                    }
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean areFriends(Integer userId1, Integer userId2) {
        return friendshipRepository.areTheyFriends(userId1, userId2);
    }
}
