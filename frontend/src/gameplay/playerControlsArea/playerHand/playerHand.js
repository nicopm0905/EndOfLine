import React from 'react';
import PropTypes from 'prop-types';
import classNames from 'classnames';
import './playerHand.css';
import RerollButton from "./RerollButton";
import { COLOR_PREFIX } from '../.././gameRules';

const FALLBACK_CARD = 'placeholder';

const buildCardAssetName = (card, playerColor) => {
  if (!card?.type || !playerColor) return FALLBACK_CARD;
  const prefix = COLOR_PREFIX[playerColor.toUpperCase()];
  if (!prefix) return FALLBACK_CARD;

  if (card.type === 'START') return `${prefix}_START`;
  if (card.type === 'ENERGY') return `${prefix}_ENERGY`;
  if (card.type === 'LINE' && card.imageId) return `${prefix}_${card.imageId}`;

  return FALLBACK_CARD;
};



const PlayerHand = ({ cards, selectedCardId, onSelectCard, onReroll, canReroll, orientation = 'center', playerColor, vertical = false, gameMode, showReroll = true, size = 'normal' }) => {
  if (!cards?.length) {
    return (
      <section className="player-hand player-hand--empty">
        <p className="player-hand__placeholder">No cards in hand</p>
      </section>
    );
  }
  return (
    <section
      className={classNames('player-hand', `player-hand--${orientation}`, `player-hand--${size}`, {
        'player-hand--vertical': vertical, 
      })}
    >
      <div className="player-hand__cards-wrapper">
        {cards.map((card) => {
          const isSelected = card.id === selectedCardId;
          const orientationClass = card.orientation ? card.orientation.toLowerCase() : 'north';
          const imageSrc = `/assets/cards/${buildCardAssetName(card, playerColor)}.png`;
          return (
            <button
              key={card.id}
              type="button"
              className={classNames('player-hand__card', `player-hand__card--${orientationClass}`, {
                'player-hand__card--selected': isSelected,
              })}
              onClick={() => onSelectCard?.(card)}
            >
              <span className="player-hand__image-wrapper">
                <img src={imageSrc} alt={`Card ${card.type}`} className="player-hand__image" />
              </span>
            </button>
          );
        })}
      </div>
      {gameMode !== 'SOLITAIRE' && showReroll && (
        <div className="player-hand__reroll-wrapper">
          <RerollButton onClick={onReroll} disabled={!canReroll} />
        </div>
      )}
    </section>
  );
};
PlayerHand.propTypes = {
  cards: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.number.isRequired,
      type: PropTypes.string.isRequired,
      initiative: PropTypes.number.isRequired,
      defaultEntrance: PropTypes.string.isRequired,
      defaultExits: PropTypes.arrayOf(PropTypes.string).isRequired,
      imageId: PropTypes.number,
      orientation: PropTypes.string,
    })
  ),
  selectedCardId: PropTypes.number,
  orientation: PropTypes.oneOf(['left', 'center', 'right']),
  size: PropTypes.oneOf(['normal', 'small']),
  onSelectCard: PropTypes.func,
  playerColor: PropTypes.oneOf(Object.keys(COLOR_PREFIX)),
};
export default PlayerHand;
