import jwt_decode from "jwt-decode";
import React from "react";
import { useNavigate } from "react-router-dom";
import "../App.css";
import "../static/css/home/home.css";
import "../static/css/home/HomeFloatingButton.css";
import tokenService from "../services/token.service";
import PixelBlast from "./PixelBlast";
import { GiLaurelCrown, GiPuzzle } from "react-icons/gi";
import { FaUserFriends } from "react-icons/fa";

export default function Home() {
  const navigate = useNavigate();
  const jwt = tokenService.getLocalAccessToken();
  let roles = [];
  if (jwt) {
    try {
      roles = jwt_decode(jwt).authorities;
    } catch (e) {
      console.error(e);
    }
  }
  const isLoggedIn = jwt;
  const isAdmin = roles.includes("ADMIN");

  return (
    <>
      <div style={{ width: "100%", minHeight: "100vh" }}>
        <PixelBlast
          style={{ width: "100%", height: "100vh" }}
          variant="square"
          pixelSize={6}
          color="#B19EEF"
          patternScale={3}
          patternDensity={1.2}
          pixelSizeJitter={0.5}
          enableRipples
          rippleSpeed={0.4}
          rippleThickness={0.12}
          rippleIntensityScale={1.5}
          liquidStrength={0.12}
          liquidRadius={1.2}
          liquidWobbleSpeed={5}
          speed={0.6}
          edgeFade={0.25}
          transparent
        />

        <h1 className="home-hero__title">END OF LINE</h1>
      </div>

      {isLoggedIn && !isAdmin && <div className="floating-buttons-row">

        <div className="floating-icon-btn" onClick={() => navigate("/achievements")}>
          <GiLaurelCrown className="floating-icon-btn__icon" />
          <span className="floating-icon-btn__label">Achievements</span>
        </div>

        <div className="floating-icon-btn" onClick={() => navigate("/friendships")}>
          <FaUserFriends className="floating-icon-btn__icon" />
          <span className="floating-icon-btn__label">Friendships</span>
        </div>

        <div className="floating-icon-btn" onClick={() => navigate("/puzzles")}>
          <GiPuzzle className="floating-icon-btn__icon" />
          <span className="floating-icon-btn__label">Puzzles</span>
        </div>

      </div>}
    </>
  );
}
