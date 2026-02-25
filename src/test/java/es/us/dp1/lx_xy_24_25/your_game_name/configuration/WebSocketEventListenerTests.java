package es.us.dp1.lx_xy_24_25.your_game_name.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import es.us.dp1.lx_xy_24_25.your_game_name.dto.FriendshipOnlineDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.social.friendship.FriendshipService;
import es.us.dp1.lx_xy_24_25.your_game_name.social.friendship.UserPresenceStatus;

@ExtendWith(MockitoExtension.class)
class WebSocketEventListenerTests {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private FriendshipService friendshipService;

    @InjectMocks
    private WebSocketEventListener listener;

    @Test
    void handleWebSocketConnectListener_ShouldAddUserAndNotifyFriends() {
        SessionConnectedEvent event = mock(SessionConnectedEvent.class);
        Principal principal = mock(Principal.class);
        
        when(event.getUser()).thenReturn(principal);
        when(principal.getName()).thenReturn("userTest");
        
        when(friendshipService.getConfirmedFriendUsernames("userTest")).thenReturn(List.of("friend1", "friend2"));

        listener.handleWebSocketConnectListener(event);

        assertEquals(UserPresenceStatus.ONLINE, listener.getStatus("userTest"));

        verify(messagingTemplate).convertAndSendToUser(eq("friend1"), eq("/queue/friends-status"), any(FriendshipOnlineDTO.class));
        verify(messagingTemplate).convertAndSendToUser(eq("friend2"), eq("/queue/friends-status"), any(FriendshipOnlineDTO.class));
    }

    @Test
    void handleWebSocketDisconnectListener_ShouldRemoveUserAndNotifyFriends() {
        SessionConnectedEvent connectEvent = mock(SessionConnectedEvent.class);
        Principal principal = mock(Principal.class);
        when(connectEvent.getUser()).thenReturn(principal);
        when(principal.getName()).thenReturn("userTest");
        when(friendshipService.getConfirmedFriendUsernames("userTest")).thenReturn(List.of("friend1"));
        
        listener.handleWebSocketConnectListener(connectEvent);
        assertEquals(UserPresenceStatus.ONLINE, listener.getStatus("userTest"));

        SessionDisconnectEvent disconnectEvent = mock(SessionDisconnectEvent.class);
        when(disconnectEvent.getUser()).thenReturn(principal);

        listener.handleWebSocketDisconnectListener(disconnectEvent);

        assertEquals(UserPresenceStatus.OFFLINE, listener.getStatus("userTest"));
        verify(messagingTemplate, times(2)).convertAndSendToUser(eq("friend1"), eq("/queue/friends-status"), any(FriendshipOnlineDTO.class));
    }
    
    @Test
    void handleWebSocketConnectListener_NullUser_ShouldDoNothing() {
        SessionConnectedEvent event = mock(SessionConnectedEvent.class);
        when(event.getUser()).thenReturn(null);
        listener.handleWebSocketConnectListener(event);
        
        verify(friendshipService, times(0)).getConfirmedFriendUsernames(anyString());
    }
}
