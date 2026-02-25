import React, { useState } from 'react';
import tokenService from '../../services/token.service';
import './DiscardButton.css'; 

const DiscardButton = ({ gameId, selectedCardId, gameMode, onDiscardSuccess }) => {
    const [loading, setLoading] = useState(false);
    const jwt = tokenService.getLocalAccessToken();

    const isSolitaireMode = gameMode === 'SOLITAIRE';
    
    if (!isSolitaireMode) {
        return null;
    }

    const handleDiscard = async () => {
        if (!selectedCardId) {
            alert("Select a card to discard.");
            return;
        }

        if (!window.confirm("Are you sure you want to discard this card? It will go to the discard pile and you will draw another.")) {
            return;
        }

        setLoading(true);
        try {
            const response = await fetch(`/api/v1/gameList/${gameId}/discard`, {
                method: "POST",
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ cardId: selectedCardId }),
            });

            if (response.ok) {
                const updatedGame = await response.json();
                if (onDiscardSuccess) onDiscardSuccess(updatedGame); 
            } else {
                const errorTxt = await response.text();
                alert(`Error al descartar: ${errorTxt}`);
            }
        } catch (error) {
            console.error("Error in discard request:", error);
            alert("Connection error while discarding.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <button 
            className="discard-button-component"
            onClick={handleDiscard}
            disabled={loading || !selectedCardId}
            title="Discard selected card and draw a new one"
        >
            {loading ? "..." : "DISCARD"}
        </button>
    );
};

export default DiscardButton;