import React, { useState, useEffect } from 'react';
import tokenService from '../services/token.service';

export default function InviteFriendModal({ open, onClose, gameId }) {
  const [friends, setFriends] = useState([]);
  const [selectedFriendId, setSelectedFriendId] = useState("");
  const [role, setRole] = useState('PLAYER');
  
  const jwt = tokenService.getLocalAccessToken();
  const user = tokenService.getUser(); 

  useEffect(() => {
    if (!open) return;

    const fetchFriends = async () => {
      try {
        const res = await fetch('/api/v1/friendships/all', {
          headers: {
            "Authorization": `Bearer ${jwt}`,
            "Content-Type": "application/json"
          }
        });
        
        if (!res.ok) throw new Error("Error fetching friendships");
        
        const data = await res.json();
        const all = Array.isArray(data) ? data : [];

        const mine = all.filter(
          f => f.sender?.id === user.id || f.receiver?.id === user.id
        );

        const accepted = mine.filter(f => (f.state || f.friendState) === "ACCEPTED");

        const friendsList = accepted.map(f => {
           const isSender = f.sender.id === user.id;
           return isSender ? f.receiver : f.sender;
        });

        console.log("Friends found:", friendsList);
        setFriends(friendsList);

      } catch (error) {
        console.error("Error loading friends:", error);
        setFriends([]);
      }
    };

    fetchFriends();
  }, [open, jwt, user.id]);

  if (!open) return null;

  const sendInvite = async () => {
    if (!selectedFriendId) return;
    const friend = friends.find(f => f.id === Number(selectedFriendId));
    if (!friend) return;
    const body = {
      receiverId: friend.id,
      gameSessionId: gameId,
      type: role
    };

    try {
        const res = await fetch('/api/v1/invitations', {
            method: 'POST',
            headers: { 
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${jwt}`
            },
            body: JSON.stringify(body)
        });
        if (res.ok) {
            alert("Invitation sent successfully!");
            onClose();
        } else {
            const txt = await res.text();
            try {
                const jsonError = JSON.parse(txt);
                if (Array.isArray(jsonError)) {
                    alert(`Error: ${jsonError[0].field} ${jsonError[0].defaultMessage}`);
                } else {
                    alert("Error: " + (jsonError.message || txt));
                }
            } catch {
                alert("Error: " + txt);
            }
        }
    } catch (e) {
        console.error(e);
        alert("Connection error");
    }
  };

  return (
    <div style={modalWrapStyle}>
      <div style={modalStyle}>
        <h3 style={{color: '#00c8ff', textAlign:'center', marginBottom: '15px'}}>INVITE FRIEND</h3>
        <select 
            className="form-select"
            value={selectedFriendId} 
            onChange={e => setSelectedFriendId(e.target.value)}
            style={{width:'100%', padding: '10px', marginBottom: '20px', color: 'black'}}
        >
          <option value="">-- SELECT FRIEND --</option>
          {friends.map(friend => (
             <option key={friend.id} value={friend.id}>
                 {friend.username}
             </option>
          ))}
        </select>
        <div style={{color: '#00c8ff', marginBottom: '20px', display:'flex', justifyContent:'center', gap:'20px'}}>
          <label style={{cursor:'pointer'}}>
            <input 
                type="radio" 
                checked={role==='PLAYER'} 
                onChange={() => setRole('PLAYER')} 
                style={{marginRight:'5px'}}
            /> 
            PLAY
          </label>
          <label style={{cursor:'pointer'}}>
            <input 
                type="radio" 
                checked={role==='SPECTATOR'} 
                onChange={() => setRole('SPECTATOR')} 
                style={{marginRight:'5px'}}
            /> 
            SPECTATE
          </label>
        </div>
        <div style={{display:'flex', justifyContent:'space-between'}}>
            <button onClick={onClose} className="btn-cyber-small" style={{background:'#444', border:'1px solid #666'}}>CLOSE</button>
            <button onClick={sendInvite} className="btn-cyber-small">SEND</button>
        </div>
      </div>
    </div>
  );
}

const modalWrapStyle = {
  position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.8)', 
  display:'flex', alignItems:'center', justifyContent:'center', zIndex: 9999,
  backdropFilter: 'blur(3px)'
};

const modalStyle = { 
    background:'rgba(10, 16, 30, 0.95)', 
    padding: '30px', 
    borderRadius: '15px', 
    width: '400px', 
    border: '1px solid #00c8ff', 
    boxShadow: '0 0 25px rgba(0,200,255,0.2)'
};