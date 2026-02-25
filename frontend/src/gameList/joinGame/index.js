import React, { useEffect, useState } from 'react';
import './joinGame.css';
import tokenService from "../../services/token.service";
import getErrorModal from "../../util/getErrorModal";
import jwt_decode from "jwt-decode";
import { useNavigate } from "react-router-dom";
import useIntervalFetchState from '../../util/useIntervalFetchState';
import "../../statistics/StatisticsGeneral.css";
import { getActiveLobbyId } from "../../util/activeLobby";


const JoinGame = () => {

  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const navigate = useNavigate();

  const jwt = tokenService.getLocalAccessToken();
  const activeLobbyId = getActiveLobbyId();
  const isLobbyLocked = Boolean(activeLobbyId);

  const [promptingGameId, setPromptingGameId] = useState(null);
  const [password, setPassword] = useState("");

  const userRoles = jwt ? jwt_decode(jwt).authorities : [];
  const isAdmin = userRoles.includes("ADMIN");

  const [games, setGames] = useIntervalFetchState(
    [],
    `/api/v1/gameList/pending`,
    jwt,
    setMessage,
    setVisible
  );

  const modal = getErrorModal(setVisible, visible, message);

    async function handleJoin(gameId, password = null) {
      if (isLobbyLocked) {
        setMessage("You are already in a lobby. Return to it before joining another game.");
        setVisible(true);
        return;
      }
      try {
        const fetchOptions = {
          method: "POST",
          headers: {
            Authorization: `Bearer ${jwt}`,
            Accept: "application/json",
            "Content-Type": "application/json",
          }
        };

        if(password){
          fetchOptions.body = JSON.stringify({ password: password });
        }

      if (password) {
        fetchOptions.body = JSON.stringify({ password: password });
      }

      const response = await fetch(`/api/v1/gameList/${gameId}/join`, fetchOptions);

      if (!response.ok) {
        let errorData = { message: "Failed to join game" };
        try {
          errorData = await response.json();
        } catch (e) {
          errorData.message = await response.text() || errorData.message;
        }
        throw new Error(errorData.message || "Failed to join game");
      }
      navigate(`/gameList/${gameId}`);

        window.location.href = `/gameList/${gameId}`;
      } catch (err) {
        setMessage(err.message || "Error joining game");
        setVisible(true);
        setPromptingGameId(null);
      }
    }
  return (
    <div className="dashboard-wrapper">
      <div className="dashboard-header" style={{ marginBottom: "2rem" }}>
        <h1 className="welcome-title">
          <span className="username-highlight">Join Game</span>
        </h1>
        <p className="player-summary">
          Find a match and start playing!
        </p>
      </div>

      <div style={{ padding: "0 2rem" }}>
        {isLobbyLocked && (
          <div className="alert alert-warning" style={{ marginBottom: "1.5rem" }}>
            You are already in a lobby{" "}
          </div>
        )}
        {modal}

        {games.length === 0 ? (
          <div className="text-center p-5">
            <p style={{ color: "rgba(255, 255, 255, 0.5)", fontStyle: "italic", fontSize: "1.2rem" }}>
              No pending games at this moment. Why not create one?
            </p>
          </div>
        ) : (
          <div style={{
            display: "grid",
            gridTemplateColumns: "repeat(auto-fill, minmax(300px, 1fr))",
            gap: "2rem"
          }}>
            {games.map((game) => (

              <div
                key={game.id}
                className="ranking-card"
                style={{
                  display: "flex",
                  flexDirection: "column",
                  justifyContent: "space-between",
                  minHeight: "200px"
                }}
              >
                <div>
                  <h3 style={{ color: "#00c8ff", fontWeight: "bold", marginBottom: "1rem", fontSize: "1.5rem" }}>{game.name}</h3>
                  <div style={{ color: "#e5f6ff", marginBottom: "1.5rem", fontSize: "0.95rem", lineHeight: "1.6" }}>
                    <div><strong style={{ color: "rgba(0, 200, 255, 0.7)" }}>Host:</strong> {game.host}</div>
                    <div><strong style={{ color: "rgba(0, 200, 255, 0.7)" }}>Mode:</strong> {game.gameMode}</div>
                    <div>
                      <strong style={{ color: "rgba(0, 200, 255, 0.7)" }}>Players:</strong> {game.players?.length ?? 0}/{game.numPlayers}
                    </div>
                    {game.private && (
                      <div style={{ marginTop: "0.5rem", color: "#ff0055", fontWeight: "bold" }}>
                        🔒 Private Game
                      </div>
                    )}
                  </div>
                </div>

                {promptingGameId === game.id ? (
                  <form
                    onSubmit={(e) => { e.preventDefault(); handleJoin(game.id, password); }}
                    style={{ marginTop: "auto" }}
                  >
                    <input
                      type="password"
                      placeholder="Enter password..."
                      value={password}
                      onChange={(e) => setPassword(e.target.value)}
                      autoFocus
                      required
                      style={{
                        width: "100%",
                        padding: "0.6rem",
                        borderRadius: "8px",
                        border: "1px solid #00c8ff",
                        backgroundColor: "rgba(255,255,255,0.05)",
                        color: "white",
                        marginBottom: "0.8rem"
                      }}
                    />
                    <div style={{ display: "flex", gap: "0.5rem" }}>
                      <button
                        type="submit"
                        disabled={isLobbyLocked}
                        style={{
                          flex: 1,
                          backgroundColor: "#00c8ff",
                          color: "black",
                          border: "none",
                          borderRadius: "8px",
                          padding: "0.5rem",
                          fontWeight: "bold",
                          cursor: "pointer"
                        }}
                      >
                        Join
                      </button>
                      <button
                        type="button"
                        onClick={() => setPromptingGameId(null)}
                        style={{
                          width: "40px",
                          backgroundColor: "rgba(255, 0, 85, 0.2)",
                          border: "1px solid #ff0055",
                          color: "#ff0055",
                          borderRadius: "8px",
                          cursor: "pointer",
                          display: "flex",
                          alignItems: "center",
                          justifyContent: "center"
                        }}
                      >
                        ✕
                      </button>
                    </div>
                  </form>) : (
                  <button
                    onClick={() => {
                      if (game.private) {
                        setPromptingGameId(game.id);
                        setPassword("");
                      } else {
                        handleJoin(game.id, null);
                      }
                    }}
                    disabled={isLobbyLocked}
                    style={{
                      width: "100%",
                      padding: "0.8rem",
                      backgroundColor: "rgba(0, 200, 255, 0.15)",
                      border: "1px solid #00c8ff",
                      color: "#00c8ff",
                      borderRadius: "10px",
                      fontWeight: "bold",
                      cursor: "pointer",
                      transition: "all 0.3s ease",
                      textTransform: "uppercase",
                      letterSpacing: "1px",
                      marginTop: "auto"
                    }}
                    onMouseOver={(e) => {
                      e.target.style.backgroundColor = "#00c8ff";
                      e.target.style.color = "black";
                    }}
                    onMouseOut={(e) => {
                      e.target.style.backgroundColor = "rgba(0, 200, 255, 0.15)";
                      e.target.style.color = "#00c8ff";
                    }}
                  >
                    {game.private ? "Enter Password" : "Join Game"}
                  </button>
                )}
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

export default JoinGame;
