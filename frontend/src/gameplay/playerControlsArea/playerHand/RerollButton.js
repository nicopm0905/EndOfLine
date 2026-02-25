import React from "react";
import PropTypes from "prop-types";
import { IoRefreshCircle } from "react-icons/io5";
import "./rerollButton.css";

const RerollButton = ({ onClick, disabled }) => {
  return (
    <button
  className={`reroll-button ${disabled ? "reroll-button--used" : ""}`}
  onClick={onClick}
  disabled={disabled}
      title={disabled ? "Reroll ya utilizado" : "Reroll Hand"}
    >
      <IoRefreshCircle className="reroll-icon" />
    </button>
  );
};

RerollButton.propTypes = {
  onClick: PropTypes.func.isRequired,
  disabled: PropTypes.bool,
};

export default RerollButton;
