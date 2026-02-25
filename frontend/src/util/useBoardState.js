import { useEffect, useCallback, useState } from "react";
import tokenService from "../services/token.service";

export const useBoardState = (gameSessionId, boardSize) => {
    const [board, setBoard] = useState([])
    const [placedCards, setPlacedCards] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState(null)

    useEffect(() => {
        if(boardSize > 0) {
            const emptyBoard = Array(boardSize).fill(null).map(() => {
                Array(boardSize).fill(null)
            })
            setBoard(emptyBoard)
        }
    }, [boardSize])
    
    const loadPlacedCards = useCallback(async () => {
        if(!gameSessionId) return

        try {
            setLoading(true)
            const jwt = tokenService.getLocalAccessToken()
            const response = await fetch( `/api/v1/gamesessions/${gameSessionId}/placed-cards`,
                { headers:{'Authorization': `Bearer ${jwt}`} }
            )

            if(!response.ok) {
                throw new Error('Failed to load placed cards')
            }

            const cards = await response.json()
            setPlacedCards(cards)

            const newBoard = Array(boardSize).fill(null).map(() => 
                Array(boardSize).fill(null)
            );

            cards.forEach(card => {
                if (card.row < boardSize && card.col < boardSize) {
                    newBoard[card.row][card.col] = card
                }
            })

            setBoard(newBoard)
            setError(null)

        } catch (err) {
            console.error('Error loading placed cards:', err);
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }, [gameSessionId, boardSize])

    useEffect(() => {
        loadPlacedCards()
    }, [loadPlacedCards])

    const addCardToBoard = useCallback((row, col, cardData) => {
        setBoard(prevBoard => {
            const newBoard = prevBoard.map(r => [...r])
            newBoard[row][col] = cardData
            return newBoard
        })
        setPlacedCards(prev => [...prev,cardData])
    },[])

    const reload = useCallback(() => {
        loadPlacedCards();
    }, [loadPlacedCards]);

return {
    board,
    placedCards,
    loading,
    error,
    addCardToBoard,
    reload 
};
}