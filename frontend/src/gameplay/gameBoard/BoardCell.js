import React from 'react';
import './BoardCell.css';
import { COLOR_PREFIX } from '../gameRules';

const BoardCell = ({
    row,
    col,
    card,
    isHovered,
    isValidPlacement,
    selectedCard,
    playerColor,
    selectedCardOrientation,
    onClick,
    onMouseEnter,
    onMouseLeave
}) => {
    const getCellClassName = () => {
        let classes = ['board-cell'];

        if (card) {
            classes.push('has-card');
            if (card.isLastPlacedByPlayer) {
                classes.push('last-placed');
            }
        } else if (isHovered && selectedCard?.id) {
            classes.push('hovered');
            
        }

        return classes.join(' ');
    };

    const getRotationDegrees = (orientation) => {
        const rotations = { N: 0, E: 90, S: 180, W: 270 };
        return rotations[orientation] || 0;
    };

    const getPlayerColorHex = (color) => {
        const colors = {
            RED: '#ff0000ff', BLUE: '#0000ffff', GREEN: '#00ff00ff',
            YELLOW: '#ffff00ff', ORANGE: '#ff5e00ff', PURPLE: '#cc00ffff',
            CYAN: '#00ffffff', PINK: '#ff00aaff'
        };
        return colors[color] || '#ffffff';
    };

    const getCardImageSrc = (cardData) => {
        const templateImageId = cardData.template?.imageId ?? cardData.imageId ?? cardData.cardTemplateId;
        const colorPrefix = COLOR_PREFIX[cardData.placedByPlayerColor] || '';

        if (cardData.cardType === 'START') {
            return `/assets/cards/${colorPrefix}_START.png`;
        }
        if (cardData.cardType === 'BACK') {
        return `/assets/cards/${colorPrefix}_BACK.png`;
        }
        const fileName = templateImageId
            ? `${colorPrefix ? `${colorPrefix}_` : ''}${templateImageId}.png`
            : 'placeholder.png';
        return `/assets/cards/${fileName}`;
    };

    const getSelectedCardPreview = () => {
        if (!selectedCard || !isHovered || card) return null;

        const tempCard = {
            template: { imageId: selectedCard.template?.imageId ?? selectedCard.imageId },
            placedByPlayerColor: playerColor,
            cardType: selectedCard.type ?? 'LINE'
        };

        const degrees = getRotationDegrees(selectedCardOrientation);
        const imageSrc = getCardImageSrc(tempCard);

        return (
            <div
                className={`placement-preview ${isValidPlacement ? 'valid' : 'invalid'}`}
                style={{ transform: `rotate(${degrees}deg)`, opacity: 0.5 }}
            >
                <img src={imageSrc} alt="Preview" className="card-image" />
            </div>
        );
    };

    return (
        <div
            className={getCellClassName()}
            onClick={onClick}
            onMouseEnter={onMouseEnter}
            onMouseLeave={onMouseLeave}
            data-row={row}
            data-col={col}
        >
            {card ? (
                <div
                    className="placed-card"
                    style={{
                        transform: `rotate(${getRotationDegrees(card.orientation)}deg)`,
                        borderColor: getPlayerColorHex(card.placedByPlayerColor || playerColor)
                    }}
                >
                    <img
                        src={getCardImageSrc(card)}
                        alt={`Card ${card.cardType}`}
                        className="card-image"
                        onError={(e) => {
                            console.error('Failed to load card image:', card);
                            e.target.src = '/assets/cards/placeholder.png';
                        }}
                    />

                    {card.isLastPlacedByPlayer && (
                        <div className="last-placed-indicator">
                            <span>⚡</span>
                        </div>
                    )}
                </div>
            ) : (
                isHovered &&
                selectedCard?.id && (
                    <>
                        {getSelectedCardPreview()}
                    </>
                )
            )}
        </div>
    );
};

export default BoardCell;