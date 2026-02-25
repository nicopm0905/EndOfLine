import { useState, useCallback } from 'react';
import tokenService from '../services/token.service';

export const usePlaceCard = (gameSessionId, onSuccess) => {
    const [placing, setPlacing] = useState(false);
    const [error, setError] = useState(null);

    const placeCard = useCallback(async (playerCardId, row, col, orientation) => {
        if (!gameSessionId || !playerCardId) {
            setError('Missing required data');
            return null;
        }

        setPlacing(true);
        setError(null);

        try {
            const jwt = tokenService.getLocalAccessToken();
            const response = await fetch(
                `/api/v1/gamesessions/${gameSessionId}/place-card`,
                {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${jwt}`
                    },
                    body: JSON.stringify({
                        playerCardId,
                        row,
                        col,
                        orientation
                    })
                }
            );

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Failed to place card');
            }

            const placedCard = await response.json();
            
            if (onSuccess) {
                onSuccess(placedCard);
            }

            return placedCard;

        } catch (err) {
            console.error('Error placing card:', err);
            setError(err.message);
            return null;
        } finally {
            setPlacing(false);
        }
    }, [gameSessionId, onSuccess]);

    return {
        placeCard,
        placing,
        error
    };
};