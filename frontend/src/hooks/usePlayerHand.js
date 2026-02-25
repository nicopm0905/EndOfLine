import { useEffect, useState } from 'react';
import tokenService from '../services/token.service';

export const usePlayerHand = (playerGameSessionId) => {
  const [cards, setCards] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const jwt = tokenService.getLocalAccessToken();

  useEffect(() => {
    let active = true;
    setLoading(true);

    const fetchPlayerHand = async () => {
      try {
        const response = await fetch(`/api/v1/cards/player-hand/${playerGameSessionId}`, {
          headers: {
            Authorization: `Bearer ${jwt}`,
            Accept: 'application/json'
          }
        });

        if (!response.ok) {
          throw new Error('Failed to fetch player hand');
        }

        const data = await response.json();
        if (active) {
          setCards(data);
          setLoading(false)
        }
      } catch (err) {
        if (active) {
          setError(err.message);
          setLoading(false)
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    };

    if (playerGameSessionId) {
      fetchPlayerHand();
      const InterrvalId = setInterval(fetchPlayerHand, 1000);
      return () => {
        active = false
        clearInterval(InterrvalId)
      }
    }

    return () => {
      active = false;
    };
  }, [playerGameSessionId, jwt]);

  return { cards, loading, error, setCards };
};

