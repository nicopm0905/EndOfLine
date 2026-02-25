import classNames from 'classnames';
import { COLOR_PREFIX } from '.././gameRules';
import PropTypes from 'prop-types';

const getEnergyRotation = (energy) => {
    switch (energy) {
        case 3:
        return 'rotate(0deg)';
        case 2:
        return 'rotate(90deg)';
        case 1:
        return 'rotate(180deg)';
        case 0:
        return 'rotate(270deg)';
        default:
        return 'rotate(0deg)';
    }
};

const EnergyCard = ({playerColor, energy}) => {
    if (!playerColor) return null;
    
    const prefix = COLOR_PREFIX[playerColor.toUpperCase()] || 'CB';
    const imageSrc = `/assets/cards/${prefix}_ENERGY.png`;
    const rotationStyle = getEnergyRotation(energy);

    return (
    <div className="energy-card-container">
      <img
        src={imageSrc}
        alt={`Remaining energy: ${energy}`}
        className="energy-card-image"
        style={{ transform: rotationStyle }}
      />
    </div>
  );
};

export default EnergyCard;