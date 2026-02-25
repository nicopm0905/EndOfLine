import React from 'react';
import './Game.css';

const OpponentCard = ({ pgs, isTurn }) => {
  if (!pgs || !pgs.player) {
    return null; 
  }

  return (
    <div 
      className={`opponent-card-container ${isTurn ? 'active-turn' : ''}`} 
      style={{ "--p-color": pgs.playerColor || "#00c8ff" }}
    >
      <img
        src={`/avatars/avatar${pgs.player.avatarId}.png`}
        alt={pgs.player.username}
        onError={(e) => { e.target.src = '/avatars/default.png'; }}
        className="opponent-avatar-small"
      />
      
      {isTurn && <div className="turn-thinking-icon">⚡</div>}

      <div className="opponent-info">
        <span>{pgs.player.username}</span>
        <div>Energy: {pgs.energy}</div>
      </div>
    </div>
  );
};

export default OpponentCard;