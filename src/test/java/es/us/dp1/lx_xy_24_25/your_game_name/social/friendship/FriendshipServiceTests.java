package es.us.dp1.lx_xy_24_25.your_game_name.social.friendship;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;

import es.us.dp1.lx_xy_24_25.your_game_name.configuration.WebSocketEventListener;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.FriendshipDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.FriendshipRequestDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.BadRequestException;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.statistic.playerStatistics.StatisticsUpdateService;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class FriendshipServiceTests {

    @Mock
    private FriendshipRepository friendshipRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private StatisticsUpdateService statisticsUpdateService;

    @Mock
    private ObjectProvider<WebSocketEventListener> webSocketEventListenerProvider;

    @InjectMocks
    private FriendshipService service;

    private Player sender;
    private Player receiver;
    private Friendship friendship;
    private FriendshipRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        sender = new Player();
        sender.setId(1);
        sender.setUsername("sender");

        receiver = new Player();
        receiver.setId(2);
        receiver.setUsername("receiver");

        friendship = new Friendship();
        friendship.setId(1);
        friendship.setSender(sender);
        friendship.setReceiver(receiver);
        friendship.setState(FriendshipState.PENDING);

        requestDTO = new FriendshipRequestDTO();
        requestDTO.setSender(1);
        requestDTO.setReceiver(2);
    }

    @Test
    void create_ShouldThrowException_WhenSenderEqualsReceiver() {
        requestDTO.setReceiver(1);
        assertThrows(BadRequestException.class, () -> service.create(requestDTO));
    }

    @Test
    void create_ShouldCreateFriendshipSuccessfully() {
        when(playerRepository.existsPlayerById(1)).thenReturn(true);
        when(playerRepository.existsPlayerById(2)).thenReturn(true);
        when(friendshipRepository.findFriendshipBySenderAndReceiver(1, 2)).thenReturn(Optional.empty());
        when(playerRepository.findById(1)).thenReturn(Optional.of(sender));
        when(playerRepository.findById(2)).thenReturn(Optional.of(receiver));
        when(friendshipRepository.save(any(Friendship.class))).thenReturn(friendship);

        Friendship result = service.create(requestDTO);

        assertEquals(FriendshipState.PENDING, result.getState());
        verify(friendshipRepository).save(any(Friendship.class));
    }

    @Test
    void update_ShouldUpdateToAcceptedAndTriggerStats() {
        requestDTO.setState(FriendshipState.ACCEPTED);
        when(friendshipRepository.findById(1)).thenReturn(Optional.of(friendship));
        when(friendshipRepository.save(any(Friendship.class))).thenReturn(friendship);

        service.update(1, requestDTO);

        assertEquals(FriendshipState.ACCEPTED, friendship.getState());
        verify(statisticsUpdateService).updateFriendsCount(1);
        verify(statisticsUpdateService).updateFriendsCount(2);
    }

    @Test
    void delete_ShouldDecrementStats_IfAccepted() {
        friendship.setState(FriendshipState.ACCEPTED);
        when(friendshipRepository.findById(1)).thenReturn(Optional.of(friendship));

        service.delete(1);

        verify(statisticsUpdateService).decrementFriendsCount(1);
        verify(statisticsUpdateService).decrementFriendsCount(2);
        verify(friendshipRepository).deleteById(1);
    }

    @Test
    void getConfirmedFriendUsernames_ShouldReturnFriendList() {
        friendship.setState(FriendshipState.ACCEPTED);
        when(playerRepository.findByUsername("sender")).thenReturn(Optional.of(sender));
        when(friendshipRepository.findAllFriendshipsByPlayerId(1)).thenReturn(List.of(friendship));

        List<String> friends = service.getConfirmedFriendUsernames("sender");

        assertEquals(1, friends.size());
        assertEquals("receiver", friends.get(0));
    }

    @Test
    void findById_ShouldReturnFriendship() {
        when(friendshipRepository.findById(1)).thenReturn(Optional.of(friendship));
        Friendship result = service.findById(1);
        assertEquals(friendship, result);
    }

    @Test
    void findById_ShouldThrowException_WhenNotFound() {
        when(friendshipRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.findById(99));
    }

    @Test
    void findAll_ShouldReturnList() {
        when(friendshipRepository.findAll()).thenReturn(List.of(friendship));
        List<Friendship> result = service.findAll();
        assertEquals(1, result.size());
    }

    @Test
    void areFriends_ShouldReturnTrue() {
        when(friendshipRepository.areTheyFriends(1, 2)).thenReturn(true);
        boolean result = service.areFriends(1, 2);
        assertTrue(result);
    }

    @Test
    void createDto_ShouldReturnDto() {
        when(playerRepository.existsPlayerById(1)).thenReturn(true);
        when(playerRepository.existsPlayerById(2)).thenReturn(true);
        when(friendshipRepository.findFriendshipBySenderAndReceiver(1, 2)).thenReturn(Optional.empty());
        when(playerRepository.findById(1)).thenReturn(Optional.of(sender));
        when(playerRepository.findById(2)).thenReturn(Optional.of(receiver));
        when(friendshipRepository.save(any(Friendship.class))).thenReturn(friendship);

        FriendshipDTO result = service.createDto(requestDTO);
        assertNotNull(result);
    }

    @Test
    void updateDto_ShouldReturnDto() {
        when(friendshipRepository.findById(1)).thenReturn(Optional.of(friendship));
        when(friendshipRepository.save(any(Friendship.class))).thenReturn(friendship);

        FriendshipDTO result = service.updateDto(1, requestDTO);
        assertNotNull(result);
    }

    @Test
    void findAllDtosWithStatus_ShouldReturnDtos() {
        when(friendshipRepository.findAll()).thenReturn(List.of(friendship));
        List<FriendshipDTO> result = service.findAllDtosWithStatus();
        assertEquals(1, result.size());
    }
}
