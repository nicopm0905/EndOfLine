import React from 'react';
import './DrawDeckButton.css';
import { COLOR_PREFIX } from './../gameRules';

const DiscardPile = ({ gameId, jwt, topCard, onDrawSuccess, disabled, playerColor }) => {

    const handleDrawDiscardClick = async () => {
        try {
            const response = await fetch(`/api/v1/gameList/${gameId}/draw-discard`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${jwt}`
                }
            });

            if (response.ok) {
                const updatedGame = await response.json();
                console.log("Card recovered from discard pile");
                if (onDrawSuccess) onDrawSuccess(updatedGame);
            } else {
                const msg = await response.text();
                alert("Error recovering from discard pile: " + msg);
            }
        } catch (error) {
            console.error("Network error:", error);
        }
    };

    if (!topCard) return <div className="empty-discard-slot">Empty Discard</div>;

    const prefix = COLOR_PREFIX[playerColor.toUpperCase()] || 'CB';
    const imageUrl = topCard ? `/assets/cards/${prefix}_${topCard.imageId}.png` : null;

    return (
        <div className="draw-deck-container">
            <button 
                className="deck-pile-button discard-style" 
                onClick={handleDrawDiscardClick}
                disabled={disabled}
                title="Recover card from discard pile"
            >
                <img src={imageUrl} alt="Discard Top" className="discard-card-image" />
                
                <div className="discard-overlay-text">
                    RECOVER
                </div>
            </button>
        </div>
    );
};

export default DiscardPile;