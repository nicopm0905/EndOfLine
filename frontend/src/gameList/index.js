import React from 'react';
import { useEffect, useState } from 'react';
import tokenService from "../services/token.service";
import jwt_decode from "jwt-decode";
import useFetchState from "../util/useFetchState";
import useIntervalFetchState from "../util/useIntervalFetchState";
import getErrorModal from "../util/getErrorModal";
import { Table } from "reactstrap";
import "../statistics/StatisticsGeneral.css";

const GameList = () => {
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);

  const jwt = tokenService.getLocalAccessToken();
  const userRoles = jwt ? jwt_decode(jwt).authorities : [];
  const isAdmin = userRoles.includes("ADMIN");

  const [activeGames, setActiveGames] = useIntervalFetchState(
    [],
    "/api/v1/gameList/active",
    jwt,
    setMessage,
    setVisible
  );

  const [finishedGames, setFinishedGames] = useIntervalFetchState(
    [],
    "/api/v1/gameList/finished",
    jwt,
    setMessage,
    setVisible
  );

  const modal = getErrorModal(setVisible, visible, message);

  const renderTable = (games, isFinished) => {
    if (games.length === 0) {
      return (
        <div className="text-center p-4">
          <p style={{ color: "rgba(255, 255, 255, 0.5)", fontStyle: "italic", fontSize: "1.1rem" }}>
            No {isFinished ? "finished" : "active"} games found.
          </p>
        </div>
      );
    }

    return (
      <Table borderless responsive className="transparent-table mb-0">
        <thead style={{ borderBottom: "2px solid rgba(0, 200, 255, 0.3)" }}>
          <tr>
            <th className="text-center" style={{ color: "#00c8ff", textTransform: "uppercase", letterSpacing: "1px" }}>Name</th>
            <th className="text-center" style={{ color: "#00c8ff", textTransform: "uppercase", letterSpacing: "1px" }}>Host</th>
            <th className="text-center" style={{ color: "#00c8ff", textTransform: "uppercase", letterSpacing: "1px" }}>Players</th>
            <th className="text-center" style={{ color: "#00c8ff", textTransform: "uppercase", letterSpacing: "1px" }}>Mode</th>
            <th className="text-center" style={{ color: "#00c8ff", textTransform: "uppercase", letterSpacing: "1px" }}>{isFinished ? "Winner" : "State"}</th>
            {isFinished && <th className="text-center" style={{ color: "#00c8ff", textTransform: "uppercase", letterSpacing: "1px" }}>Duration</th>}
          </tr>
        </thead>
        <tbody>
          {games.map((game, idx) => (
            <tr key={idx} style={{ borderBottom: "1px solid rgba(255, 255, 255, 0.1)" }}>
              <td className="text-center" style={{ color: "#e5f6ff", verticalAlign: "middle" }}>{game.name}</td>
              <td className="text-center" style={{ color: "#e5f6ff", verticalAlign: "middle" }}>{game.host?.username ?? game.host}</td>
              <td className="text-center" style={{ color: "#e5f6ff", verticalAlign: "middle" }}>
                <div style={{ display: "flex", flexDirection: "column", gap: "2px", alignItems: "center" }}>
                  {game.players
                    .sort((a, b) => (a.player?.id || 0) - (b.player?.id || 0))
                    .map((pgs, i) => (
                      <span key={i} style={{ fontSize: "0.9rem" }}>
                        {pgs.player?.username} <span style={{ opacity: 0.7 }}>({pgs.playerColor})</span>
                      </span>
                    ))}
                </div>
              </td>
              <td className="text-center" style={{ color: "#00ffbf", verticalAlign: "middle" }}>{game.gameMode}</td>
              <td className="text-center" style={{ color: isFinished ? "#ffb347" : "#e5f6ff", verticalAlign: "middle", fontWeight: isFinished ? "bold" : "normal" }}>
                {isFinished ? game.winner : game.state}
              </td>
              {isFinished && (
                <td className="text-center" style={{ color: "#e5f6ff", verticalAlign: "middle" }}>{game.duration_formatted}</td>
              )}
            </tr>
          ))}
        </tbody>
      </Table>
    );
  };

  return (
    <div className="dashboard-wrapper">
      <div className="dashboard-header" style={{ marginBottom: "2rem" }}>
        <h1 className="welcome-title">
          <span className="username-highlight">Game History</span>
        </h1>
        <p className="player-summary">
          Track all ongoing and past battles.
        </p>
      </div>

      <div style={{ padding: "0 2rem" }}>
        {modal}

        <div className="mb-5">
          <h3 className="text-center mb-3" style={{ color: "#00c8ff", textShadow: "0 0 10px rgba(0, 200, 255, 0.3)" }}>ACTIVE GAMES</h3>
          <div className="ranking-card" style={{ overflowX: "auto" }}>
            {renderTable(activeGames, false)}
          </div>
        </div>

        <div className="mb-4">
          <h3 className="text-center mb-3" style={{ color: "#00c8ff", textShadow: "0 0 10px rgba(0, 200, 255, 0.3)" }}>FINISHED GAMES</h3>
          <div className="ranking-card" style={{ overflowX: "auto" }}>
            {renderTable(finishedGames, true)}
          </div>
        </div>
      </div>
    </div>
  );
};

export default GameList;