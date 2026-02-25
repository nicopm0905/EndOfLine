import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import tokenService from '../services/token.service';
import './Notification.css';

export default function Notification({ isOpen, onClose }) {
    const [invitations, setInvitations] = useState([]);
    const [message, setMessage] = useState('');
    const [visible, setVisible] = useState(false);
    const jwt = tokenService.getLocalAccessToken();
    const navigate = useNavigate();

    useEffect(() => {
        if (isOpen) {
            fetchInvitations();
        }
    }, [isOpen]);

    const fetchInvitations = async () => {
        try {
            const res = await fetch('/api/v1/invitations/received/pending', {
                headers: { 'Authorization': `Bearer ${jwt}` }
            });
            if (res.ok) {
                const data = await res.json();
                const sorted = data.sort((a, b) => {
                    if (a.status === 'PENDING' && b.status !== 'PENDING') return -1;
                    if (a.status !== 'PENDING' && b.status === 'PENDING') return 1;
                    return new Date(b.createdAt) - new Date(a.createdAt);
                });
                setInvitations(sorted);
            }
        } catch (e) {
            console.error("Error fetching notifications", e);
        }
    };

    const handleRespond = async (invitationId, action) => {
        try {
            const inv = invitations.find(i => i.id === invitationId);
            
            const res = await fetch(`/api/v1/invitations/${invitationId}/${action}`, {
                method: 'PUT',
                headers: { 'Authorization': `Bearer ${jwt}` }
            });
            if (res.ok) {
                fetchInvitations(); 
                
                if (action === 'accept' && inv) {
                    onClose();
                    const targetGameId = inv.gameId || inv.gameSessionId;
                    const isSpectator = inv.type === 'SPECTATOR'; 

                    if (targetGameId) {
                        if (isSpectator) {
                            navigate(`/spectate/${targetGameId}`); 

                        } else {
                            navigate(`/gameList/${targetGameId}`);
                        }
                    } else {
                        alert("Error: Game ID not found in invitation");
                    }
                }
            }else{
                let errorMessage = "Error processing invitation";
                try {
                    const errorData = await res.json();
                    setMessage(`Error processing invitation: ${errorData.message || errorMessage}`);
                    setVisible(true);
                } catch (parseError) {
                    const textError = await res.text();
                    if (textError) errorMessage = textError;
                }
                alert(`Cannot ${action} invitation: ${errorMessage}`);
                return;
            }
        } catch (e) {
            console.error("Error responding to invitation", e);
            setMessage("Error processing invitation");
            setVisible(true);
        }
    };

    if (!isOpen) return null;

    return (
        <>
            <div className="drawer-overlay" onClick={onClose}></div>
            
            <div className={`notification-drawer ${isOpen ? 'open' : ''}`}>
                <h2 className="drawer-title">NOTIFICATIONS</h2>
              <div className="notification-list">
                  {invitations.length === 0 && <p style={{textAlign:'center', color:'#666'}}>No active invitations.</p>}

                  {invitations.map(inv => {
                      const senderName = inv.senderUsername || "Unknown Player"; 
                      const gameName = inv.gameSessionName || "Game #" + inv.gameId;
                      const mode = inv.type || "PLAYER";
                      const dateStr = inv.createdAt ? new Date(inv.createdAt).toLocaleDateString() : "";

                      return (
                          <div key={inv.id} className="notif-card pending">
                              <div className="notif-header">
                                  <span>{dateStr}</span>
                                  <span style={{color: '#00c8ff'}}>{inv.status}</span>
                              </div>
                              <div className="notif-body">
                                  <span className="highlight">{senderName}</span> invited you to play 
                                  <span className="highlight"> {gameName}</span> as 
                                  <span style={{color: mode === 'PLAYER' ? '#00ff00':'#ff00ff', fontWeight:'bold'}}> {mode}</span>.
                              </div>
                              
                              <div className="notif-actions">
                                  <button className="btn-accept" onClick={() => handleRespond(inv.id, 'accept')}>
                                      ACCEPT
                                  </button>
                                  <button className="btn-decline" onClick={() => handleRespond(inv.id, 'reject')}>
                                      DECLINE
                                  </button>
                              </div>
                          </div>
                      );
                  })}
              </div>
            </div>
        </>
    );
}