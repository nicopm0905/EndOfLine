import React, { useState, useEffect } from "react";
import tokenService from "../../services/token.service";
import "../../statistics/StatisticsGeneral.css";
import "./createGame.css";
import { useNavigate } from "react-router-dom";
import { getActiveLobbyId } from "../../util/activeLobby";

export default function CreateGame() {

    const emptyGame = {
        name: "",
        password: "",
        host: "",
        gameMode: "VERSUS",
        winner: "",
        numPlayers: 2,
        boardSize: null,
        isPrivate: false,
    };

    const jwt = tokenService.getLocalAccessToken();
    const navigate = useNavigate();
    const activeLobbyId = getActiveLobbyId();
    const isLobbyLocked = Boolean(activeLobbyId);

    const [game, setGame] = useState(emptyGame);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false);

    function handleChange(e) {
        const { name, value, type, checked } = e.target;
        const newValue = type === "checkbox" ? checked : value;
        setGame((prev) => {
            const newGameData = { ...prev, [name]: newValue };

            if (name === "gameMode") {
                if (newValue === "VERSUS") {
                    newGameData.numPlayers = 2;
                } 
            }
            return newGameData;
        });
    }

    async function handleSubmit(e) {
        e.preventDefault();
        if (isLobbyLocked) {
            setError("You are already in a lobby. Return to it to continue.");
            return;
        }
        setError(null);
        setLoading(true);
        try {
            const payload = {
                name: game.name,
                host: game.host,
                password: game.password || null,
                gameMode: game.gameMode,
                numPlayers: Number(game.numPlayers) || 1,
                boardSize: null,
                private: game.isPrivate,
            };
            delete payload.winner;
            const response = await fetch("/api/v1/gameList", {
                method: "POST",
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    Accept: "application/json",
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(payload),
            });

            const createdGame = await response.json();
            navigate(`/gameList/${createdGame.id}`);

            setLoading(false);
        } catch (err) {
            setLoading(false);
            setError("Error creating game");
        }
    }

    useEffect(() => {
        try {
            const user = tokenService.getUser();
            if (user && user.username) {
                setGame((prev) => ({ ...prev, host: user.username }));
            }
        } catch (err) {
        }
    }, []);

    const inputStyle = {
        backgroundColor: "rgba(255, 255, 255, 0.05)",
        border: "1px solid rgba(0, 200, 255, 0.3)",
        color: "#e5f6ff",
        borderRadius: "10px",
        padding: "0.8rem",
        width: "100%",
        marginBottom: "1.5rem"
    };

    const labelStyle = {
        color: "#00c8ff",
        fontWeight: "600",
        marginBottom: "0.5rem",
        display: "block"
    };

    if (isLobbyLocked) {
        return (
            <div className="dashboard-wrapper" style={{ display: "flex", justifyContent: "center", alignItems: "center" }}>
                <div className="ranking-card" style={{ padding: "2.5rem", maxWidth: "520px", width: "100%" }}>
                    <h2 className="text-center" style={{ marginBottom: "1rem" }}>
                        <span className="username-highlight">Lobby in progress</span>
                    </h2>
                    <p style={{ color: "#e5f6ff", textAlign: "center", marginBottom: "1.5rem" }}>
                        You cannot create a new game while you are already in a lobby.
                    </p>
                    <div className="text-center">
                        <button
                            type="button"
                            style={{
                                backgroundColor: "#00c8ff",
                                color: "black",
                                fontWeight: "bold",
                                padding: "0.8rem 2rem",
                                borderRadius: "30px",
                                border: "none",
                                boxShadow: "0 0 15px rgba(0, 200, 255, 0.5)",
                                fontSize: "1.1rem",
                                cursor: "pointer",
                                transition: "all 0.3s ease"
                            }}
                            onClick={() => navigate(`/gameList/${activeLobbyId}`)}
                        >
                            Back to Lobby
                        </button>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="dashboard-wrapper" style={{ display: "flex", justifyContent: "center", alignItems: "center" }}>
            <div style={{ width: "100%", maxWidth: "600px" }}>

                <div className="ranking-card" style={{ padding: "3rem" }}>
                    <h2 className="text-center" style={{ marginBottom: "2rem" }}>
                        <span className="username-highlight">Create New Game</span>
                    </h2>

                    <form onSubmit={handleSubmit}>
                        <div>
                            <label style={labelStyle}>Name</label>
                            <input
                                style={inputStyle}
                                name="name"
                                value={game.name}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <div>
                            <label style={labelStyle}>GameMode</label>
                            <select
                                style={{ 
                                    ...inputStyle, 
                                    color: "#ffffff", 
                                    backgroundColor: "rgba(0, 50, 80, 0.9)",
                                    fontWeight: "600",
                                    cursor: "pointer"
                                }}
                                name="gameMode"
                                value={game.gameMode}
                                onChange={handleChange}
                            >   
                                <option value="VERSUS" style={{ backgroundColor: "#003250", color: "#ffffff", padding: "10px" }}>VERSUS</option>
                                <option value="BATTLE_ROYALE" style={{ backgroundColor: "#003250", color: "#ffffff", padding: "10px" }}>BATTLE ROYALE</option>
                                <option value="TEAMBATTLE" style={{ backgroundColor: "#003250", color: "#ffffff", padding: "10px" }}>TEAMBATTLE</option>
                            </select>
                        </div>

                        <div>
                            <label style={labelStyle}>Number of players</label>
                            <input
                                type="number"
                                style={inputStyle}
                                name="numPlayers"
                                min={2}
                                max={8}
                                value={game.numPlayers}
                                onChange={handleChange}
                                disabled={game.gameMode === "VERSUS"}
                            />
                        </div>

                        <div className="d-flex align-items-center mb-3">
                            <input
                                type="checkbox"
                                name="isPrivate"
                                id="isPrivate"
                                checked={game.isPrivate}
                                onChange={handleChange}
                                style={{
                                    width: "20px",
                                    height: "20px",
                                    marginRight: "10px",
                                    accentColor: "#00c8ff"
                                }}
                            />
                            <label htmlFor="isPrivate" style={{ color: "#e5f6ff", fontSize: "1rem", cursor: "pointer" }}>
                                Private Game
                            </label>
                        </div>

                        {game.isPrivate && (
                            <div>
                                <label style={labelStyle}>
                                    Password
                                </label>
                                <input
                                    type="password"
                                    style={inputStyle}
                                    name="password"
                                    value={game.password}
                                    onChange={handleChange}
                                    required={true}
                                />
                            </div>
                        )}

                        {error && <div className="alert alert-danger" style={{ backgroundColor: "rgba(255, 0, 85, 0.2)", border: "1px solid #ff0055", color: "#ff0055" }}>{error}</div>}

                        <div className="text-center mt-4">
                            <button
                                type="submit"
                                disabled={loading}
                                style={{
                                    backgroundColor: "#00c8ff",
                                    color: "black",
                                    fontWeight: "bold",
                                    padding: "0.8rem 2rem",
                                    borderRadius: "30px",
                                    border: "none",
                                    boxShadow: "0 0 15px rgba(0, 200, 255, 0.5)",
                                    fontSize: "1.1rem",
                                    opacity: loading ? 0.7 : 1,
                                    cursor: loading ? "not-allowed" : "pointer",
                                    transition: "all 0.3s ease"
                                }}
                            >
                                {loading ? "Creating..." : "Create Game"}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
}
