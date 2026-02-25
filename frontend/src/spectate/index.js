import React, { useEffect, useMemo, useState, useRef } from "react";
import { useParams, useNavigate } from "react-router-dom";
import tokenService from "../services/token.service";
import GameBoardComponent from "../gameplay/gameBoard/GameBoardComponent";
import PlayerHand from "../gameplay/playerControlsArea/playerHand/playerHand";
import ChatMessage from "../gameplay/chatMessage";
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';
import "./spectate.css";

const COLOR_MAP = {
  BLUE: "#3498db",
  RED: "#e74c3c",
  GREEN: "#27ae60",
  YELLOW: "#f1c40f",
  ORANGE: "#e67e22",
  PURPLE: "#9b59b6",
  PINK: "#ff6b81",
  CYAN: "#00bcd4",
};

const formatDuration = (startIso) => {
  if (!startIso) return "--:--:--";
  const start = new Date(startIso).getTime();
  if (isNaN(start)) return "--:--:--";
  const diff = Math.max(0, Math.floor((Date.now() - start) / 1000));
  const h = String(Math.floor(diff / 3600)).padStart(2, "0");
  const m = String(Math.floor((diff % 3600) / 60)).padStart(2, "0");
  const s = String(diff % 60).padStart(2, "0");
  return `${h}:${m}:${s}`;
};

const Spectate = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const jwt = tokenService.getLocalAccessToken();

  const [game, setGame] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [elapsed, setElapsed] = useState("--:--:--");
  const [hands, setHands] = useState({});
  const stompClientRef = useRef(null);

  const fetchGame = async () => {
    try {
      const resp = await fetch(`/api/v1/gameList/${id}/spectate`, {
        headers: { Authorization: `Bearer ${jwt}` },
      });
      if (!resp.ok) throw new Error(await resp.text());
      const data = await resp.json();
      setGame(data);
      setError(null);
    } catch (e) {
      setError(e.message || "Error loading game");
    } finally {
      setLoading(false);
    }
  };

  const fetchHand = async (pgsId) => {
    setHands((prev) => ({
      ...prev,
      [pgsId]: { ...(prev[pgsId] || {}), loading: true, error: null },
    }));
    try {
      const resp = await fetch(`/api/v1/cards/player-hand/${pgsId}`, {
        headers: { Authorization: `Bearer ${jwt}`, Accept: "application/json" },
      });
      if (!resp.ok) throw new Error(await resp.text());
      const cards = await resp.json();
      setHands((prev) => ({
        ...prev,
        [pgsId]: { ...(prev[pgsId] || {}), cards, loading: false, error: null },
      }));
    } catch (e) {
      setHands((prev) => ({
        ...prev,
        [pgsId]: { ...(prev[pgsId] || {}), loading: false, error: e.message },
      }));
    }
  };

  const toggleHand = (pgsId) => {
    setHands((prev) => {
      const current = prev[pgsId] || { open: false, cards: [] };
      const nextOpen = !current.open;
      const nextState = { ...prev, [pgsId]: { ...current, open: nextOpen } };
      if (nextOpen && (!current.cards || current.cards.length === 0) && !current.loading) {
        fetchHand(pgsId);
      }
      return nextState;
    });
  };

  useEffect(() => {
    fetchGame();
    
    if (!id) return;

    const socket = new SockJS('http://localhost:8080/ws');
    const stompClient = Stomp.over(socket);

    stompClient.connect({}, () => {
      stompClient.subscribe(`/topic/spectate/${id}`, (message) => {

        if (message.body) {
          const updatedGameData = JSON.parse(message.body);
          setGame(prevGame => ({ ...prevGame, ...updatedGameData }));
        }
      });
    }, (error) => {
      console.error("WebSocket connection error:", error);
    });

    stompClientRef.current = stompClient;

    return () => { 
      if (stompClientRef.current) {
        stompClientRef.current.disconnect();
      }
    };
  }, [id, jwt]);

  useEffect(() => {
    if (!game?.startTime) return;
    setElapsed(formatDuration(game.startTime));
    const t = setInterval(() => setElapsed(formatDuration(game.startTime)), 1000);
    return () => clearInterval(t);
  }, [game?.startTime]);

  const players = useMemo(() => {
    if (!game?.players) return [];
    return Array.from(game.players).sort((a, b) => (a.id ?? 0) - (b.id ?? 0));
  }, [game]);

  const boardSize = game?.boardSize || 5;
  const currentTurnName =
    players.find((p) => p.id === game?.gamePlayerTurnId)?.player?.username || "—";

  const openPlayers = players.filter((p) => hands[p.id]?.open);
  const leftHands = [];
  const rightHands = [];
  openPlayers.forEach((p, idx) => {
    if (idx % 2 === 0) leftHands.push(p);
    else rightHands.push(p);
  });

  const renderHand = (p) => {
    const handState = hands[p.id] || { cards: [], loading: false, error: null };
    return (
      <div key={p.id} className="hand-slot-inboard">
        <div
          className="hand-slot__header"
          style={{ borderColor: COLOR_MAP[p.playerColor] || "#334155" }}
        >
          <span
            className="dot"
            style={{ backgroundColor: COLOR_MAP[p.playerColor] || "#334155" }}
          />
          <span className="name">{p.player?.username || "Unknown"}</span>
          {p.id === game.gamePlayerTurnId && <span className="tag">Turn</span>}
        </div>
        {handState.loading && <p className="hand-msg">Loading hand...</p>}
        {handState.error && <p className="hand-msg hand-error">{handState.error}</p>}
        {!handState.loading && !handState.error && (
          <PlayerHand
            cards={handState.cards || []}
            selectedCardId={null}
            onSelectCard={() => {}}
            onReroll={() => {}}
            canReroll={false}
            orientation="center"
            playerColor={p.playerColor}
            vertical={true}
            gameMode={game.gameMode}
            showReroll={false}
            size="small"
          />
        )}
      </div>
    );
  };

  if (loading) return <div className="spectate-loading">Loading...</div>;
  if (error) return <div className="spectate-error">{error}</div>;
  if (!game) return <div className="spectate-error">No game data.</div>;

  return (
    <>
      {game.state === "PENDING" && (
        <div className="spectator-wait-overlay">
          <div className="spectator-wait-modal">
            <h1>GAME HAS NOT STARTED YET</h1>
            <p>Waiting for the host to start the game...</p>
            <div className="spectator-spinner"></div>
          </div>
        </div>
      )}
      <div className="spectate-shell">
      {game.state === "FINISHED" && (
        <div className="game-over-overlay">
          <div className="game-over-modal">
            <h1>GAME OVER</h1>
            <button className="leave-game-btn" onClick={() => navigate("/gameList/joinGame")}>
              Back to Menu
            </button>
          </div>
        </div>
      )}
      <div className="spectate-header">
        <div className="spectate-title-block">
          <h2>{game.gameName || "Spectating game"}</h2>
          <div className="spectate-meta">
            <span>Mode: {game.gameMode}</span>
            <span>State: {game.state}</span>
          </div>
        </div>
        <div className="spectate-center-metrics">
          <div className="metric">
            <span className="metric-label">Round</span>
            <span className="metric-value metric-value--accent">{game.round}</span>
          </div>
          <div className="metric">
            <span className="metric-label">Elapsed</span>
            <span className="metric-value metric-value--accent">{elapsed}</span>
          </div>
        </div>
        <button className="spectate-back spectate-back--danger" onClick={() => navigate("/spectate")}>
          Back to Games in course
        </button>
      </div>

      <div className="spectate-layout">
        <div className="spectate-side">
          <h3>Players</h3>
          <div className="spectate-players">
            {players.map((p) => {
              const color = COLOR_MAP[p.playerColor] || "#34495e";
              const isTurn = p.id === game.gamePlayerTurnId;
              const handState = hands[p.id] || { open: false };
              return (
                <div
                  key={p.id}
                  className={`spectate-player ${isTurn ? "turn" : ""}`}
                  style={{ borderColor: color }}
                >
                  <div className="spectate-player__header">
                    <span className="spectate-player__dot" style={{ backgroundColor: color }} />
                    <span className="spectate-player__name">{p.player?.username || "Unknown"}</span>
                    {isTurn && <span className="spectate-player__tag">Turn</span>}
                  </div>
                  <div className="spectate-player__stats">
                    <span>Energy: {p.energy ?? "-"} </span>
                    <span>Cards placed (round): {p.cardsPlacedThisRound ?? 0}</span>
                  </div>
                  <button className="spectate-hand-toggle" onClick={() => toggleHand(p.id)}>
                    {handState.open ? "Hide hand" : "Show hand"}
                  </button>
                </div>
              );
            })}
          </div>
        </div>

        <div className="spectate-board-panel">
          <div className="spectate-board-header">
            <span>Turn: {currentTurnName}</span>
            <span>Board {boardSize}x{boardSize}</span>
          </div>
          <div className="spectate-board-grid">
            <div className="board-hands left">
              {leftHands.map((p) => renderHand(p))}
            </div>
            <div className="board-center">
              <GameBoardComponent
                key={game?.placedCards?.length || 0}
                gameSessionId={id}
                boardSize={boardSize}
                selectedCard={null}
                playerColor={null}
                currentTurn={game.gamePlayerTurnId}
                isMyTurn={true}
                hideTurnIndicator={true}
                cardsPlaced={0}
                maxCards={0}
              />
            </div>
            <div className="board-hands right">
              {rightHands.map((p) => renderHand(p))}
            </div>
          </div>
        </div>

        <div className="spectate-side">
          <h3>Summary</h3>
          <div className="spectate-summary">
            <div><strong>Board size:</strong> {boardSize}x{boardSize}</div>
            <div><strong>Current turn:</strong> {currentTurnName}</div>
            <div><strong>Placed cards:</strong> {game.placedCards?.length ?? 0}</div>
            {game.placedCards?.length ? (
              <div>
                <strong>Last move by:</strong>{" "}
                {game.placedCards[game.placedCards.length - 1]?.placedByUsername || "Unknown"}
              </div>
            ) : null}
          </div>

          <div className="spectate-chat">
            <h3>CHAT</h3>
            <div className="spectate-chat__box">
              <ChatMessage id={id} currentUser={null} />
            </div>
          </div>
        </div>
      </div>
      </div>
    </>
  );
};

export default Spectate;
