import React, { useState, useCallback } from 'react';
import BoardCell from './BoardCell';
import { useBoardState } from '../../util/useBoardState';
import { usePlaceCard } from '../../util/usePlaceCard';
import './GameBoard.css';

const GameBoardComponent = ({ 
    gameSessionId, 
    boardSize, 
    selectedCard,
    playerColor,
    playerGameSessionId,
    playerTeamNumber,
    teamBySessionId = {},
    currentTurn,
    isMyTurn,
    cardsPlaced = 0,
    maxCards = 1,
    onCardPlaced,
    onTurnEnd,
    refreshKey,
    gameMode,
    activeEnergyEffect
}) => {
    const [hoveredCell, setHoveredCell] = useState(null);
    const [cardOrientation, setCardOrientation] = useState('N');

    const { 
        board, 
        error, 
        reload,
        addCardToBoard 
    } = useBoardState(gameSessionId, boardSize);

    const { placeCard, placing } = usePlaceCard(gameSessionId, (placedCard) => {
        addCardToBoard(placedCard.row, placedCard.col, placedCard);
        if (onCardPlaced) {
            onCardPlaced(placedCard);
        }
        reload(); 
        if (onTurnEnd) {
            setTimeout(() => onTurnEnd(), 200);
        }
    });

    const isJumpActive = gameMode === 'TEAMBATTLE' && activeEnergyEffect === 'JUMP_LINE';

    const isOwnCard = useCallback((card) => {
        if (!card) return false;
        if (playerGameSessionId != null && card.placedByPlayerGameSessionId != null) {
            return Number(card.placedByPlayerGameSessionId) === Number(playerGameSessionId);
        }
        return card.placedByPlayerColor === playerColor;
    }, [playerGameSessionId, playerColor]);

    const isTeammateCard = useCallback((card) => {
        if (!card || playerTeamNumber == null) return false;
        if (isOwnCard(card)) return false;
        const cardTeam = teamBySessionId?.[card.placedByPlayerGameSessionId];
        return cardTeam != null && Number(cardTeam) === Number(playerTeamNumber);
    }, [teamBySessionId, playerTeamNumber, isOwnCard]);

    const canJumpToCell = useCallback((row, col) => {
        if (!isJumpActive || playerTeamNumber == null) return false;
        const directions = [
            [-1, 0], // Norte
            [0, 1],  // Este
            [1, 0],  // Sur
            [0, -1]  // Oeste
        ];
        const size = parseInt(boardSize, 10);
        const wrap = (value) => ((value % size) + size) % size;

        return directions.some(([dr, dc]) => {
            const middleRow = wrap(row - dr);
            const middleCol = wrap(col - dc);
            const anchorRow = wrap(row - 2 * dr);
            const anchorCol = wrap(col - 2 * dc);

            const middleCard = board[middleRow]?.[middleCol];
            const anchorCard = board[anchorRow]?.[anchorCol];

            return isTeammateCard(middleCard) && isOwnCard(anchorCard);
        });
    }, [board, boardSize, isJumpActive, isTeammateCard, isOwnCard, playerTeamNumber]);

    const hasAdjacentCard = useCallback((row, col) => {
        const directions = [
            [-1, 0], // Norte
            [0, 1],  // Este
            [1, 0],  // Sur
            [0, -1]  // Oeste
        ];

        const size = parseInt(boardSize, 10); 

        return directions.some(([dr, dc]) => {
            const newRow = (row + dr + size) % size; 
            const newCol = (col + dc + size) % size;
            
            const neighborCard = board[newRow]?.[newCol];

            return neighborCard && isOwnCard(neighborCard);
        });
    }, [board, boardSize, isOwnCard]);

    const isValidPlacement = useCallback((row, col) => {
        if (!selectedCard || !isMyTurn) return false;
        
        if (board[row]?.[col]) return false;
        
        const placedCardsCount = board.flat().filter(cell => cell !== null).length;
        if (placedCardsCount === 0) {
            const center = Math.floor(boardSize / 2);
            return row === center && col === center;
        }

        if (isJumpActive && canJumpToCell(row, col)) {
            return true;
        }

        return hasAdjacentCard(row, col);
    }, [selectedCard, isMyTurn, board, boardSize, isJumpActive, canJumpToCell, hasAdjacentCard]);

    const handleCellClick = useCallback(async (row, col) => {
        if (!selectedCard || !isMyTurn || placing) return;
        
        if (!isValidPlacement(row, col)) {
            alert('Invalid position. The first card must be placed in the center.');
            return;
        }

        try {
            await placeCard(selectedCard.id, row, col, cardOrientation);
        } catch (err) {
            alert('Error placing card: ' + err.message);
        }
    }, [selectedCard, isMyTurn, placing, cardOrientation, isValidPlacement, placeCard]);

    const rotateCard = useCallback(() => {
        const rotations = ['N', 'E', 'S', 'W'];
        const currentIndex = rotations.indexOf(cardOrientation);
        const nextIndex = (currentIndex + 1) % 4;
        setCardOrientation(rotations[nextIndex]);
    }, [cardOrientation]);

    React.useEffect(() => {
        reload()
    }, [refreshKey, reload])

    React.useEffect(() => {
        const handleKeyPress = (e) => {
            if (e.key === 'r' || e.key === 'R') {
                rotateCard();
            }
        };

        window.addEventListener('keydown', handleKeyPress);
        return () => window.removeEventListener('keydown', handleKeyPress);
    }, [rotateCard]);

    if (error) {
        return <div className="board-error">Error: {error}</div>;
    }

    return (
        <div className="game-board-container">
            {selectedCard && isMyTurn && (
                <div className="board-controls">
                    <button onClick={rotateCard} className="rotate-btn">
                        Rotate (R): {cardOrientation}
                    </button>
                    
           </div>
            )}

            <div 
                className="board-grid" 
                style={{ 
                    '--board-size': boardSize,
                    gridTemplateColumns: `repeat(${boardSize}, 1fr)`
                }}
            >
                {board?.map((row, rowIndex) => 
                    row?.map((cell, colIndex) => (
                        <BoardCell
                            key={`${rowIndex}-${colIndex}`}
                            row={rowIndex}
                            col={colIndex}
                            card={cell}
                            isHovered={hoveredCell?.row === rowIndex && hoveredCell?.col === colIndex}
                            isValidPlacement={isValidPlacement(rowIndex, colIndex)}
                            selectedCard={selectedCard}
                            selectedCardId={selectedCard?.id}
                            selectedCardOrientation = {cardOrientation}
                            playerColor={playerColor}
                            onClick={() => handleCellClick(rowIndex, colIndex)}
                            onMouseEnter={() => setHoveredCell({ row: rowIndex, col: colIndex })}
                            onMouseLeave={() => setHoveredCell(null)}
                        />
                    ))
                )}
            </div>

            {!isMyTurn && (
                <div className="turn-indicator">
                    Waiting turn...
                </div>
            )}

            {placing && (
                <div className="placing-overlay">
                    Placing card...
                </div>
            )}
        </div>
    );
};

export default GameBoardComponent;
