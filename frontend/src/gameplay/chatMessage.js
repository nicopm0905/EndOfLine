import { useState, useEffect, useRef } from "react";
import tokenService from "../services/token.service";
import { useParams, useLocation } from "react-router-dom";

import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

const ChatMessage = ({ id, currentUser }) => {
    
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const messagesEndRef = useRef(null); 

    const jwt = tokenService.getLocalAccessToken();
    const location = useLocation();

    const [chat, setChat] = useState([]);
    const [messageInput, setMessageInput] = useState("");
    const stompClientRef = useRef(null);

    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [chat]);

    useEffect(() =>  {
        let mounted = true;
        const fetchChat = async () => {
            try{
                let chatData = null;
                if (location.state && location.state.gameChatData) {
                    chatData = location.state.gameChatData;
                } else {
                    const response = await fetch(`/api/v1/chat/${id}`, {
                        headers: {
                            Authorization: `Bearer ${jwt}`,
                        },
                    });
                    if (!response.ok) {
                        throw new Error('Failed to load chat data');
                    }
                    chatData = await response.json();
                }
                
                if(mounted) {
                    setChat(Array.isArray(chatData) ? chatData : []);
                    setLoading(false);
                }
            } catch (err) {
                if(mounted) {
                    setError(err.message);
                    setLoading(false);
                }
            }
        };

        if (id) {
            fetchChat();
        } else {
            setError("Game ID not provided");
            setLoading(false);
        }
        return () => { mounted = false; };
    }, [id, jwt, location.state]);

    useEffect(() => {
        console.log("Starting WebSocket connection for game:", id);
        const socket = new SockJS('http://localhost:8080/ws');
        const stompClient = Stomp.over(socket);

        stompClient.connect({}, () => {
            console.log('Connected to chat WebSocket');
            stompClient.subscribe(`/topic/chat/${id}`, (message) => {
                console.log("Message received via WS:", message.body);
                const receivedMsg = JSON.parse(message.body);
                setChat((prevChat) => [...prevChat, receivedMsg]);
            });
        }, (error) => {
            console.error('Error en WebSocket:', error);
        });
        stompClientRef.current = stompClient;
        return () => {
            if (stompClientRef.current) {
                stompClientRef.current.disconnect();
            }
        };
    }, [id]);


    const handleSendMessage = async (e) => {
        e.preventDefault();
        if (!messageInput.trim()) return;

        try {
            const resp = await fetch(`/api/v1/chat/${id}/message`, {
                method: 'POST',
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ message: messageInput })
            });
            
            if (!resp.ok) throw new Error('Failed to send message');
            
            setMessageInput('');
        } catch (err) {
            setError(err.message);
        }
    };

    return (
        <div style={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
            
            <div className="chat-messages">
                {chat && chat.map((msg, index) => {
                    const isOwnMessage = msg.username === currentUser;
                    return (
                        <div 
                           key={msg.id || index} 
                           className={`message-bubble ${isOwnMessage ? 'sent' : 'received'}`}
                        >
                            {!isOwnMessage && (
                                <div style={{ 
                                    fontSize: '0.75rem', 
                                    fontWeight: 'bold', 
                                    color: msg.color ? msg.color.toLowerCase() : '#00c8ff',
                                    marginBottom: '2px'
                                }}>
                                    {msg.username}
                                </div>
                            )}
                            
                            {msg.message}
                        </div>
                    );
                })}
                <div ref={messagesEndRef} />
            </div>
            <form className="chat-input-area" onSubmit={handleSendMessage}>
                <input
                    type="text"
                    value={messageInput}
                    onChange={(e) => setMessageInput(e.target.value)}
                    placeholder="Write your message..."
                />
                <button type="submit">
                    ➤
                </button>
            </form>
        </div>
    )
}

export default ChatMessage;