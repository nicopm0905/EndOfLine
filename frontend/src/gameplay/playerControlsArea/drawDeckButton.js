import React from 'react';
import './DrawDeckButton.css';

const DrawDeckButton = ({ gameId, jwt, onDrawSuccess, disabled }) => {

    const handleDrawClick = async () => {
        try {
            const response = await fetch(`/api/v1/gameList/${gameId}/drawdeck`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${jwt}`
                }
            });

            if (response.ok) {
                const updatedGame = await response.json();
                if (onDrawSuccess) onDrawSuccess(updatedGame);
            } else {
                const msg = await response.text();
                alert("Error drawing card: " + msg);
            }
        } catch (error) {
            console.error("Network error while drawing card:", error);
        }
    };

    return (
        <div className="draw-deck-container">
            <button 
                className="deck-pile-button" 
                onClick={handleDrawClick}
                disabled={disabled}
                title="Draw card from deck"
            >
                <div className="deck-count-indicator">
                    DRAW
                </div>
                
                <span className="draw-text">CARD</span>
            </button>
        </div>
    );
};

export default DrawDeckButton;