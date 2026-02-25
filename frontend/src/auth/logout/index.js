import React from "react";
import { Link } from "react-router-dom";
import "../../static/css/auth/authButton.css";
import "../../static/css/auth/authPage.css";
import tokenService from "../../services/token.service";
import "../../statistics/StatisticsGeneral.css";

const Logout = () => {
  function sendLogoutRequest() {
    const jwt = window.sessionStorage.getItem("jwt");
    if (jwt || typeof jwt === "undefined") {
      tokenService.removeUser();
      window.location.href = "/";
    } else {
      alert("There is no user logged in");
    }
  }

  return (
    <div className="dashboard-wrapper" style={{ display: "flex", justifyContent: "center", alignItems: "center" }}>
      <div style={{ width: "100%", maxWidth: "500px", textAlign: "center" }}>
        <div className="ranking-card" style={{ padding: "3rem" }}>
          <h2 className="text-center text-md" style={{ color: "#e5f6ff", marginBottom: "2rem" }}>
            Are you sure you want to log out?
          </h2>
          <div className="options-row" style={{ display: "flex", justifyContent: "center", gap: "2rem" }}>
            <Link
              to="/"
              style={{
                textDecoration: "none",
                backgroundColor: "transparent",
                color: "#00c8ff",
                border: "2px solid #00c8ff",
                padding: "0.8rem 2rem",
                borderRadius: "30px",
                fontWeight: "bold",
                transition: "all 0.3s ease"
              }}
            >
              No
            </Link>
            <button className="auth-button" onClick={() => sendLogoutRequest()}>
              Yes
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Logout;
