import React, { useState, useEffect, useRef } from "react";
import useIntervalFetchState from "../util/useIntervalFetchState";
import { useParams, useLocation, useNavigate } from "react-router-dom";
import tokenService from "../services/token.service";
import "./Game.css";
import { IoChatbubbleEllipsesOutline, IoRemove } from "react-icons/io5";
import { usePlayerHand } from "../hooks/usePlayerHand";
import { useRerollHand } from "../hooks/useRerollHand";
import { START_POSITIONS, ENERGY_ACTIONS } from "./gameRules";
import ChatMessage from "./chatMessage";
import PlayerHand from "./playerControlsArea/playerHand/playerHand";
import StartCard from "./startCard";
import OpponentCard from "./OpponentCard";
import EnergyCard from "./playerControlsArea/energyCard";
import FirstTurnModal from "./firstTurnModal";
import GameBoardComponent from "./gameBoard/GameBoardComponent";
import EnergyActionsMenu from "./playerControlsArea/EnergyActionsMenu";
import RerollButton from "./playerControlsArea/playerHand/RerollButton";
import DiscardButton from "./playerControlsArea/discardButton";
import DrawDeckButton from "./playerControlsArea/drawDeckButton";
import DiscardPile from "./playerControlsArea/DiscardPile";
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';
import { clearActiveLobbyId, setActiveLobbyId } from "../util/activeLobby";

const GameBoard = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const location = useLocation();
  const jwt = tokenService.getLocalAccessToken();
  const [error, setError] = useState(null);
  const [errorVisible, setErrorVisible] = useState(false);

  const [game, setGame] = useIntervalFetchState(
    null,
    `/api/v1/gameList/${id}`,
    jwt,
    setError,
    setErrorVisible,
    null,
    1000
  )
  const [showFirstTurn, setShowFirstTurn] = useState(true)
  const [hasCheckedInitiative, setHasCheckedInitiative] = useState(false);
  const [toogleChat, setToogleChat] = useState(false);
  const [currentUser, setCurrentUser] = useState(null);
  const [leaving, setLeaving] = useState(false);
  const [handCards, setHandCards] = useState([]);
  const [selectedCardId, setSelectedCardId] = useState(null);
  const [playerGameSessionId, setPlayerGameSessionId] = useState(null);
  const [playerColor, setPlayerColor] = useState(null);
  const [hasRerolled, setHasRerolled] = useState(false);
  const [activeEnergyEffect, setActiveEnergyEffect] = useState(null);
  const [energyError, setEnergyError] = useState(null);
  const [hasUsedEnergyThisRound, setHasUsedEnergyThisRound] = useState(false);

  const [notification, setNotification] = useState(null);
  const prevEliminatedPlayersRef = useRef(new Set());

  const decreaseEnergy = () => {
    const playerIndex = game.players.findIndex(p => p.player.username === currentUser);
    const currentEnergy = game.players[playerIndex].energy;
    const newEnergy = currentEnergy <= 0 ? 3 : currentEnergy - 1;

    const newPlayers = game.players.map((player, index) => {
      if (index === playerIndex) {
        return { ...player, energy: newEnergy };
      }
      return player;
    });
    setGame({
      ...game,
      players: newPlayers
    });
  }

  const stompClientRef = useRef(null);

  useEffect(() => {
    if (!id) return;
    setActiveLobbyId(id);
  }, [id]);

  useEffect(() => {
    if (!id) return;

    const socket = new SockJS('http://localhost:8080/ws');
    const stompClient = Stomp.over(socket);

    stompClient.connect({}, () => {
      stompClient.subscribe(`/topic/game/${id}`, (message) => {
        if (message.body) {
          const updatedGameData = JSON.parse(message.body);
          setGame(prevGame => ({ ...prevGame, ...updatedGameData }));
          if (updatedGameData.players) {
            const me = updatedGameData.players.find(p =>
              (p.player?.username === currentUser) || (p.username === currentUser)
            );

            if (me && me.hand) {
              setHandCards(me.hand);
            }
          }
        }
      });

    }, (error) => {
      console.error(error);
    });

    stompClientRef.current = stompClient;

    return () => { if (stompClientRef.current) stompClientRef.current.disconnect(); };
  }, [id, setGame, currentUser]);

  useEffect(() => {
    if (game) {
      if (!hasCheckedInitiative) {
        const currentRound = game.round ?? 0;
        const storageKey = `initiative_seen_${id}`;
        const alreadySeen = sessionStorage.getItem(storageKey);

        if (alreadySeen || currentRound > 1) {
          setShowFirstTurn(false);
        }
        setHasCheckedInitiative(true);
      }

      const user = tokenService.getUser();
      if (user && user.username) {
        setCurrentUser(user.username);
        const currentPlayerSession = game.players.find(
          (pgs) => pgs.player?.username === user.username
        );
        if (currentPlayerSession) {
          setPlayerGameSessionId(Number(currentPlayerSession.id));
          setPlayerColor(currentPlayerSession.playerColor);
          setHasRerolled(!!currentPlayerSession.hasRerolled);
        }
      }
    }
  }, [game, hasCheckedInitiative, id]);

  useEffect(() => {
    if (game && game.players) {
      const currentEliminated = game.players.filter(p => p.turnOrder === null);
      
      currentEliminated.forEach(p => {
        const playerId = p.player.id;
        if (!prevEliminatedPlayersRef.current.has(playerId)) {
          const username = p.player.username;
          if (username !== currentUser) {
             setNotification(`${username} HAS BEEN ELIMINATED! 💀`);
             setTimeout(() => setNotification(null), 5000);
          }
        }
      });

      const newSet = new Set(currentEliminated.map(p => p.player.id));
      prevEliminatedPlayersRef.current = newSet;
    }
  }, [game, currentUser]);


  const { cards, loading: handLoading, error: handError } = usePlayerHand(playerGameSessionId);
  useEffect(() => {
    if (cards && cards?.length > 0) {
      setHandCards(cards);
    }
  }, [cards]);



  const { rerollHand, isRerolling } = useRerollHand(
    playerGameSessionId,
    jwt,
    () => {
      setHasRerolled(true);
      setTimeout(() => window.location.reload(), 300);
    }
  );

  const handleLeaveGame = async () => {
    if (!window.confirm("Are you sure you want to leave the game?")) return;
    if (leaving) return;
    setLeaving(true);
    let leaveSucceeded = false;

    try {
      const response = await fetch(`/api/v1/gameList/${id}/leave`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${jwt}`,
          "Content-Type": "application/json",
        },
      });

      if (response.ok) {
        leaveSucceeded = true;
        navigate("/");
      }
      else alert("Error leaving game: " + (await response.text()));
    } catch (error) {
      alert("Could not leave the game");
    } finally {
      if (leaveSucceeded) {
        clearActiveLobbyId();
      }
      setLeaving(false);
    }
  };

  const handleEnergyActionSelection = async (action) => {
    if (!game || !currentUser || !action) return;

    if (hasUsedEnergyThisRound) {
      setEnergyError("You have already used energy this round.");
      return;
    }

    const playerIndex = game.players.findIndex(
      (p) => p.player.username === currentUser
    );
    if (playerIndex < 0) return;

    try {
      const response = await fetch(`/api/v1/gameList/${id}/energy`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${jwt}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ actionId: action.id }),
      });

      if (!response.ok) {
        const message = await response.text();
        throw new Error(message || "Could not consume energy.");
      }

      const updatedPlayer = await response.json();

      setGame((prev) => {
        if (!prev?.players) return prev;
        const updatedPlayers = prev.players.map((player) =>
          player.id === updatedPlayer.id ? updatedPlayer : player
        );
        return { ...prev, players: updatedPlayers };
      });

      setEnergyError(null);
      setActiveEnergyEffect({
        id: action.id,
        label: action.label,
        summary: action.summary,
        cardsToPlace: action.cardsToPlace,
        startFromPenultimate: action.startFromPenultimate ?? false,
      });
      setHasUsedEnergyThisRound(true);
    } catch (err) {
      setEnergyError(err.message);
    }
  };

  const handleDiscard = async (card) => {
    try {
      const response = await fetch(`/api/v1/gameList/${id}/discard`, {
        method: 'POST',
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${jwt}`
        },
        body: JSON.stringify({ cardId: card.id })
      });

      if (response.ok) {
        const partidaActualizada = await response.json();
        const me = partidaActualizada.players.find(p => p.player.username === currentUser);
        if (me) {
          setHandCards(me.hand);
        }
        setSelectedCardId(null);
      } else {
        console.error("Error discarding card");
      }
    } catch (error) {
      console.error("Network error", error);
    }
  };

  const resetEnergyEffect = () => setActiveEnergyEffect(null);

  useEffect(() => {
    setActiveEnergyEffect(null);
    setHasUsedEnergyThisRound(false);
  }, [game?.round]);

  useEffect(() => {
    if (errorVisible && error && error.toLowerCase().includes("not found")) {
      window.alert("The host has abandoned the game. You will be redirected.");
      clearActiveLobbyId();
      navigate("/gameList/joinGame");
    }
  }, [errorVisible, error, navigate]);

  const handleDiscardSuccess = (winnerId) => {
    setShowFirstTurn(false);
  };

  if (!game && !error) return <div className="game-loading">Loading...</div>;
  if (error) return <div className="game-error">Error: {error}</div>;
  if (!game) return <div className="game-error">Data not found.</div>;

  const currentPlayerGameSession = game?.players?.find((p) => p.player.username === currentUser);
  const boardSize = game.boardSize || 5;
  const totalCells = boardSize * boardSize;
  const cells = Array.from({ length: totalCells }, (_, i) => i);

  const playerCount = game?.players ? game.players.length : 0;
  const startConfig = START_POSITIONS[playerCount] || [];
  const isEliminated = currentPlayerGameSession && currentPlayerGameSession.turnOrder === null;
  const isGameOver = game?.state === 'FINISHED';
  const winnerName = game?.winner;
  const opponents = (game?.players || []).filter((p) => p.player.username !== currentUser)
    .sort((a, b) => {
      const orderA = a.turnOrder ?? 99;
      const orderB = b.turnOrder ?? 99;
      if (orderA !== orderB) {
        return orderA - orderB;
      }
      return a.id - b.id;
    });
  const remainingEnergy = currentPlayerGameSession?.energy ?? 0;
  const currentRound = game?.round ?? 0;
  const teamBySessionId = (game?.players || []).reduce((acc, player) => {
    if (player?.id != null) {
      acc[player.id] = player.teamNumber ?? null;
    }
    return acc;
  }, {});

  const isSolitary = game?.gameMode === "SOLITARY_PUZZLE" || game?.gameMode === "SOLITAIRE";
  const isMyTurnId = isSolitary || Number(game?.gamePlayerTurnId) === Number(playerGameSessionId);
  const cardsPlaced = currentPlayerGameSession?.cardsPlacedThisRound || 0;
  const baseMaxCards = currentRound === 0 ? 1 : 2;
  const maxCards = currentPlayerGameSession?.energyCardsToPlaceOverride ?? baseMaxCards;
  const canPlaceMore = cardsPlaced <= maxCards;
  const isMyTurn = isMyTurnId && canPlaceMore && !isEliminated && !isGameOver;
  const isEnergyUnlocked = currentRound >= 3;
  const hasPlacedCard = cardsPlaced > 0;
  const canRerollHand = !hasRerolled && !isRerolling && !hasPlacedCard;
  const isHandEmpty = handCards?.length === 0;
  const handleDrawSuccess = (updatedGame) => {
    const me = updatedGame.players?.find(p =>
      (p.player && p.player.username === currentUser) ||
      (p.username === currentUser)
    );

    if (me && me.hand) {
      setHandCards(me.hand);
    } else {
      console.warn("Could not find hand in received DTO");
    }
  };

  const discardPile = currentPlayerGameSession?.discardPile || [];
  const topTemplate = discardPile.length > 0 ? discardPile[discardPile.length - 1] : null;



  return (
    <div className="game-container">
      {showFirstTurn && game?.players && !isSolitary && (
        <FirstTurnModal
          gameSessionId={id}
          players={game.players}
          actualWinnerId={game.gamePlayerTurnId}
          onFinish={() => {
            setShowFirstTurn(false);
            sessionStorage.setItem(`initiative_seen_${id}`, 'true');
          }}
        />
      )}
      
      {notification && (
        <div className="notification-toast">
          {notification}
        </div>
      )}

      {isGameOver && (
        <div className="game-over-overlay">
          <div className="game-over-modal">
            <h1>{winnerName && winnerName.includes(currentUser) ? "VICTORY!" : "GAME OVER"}</h1>
            {!isSolitary && (
              <div className="winner-announcement">
                {(() => {
                  if (game?.gameMode === "TEAMBATTLE" && game?.players) {
                    const winnerNames = winnerName ? winnerName.split(", ").map(n => n.trim()) : [];
                    const winnerPlayer = game.players.find(p => winnerNames.includes(p.player.username));
        
                    if (winnerPlayer && winnerPlayer.teamNumber != null) {
                      const winningTeamPlayers = game.players
                        .filter(p => p.teamNumber === winnerPlayer.teamNumber)
                        .map(p => p.player.username);
                      
                      const isPlayerInWinningTeam = winningTeamPlayers.includes(currentUser);
                      
                      if (isPlayerInWinningTeam) {
                        return `YOUR TEAM WON! (Team ${winnerPlayer.teamNumber})`;
                      } else {
                        return `Team ${winnerPlayer.teamNumber} wins! (${winningTeamPlayers.join(", ")})`;
                      }
                    }
                  }
                  // Default for non-TeamBattle modes
                  return winnerName && winnerName.includes(currentUser) ? "YOU WON!" : `Winner: ${winnerName}`;
                })()}
              </div>
            )}
            {isSolitary && (
              <div className="winner-score">
                {game.winnerScore != null ? `Score: ${game.winnerScore}` : "You lost!"}
              </div>
            )}
            <button
              className="leave-game-btn"
              onClick={() => {
                clearActiveLobbyId();
                navigate("/");
              }}
            >
              Back to Menu
            </button>
          </div>
        </div>
      )}

      {isEliminated && !isGameOver && (
        <div className="eliminated-banner">
          ⚠️ YOU HAVE BEEN ELIMINATED - SPECTATOR MODE ⚠️
        </div>
      )}

      <div className="side-column left">
        {game?.gameMode !== "SOLITAIRE" ? (
          <>
            {game?.gameMode === "TEAMBATTLE" && (
              <>
                <h3>Allies</h3>
                <div className="opponents-stack" style={{ marginBottom: '20px' }}>
                  {opponents.filter(p => p.teamNumber === currentPlayerGameSession?.teamNumber).map((pgs) => {
                    const isTurn = game?.gamePlayerTurnId === pgs.id;
                    return (
                      <OpponentCard key={`${pgs.id}-${game.round}`} pgs={pgs} isTurn={isTurn} />
                    );
                  })}
                  {opponents.filter(p => p.teamNumber === currentPlayerGameSession?.teamNumber).length === 0 && <div style={{ opacity: 0.5, fontStyle: 'italic' }}>No allies</div>}
                </div>
              </>
            )}

            <h3>Opponents</h3>
            <div className="opponents-stack">
              {opponents.filter(p => game.gameMode !== "TEAMBATTLE" || p.teamNumber !== currentPlayerGameSession?.teamNumber).map((pgs) => {
                const isTurn = game?.gamePlayerTurnId === pgs.id;
                return (
                  <OpponentCard key={`${pgs.id}-${game.round}`} pgs={pgs} isTurn={isTurn} />
                );
              })}
            </div>
          </>
        ) : (
          <div className="empty-state">
            <h3>Discard Pile</h3>
            <div>
              <DiscardPile
                gameId={id}
                jwt={jwt}
                topCard={topTemplate}
                onDrawSuccess={handleDrawSuccess}
                disabled={!topTemplate}
                playerColor={playerColor}
              />
            </div>
          </div>
        )}
      </div>


      <div className="center-column">
        <h2 className="game-title">{game?.name || "End of Line"}</h2>
        <GameBoardComponent
          gameSessionId={id}
          boardSize={game?.boardSize || 5}
          selectedCard={handCards.find(c => c.id === selectedCardId)}
          playerColor={playerColor}
          playerGameSessionId={currentPlayerGameSession?.id}
          playerTeamNumber={currentPlayerGameSession?.teamNumber}
          teamBySessionId={teamBySessionId}
          currentTurn={game?.gamePlayerTurnId}
          isMyTurn={isMyTurn}
          cardsPlaced={cardsPlaced}
          maxCards={maxCards}
          refreshKey={game}
          gameMode={game?.gameMode}
          activeEnergyEffect={currentPlayerGameSession?.activeEnergyEffect}
          onCardPlaced={() => {}}
        />
      </div>

      <div className="side-column hand-column">
        <div className="player-hand-vertical">
          {handLoading && <p>Loading...</p>}
          <div className="player-hand-vertical__row">
            <PlayerHand
              vertical={true}
              cards={handCards}
              selectedCardId={selectedCardId}
              onSelectCard={(card) => setSelectedCardId(card?.id ?? null)}
              playerColor={playerColor}
              onReroll={rerollHand}
              canReroll={canRerollHand}
              gameMode={game?.gameMode}
              showReroll={false}
            />
            <div className="player-hand-vertical__controls">
              {game?.gameMode !== 'SOLITAIRE' && game?.round === 1 && (
                <RerollButton onClick={rerollHand} disabled={!canRerollHand} />
              )}
              {currentPlayerGameSession && (
                <EnergyCard
                  playerColor={currentPlayerGameSession.playerColor}
                  energy={currentPlayerGameSession.energy}
                />
              )}
            </div>
          </div>
          <div style={{ marginTop: '10px', width: '150px' }}>
            <DiscardButton
              gameId={id}
              selectedCardId={selectedCardId}
              gameMode={game?.gameMode}
              onDiscardSuccess={(updatedGame) => {
                setGame(updatedGame);
                const me = updatedGame.players.find(p => p.player.username === currentUser);
                if (me) {
                  setHandCards(me.hand || []);
                }
                setSelectedCardId(null);
              }}
            />
          </div>
          {isSolitary && isHandEmpty &&
            <div className="solitaire-actions">
              <DrawDeckButton
                gameId={id}
                jwt={jwt}
                onDrawSuccess={handleDrawSuccess}
              />
            </div>
          }
        </div>
      </div>


      <div className="side-column right">
        <div className="right-panel__top">
          <button
            className="leave-game-btn"
            onClick={handleLeaveGame}
            disabled={leaving}
          >
            {leaving ? "Leaving..." : "LEAVE"}
          </button>
          <div className="round-pill">ROUND: {game.round}</div>
        </div>
        {!isSolitary && (
          <div className="right-panel__chat">
            {toogleChat && (
              <div className="chat-integrated">
                <ChatMessage id={id} currentUser={currentUser} />
              </div>
            )}
            <button
              className={`toggle-chat-button ${toogleChat ? "chat-open" : ""}`}
              onClick={() => setToogleChat((prev) => !prev)}
            >
              {toogleChat ? <IoRemove /> : <IoChatbubbleEllipsesOutline />}
            </button>
          </div>
        )}
        {!isEliminated && (
          <div className="right-panel__energy">
            <EnergyActionsMenu
              actions={
                game?.gameMode === "TEAMBATTLE"
                  ? ENERGY_ACTIONS
                  : ENERGY_ACTIONS.filter((a) => a.id !== "JUMP_LINE")
              }
              activeEffect={activeEnergyEffect}
              isLockedByRound={!isEnergyUnlocked}
              disabled={
                !currentPlayerGameSession ||
                remainingEnergy <= 0 ||
                hasUsedEnergyThisRound ||
                !isEnergyUnlocked ||
                (currentPlayerGameSession?.cardsPlacedThisRound ?? 0) > 0
              }
              remainingEnergy={remainingEnergy}
              onSelectAction={handleEnergyActionSelection}
              onClearEffect={resetEnergyEffect}
            />
            {energyError && <p className="energy-menu__error">{energyError}</p>}
          </div>
        )}
      </div>
    </div>
  );
};

export default GameBoard;
