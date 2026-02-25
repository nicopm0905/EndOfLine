package es.us.dp1.lx_xy_24_25.your_game_name.configuration;

import es.us.dp1.lx_xy_24_25.your_game_name.dto.FriendshipOnlineDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.social.friendship.FriendshipService;
import es.us.dp1.lx_xy_24_25.your_game_name.social.friendship.UserPresenceStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final FriendshipService friendService;

    private static final Map<String, UserPresenceStatus> ONLINE_USERS = new ConcurrentHashMap<>();
    
    public WebSocketEventListener(SimpMessagingTemplate messagingTemplate, FriendshipService friendService) {
        this.messagingTemplate = messagingTemplate;
        this.friendService = friendService;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        Principal user = event.getUser();

        if (user != null) {
            String username = user.getName();
            logger.info("User connected via WebSocket: {}", username);
            ONLINE_USERS.put(username, UserPresenceStatus.ONLINE);
            notifyFriends(username, UserPresenceStatus.ONLINE);
        } else {
            logger.debug("Anonymous or technical WebSocket connection detected");
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        Principal user = event.getUser();

        if (user != null) {
            String username = user.getName();
            logger.info("User disconnected from WebSocket: {}", username);
            ONLINE_USERS.remove(username);
            notifyFriends(username, UserPresenceStatus.OFFLINE);
        }
    }

    public UserPresenceStatus getStatus(String username) {
        return ONLINE_USERS.getOrDefault(username, UserPresenceStatus.OFFLINE);
    }
    
    public void notifyFriendsAboutUser(String username) {
        UserPresenceStatus status = getStatus(username);
        notifyFriends(username, status);
    }
    
    public void notifyFriends(String userChanging, UserPresenceStatus status) {
        try {
            logger.debug("Looking for friends to notify about user: {}", userChanging);
            List<String> friends = friendService.getConfirmedFriendUsernames(userChanging);
            logger.debug("Found {} friends for user: {}", friends.size(), userChanging);
            FriendshipOnlineDTO notification = new FriendshipOnlineDTO(userChanging, status);

            for (String friend : friends) {
                logger.debug("Sending presence notification to friend: {}", friend);
                messagingTemplate.convertAndSendToUser(
                    friend, 
                    "/queue/friends-status", 
                    notification
                );
            }
        } catch (Exception e) {
            logger.error("Error sending presence notification to friends for user: {}", userChanging, e);
        }
    }
}
