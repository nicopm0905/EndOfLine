import React from 'react';
import PropTypes from 'prop-types';
import './startCard.css';
import classNames from 'classnames';
import { COLOR_PREFIX, getRotationStyle } from './gameRules';

const StartCard = ({ playerColor, orientation }) => {
  if (!playerColor) return null;

  const prefix = COLOR_PREFIX[playerColor.toUpperCase()] || 'CB';
  const imageSrc = `/assets/cards/${prefix}_START.png`;
  const rotationStyle = getRotationStyle(orientation);

  return (
    <div 
      className={classNames('start-card', `start-card--${orientation.toLowerCase()}`)}
      style={rotationStyle}
    >
      <img 
        src={imageSrc} 
        alt={`Inicio ${playerColor}`} 
        className="start-card__image"
      />
    </div>
  );
};

StartCard.propTypes = {
  playerColor: PropTypes.string.isRequired,
  orientation: PropTypes.oneOf(['N', 'S', 'E', 'W']).isRequired,
};

export default StartCard;