import React, { useEffect, useRef, useState } from "react";
import PropTypes from "prop-types";
import classNames from "classnames";

const EnergyActionsMenu = ({
  actions,
  activeEffect,
  isLockedByRound = false,
  disabled = false,
  remainingEnergy = 0,
  onSelectAction,
  onClearEffect,
  label = "Energy",
}) => {
  const [isOpen, setIsOpen] = useState(false);
  const containerRef = useRef(null);

  const toggleOpen = () => {
    if (disabled) return;
    setIsOpen((prev) => !prev);
  };

  const handleSelect = (action) => {
    onSelectAction?.(action);
    setIsOpen(false);
  };

  useEffect(() => {
    const handleOutsideClick = (event) => {
      if (containerRef.current && !containerRef.current.contains(event.target)) {
        setIsOpen(false);
      }
    };
    if (isOpen) document.addEventListener("mousedown", handleOutsideClick);
    return () => document.removeEventListener("mousedown", handleOutsideClick);
  }, [isOpen]);

  useEffect(() => {
    if (disabled) setIsOpen(false);
  }, [disabled]);

  return (
    <div
      ref={containerRef}
      className={classNames("energy-menu", {
        "energy-menu--open": isOpen,
        "energy-menu--disabled": disabled,
        "energy-menu--locked": isLockedByRound,
      })}
    >
      <button
        type="button"
        className="energy-menu__trigger"
        onClick={toggleOpen}
        aria-haspopup="true"
        aria-expanded={isOpen}
      >
        <span className="energy-menu__label">{label}</span>
        <span className="energy-menu__counter">{Math.max(0, remainingEnergy)}</span>
      </button>

      {isLockedByRound && (
        <span className="energy-menu__hint">Not available until round 3</span>
      )}

      {isOpen && (
        <div className="energy-menu__dropdown">
          {actions.map((action) => (
            <button
              key={action.id}
              type="button"
              className="energy-menu__option"
              onClick={() => handleSelect(action)}
              disabled={disabled}
            >
              <span className="energy-menu__option-title">{action.label}</span>
              <span className="energy-menu__option-description">
                {action.description}
              </span>
            </button>
          ))}
        </div>
      )}

      {activeEffect && (
        <div className="energy-menu__active-effect">
          <div className="energy-menu__active-header">
            <span className="energy-menu__active-title">{activeEffect.label}</span>
            {onClearEffect && (
              <button
                type="button"
                className="energy-menu__clear"
                onClick={onClearEffect}
              >
                Restore
              </button>
            )}
          </div>
          <p className="energy-menu__active-summary">{activeEffect.summary}</p>
          <ul className="energy-menu__active-meta">
            <li>Lines cards: {activeEffect.cardsToPlace}</li>
            <li>
              Continue from:{" "}
              {activeEffect.startFromPenultimate ? "Penultimate card" : "Last card"}
            </li>
          </ul>
        </div>
      )}
    </div>
  );
};

EnergyActionsMenu.propTypes = {
  actions: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.string.isRequired,
      label: PropTypes.string.isRequired,
      description: PropTypes.string.isRequired,
      summary: PropTypes.string.isRequired,
      cardsToPlace: PropTypes.number.isRequired,
      startFromPenultimate: PropTypes.bool,
    })
  ).isRequired,
  activeEffect: PropTypes.shape({
    id: PropTypes.string.isRequired,
    label: PropTypes.string.isRequired,
    summary: PropTypes.string.isRequired,
    cardsToPlace: PropTypes.number.isRequired,
    startFromPenultimate: PropTypes.bool,
  }),
  isLockedByRound: PropTypes.bool,
  disabled: PropTypes.bool,
  remainingEnergy: PropTypes.number,
  onSelectAction: PropTypes.func.isRequired,
  onClearEffect: PropTypes.func,
  label: PropTypes.string,
};

export default EnergyActionsMenu;