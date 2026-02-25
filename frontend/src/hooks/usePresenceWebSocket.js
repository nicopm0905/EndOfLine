import { useEffect, useRef } from 'react';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';
import tokenService from '../services/token.service';

/**
 * @param {Function} onStatusUpdate - Callback opcional que se llama cuando se recibe una actualización de estado
 */
export const usePresenceWebSocket = (onStatusUpdate) => {
  const stompClientRef = useRef(null);
  const reconnectTimeoutRef = useRef(null);

  useEffect(() => {
    const connectPresenceWebSocket = () => {
      const jwt = tokenService.getLocalAccessToken();
      
      if (!jwt) {
        return null;
      }

      const socketFactory = () => new SockJS('http://localhost:8080/ws');
      const stompClient = Stomp.over(socketFactory);

      stompClient.reconnect_delay = 5000;

      stompClient.connect(
        { 'Authorization': `Bearer ${jwt}` },
        (frame) => {
          stompClient.subscribe('/user/queue/friends-status', (message) => {
            if (onStatusUpdate) {
              try {
                const data = JSON.parse(message.body);
                onStatusUpdate(data);
              } catch (error) {
                console.error('Error parsing status update:', error);
              }
            }
          });
        },
        (error) => {
          console.error('Presence WebSocket connection error:', error);
          reconnectTimeoutRef.current = setTimeout(() => {
            connectPresenceWebSocket();
          }, 5000);
        }
      );

      stompClientRef.current = stompClient;
      return stompClient;
    };

    const client = connectPresenceWebSocket();

    return () => {
      if (reconnectTimeoutRef.current) {
        clearTimeout(reconnectTimeoutRef.current);
      }
      
      if (stompClientRef.current && stompClientRef.current.connected) {
        stompClientRef.current.disconnect();
      }
    };
  }, [onStatusUpdate]);

  return stompClientRef.current;
};
