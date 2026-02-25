import tokenService from "../services/token.service";
import jwt_decode from "jwt-decode";
import useFetchState from "../util/useFetchState";
import getIdFromUrl from "./../util/getIdFromUrl";
import getErrorModal from "./../util/getErrorModal";
import { useState } from "react";
import useIntervalFetchState from "../util/useIntervalFetchState";
import { useNavigate } from "react-router-dom";
import { useEffect } from "react";
import "./lobby.css";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";
import InviteFriendModal from "../invitation/InviteFriendModal";
import { clearActiveLobbyId, setActiveLobbyId } from "../util/activeLobby";



export default function Lobby() {
    const id = getIdFromUrl(2);
    const emptyGame = {
        name: "",
        password: "",
        host: "",
        gameMode: "SOLITAIRE",
        winner: "",
        numPlayers: 1,
        isPrivate: false,
        players: [],
        boardSize: null
    };

    const jwt = tokenService.getLocalAccessToken();
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [game, setGame] = useState(emptyGame);
    const [openInviteModal, setOpenInviteModal] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        setActiveLobbyId(id);
        const redirectToList = () => {
            clearActiveLobbyId();
            navigate('/gameList/joinGame');
        };

        const socket = new SockJS("http://localhost:8080/ws");
        const stompClient = Stomp.over(socket);

        stompClient.connect({}, () => {
            stompClient.subscribe(`/topic/lobby/${id}`, (message) => {
                try {
                    if (message.body) {
                        const dto = JSON.parse(message.body);

                        if (dto && dto.deleted) {
                            setMessage('Game deleted by host. Returning...');
                            setVisible(true);
                            clearActiveLobbyId();
                            setTimeout(() => {
                                redirectToList();
                            }, 1000);
                            return;
                        }

                        setGame(dto);
                    }
                } catch (err) {
                    console.error("[WS] Failed to parse lobby message", err);
                    redirectToList();
                }
            });
        });

        return () => stompClient.disconnect();
    }, [id, navigate]);

    useEffect(() => {
        fetch(`/api/v1/gameList/${id}`, { headers: { Authorization: `Bearer ${jwt}` } })
            .then(res => {
                if (!res.ok) {
                    clearActiveLobbyId();
                    navigate('/gameList/joinGame');
                    return null;
                }
                return res.json();
            })
            .then(data => {
                if (data) {
                    setGame(data);
                }
            })
            .catch(() => {
                clearActiveLobbyId();
                navigate('/gameList/joinGame');
            });
    }, [id, jwt, navigate]);

    const modal = getErrorModal(setVisible, visible, message);




    useEffect(() => {
        if (visible && message && message.toLowerCase().includes('delete')) {
            setTimeout(() => {
                clearActiveLobbyId();
                navigate('/gameList/joinGame');
            }, 1200);
        }
    }, [visible, message, navigate]);


    useEffect(() => {
        if (!game) return;

        const gameState = game.state || game.status || game.gameState || null;
        if (gameState === "ACTIVE" || gameState === "STARTED") {
            console.log(`[Lobby] Game ${id} moved to ${gameState}. Redirecting to play view...`);
            navigate(`/gameList/${id}/play`, { state: { gameStartData: game } });
        }
    }, [game, id, navigate]);

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!game.id) {
            setMessage('Error: game ID not available');
            setVisible(true);
            return;
        }

        const requestBody = {
            gameMode: game.gameMode
        };

        try {
            const response = await fetch(`/api/v1/gameList/${id}/start`, {
                method: 'POST',
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    Accept: "application/json",
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(requestBody)
            });

            if (response.ok) {
                const gameStartData = await response.json();
                setMessage('Game started successfully. Redirecting...');

                setTimeout(() => {
                    navigate(`/gameList/${id}/play`, { state: { gameStartData } });
                }, 1000);
            } else {
                const errorData = await response.json();
                setMessage(`Error starting game: ${errorData.message || 'Unknown error'}`);
                setVisible(true);
            }
        } catch (error) {
            console.error('Error starting game:', error);
            setMessage('Error starting game');
            setVisible(true);
        }
    };


    const handleLeave = async (e) => {
        e.preventDefault();

        if (!game.id) {
            setMessage('Error: game ID not available');
            return;
        }
        if (game.host === tokenService.getUser().username) {
            try {
                const response = await fetch(`/api/v1/gameList/${game.id}`, {
                    method: 'DELETE',
                    headers: {
                        Authorization: `Bearer ${jwt}`,
                        Accept: "application/json",
                        "Content-Type": "application/json"
                    },
                });

                if (response.ok) {
                    clearActiveLobbyId();
                    setMessage('Game deleted succesfuly. Redirecting...');
                    setTimeout(() => {
                        window.location.href = '/gameList/createGame';
                    }, 1500);
                } else {
                    const errorData = await response.json();
                    setMessage(`Error deleting game: ${errorData.message || 'Unknown error'}`);
                }
            } catch (error) {
                console.error('Error deleting game:', error);
                setMessage('Error deleting game');
            }
        } else {
            try {
                const response = await fetch(`/api/v1/gameList/${game.id}/leave`, {
                    method: 'POST',
                    headers: {
                        Authorization: `Bearer ${jwt}`,
                        Accept: "application/json",
                        "Content-Type": "application/json"
                    },
                });

                if (response.ok) {
                    clearActiveLobbyId();
                    setMessage('Game deleted succesfuly. Redirecting...');
                    setTimeout(() => {
                        window.location.href = '/gameList/joinGame';
                    }, 1500);
                } else {
                    const errorData = await response.json();
                    setMessage(`Error leaving game: ${errorData.message || 'Unknown error'}`);
                }
            } catch (error) {
                console.error('Error leaving game:', error);
                setMessage('Error leaving game');
            }
        }
    };


    function handleInvite() {
        setOpenInviteModal(true);
    }

    const handleSwitchTeam = async (newTeam) => {
        try {
            const response = await fetch(`/api/v1/gameList/${id}/switchTeam`, {
                method: 'POST',
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    Accept: "application/json",
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ teamNumber: newTeam })
            });
            if (!response.ok) {
                const errorData = await response.json();
                setMessage(`Error switching team: ${errorData.message || 'Unknown error'}`);
                setVisible(true);
            }
        } catch (error) {
            console.error("Error switching team", error);
            setMessage('Error switching team');
            setVisible(true);
        }
    }

    const players = Array.isArray(game.players)
        ? [...game.players].sort((a, b) => {
            const aKey = (a?.id ?? a?.player?.id ?? 0);
            const bKey = (b?.id ?? b?.player?.id ?? 0);
            if (aKey !== bKey) return aKey - bKey;
            const aName = (a?.player?.username ?? "").toString();
            const bName = (b?.player?.username ?? "").toString();
            return aName.localeCompare(bName);
        }) : [];

    const maxSlots = typeof game.numPlayers === "number" ? game.numPlayers : parseInt(game.numPlayers);

    const renderPlayerList = (playersToRender, capacity, isTeam = false) => {
        const currentSlots = Array.from({ length: Math.max(capacity, playersToRender.length) }, (_, i) => {
            return playersToRender[i] ?? null;
        });

        return (
            <div className="player-list" style={isTeam ? { minHeight: '150px' } : {}}>
                {currentSlots.map((session, idx) => {
                    const playerData = session ? session.player : null;

                    return (
                        <div key={idx} className="participant-row">
                            <div className={`participant-avatar ${playerData ? "filled" : "empty"}`}>
                                {playerData ? (
                                    playerData.avatarId ? (
                                        <img
                                            src={`/avatars/avatar${playerData.avatarId}.png`}
                                            alt={playerData.username}
                                            onError={(e) => {
                                                e.target.onerror = null;
                                                e.target.src = "/avatars/default.png";
                                            }}
                                            style={{
                                                width: "100%",
                                                height: "100%",
                                                borderRadius: "50%",
                                                border: `3px solid ${session.playerColor || '#FFF'}`
                                            }}
                                        />
                                    ) : (
                                        playerData.username ? playerData.username.charAt(0).toUpperCase() : '?'
                                    )
                                ) : (
                                    ""
                                )}
                            </div>
                            <div className="participant-name">
                                {playerData ? playerData.username : "WAITING"}
                            </div>
                            {!playerData && <div className="participant-spinner" aria-hidden="true"></div>}
                        </div>
                    );
                })}
            </div>
        );
    };

    const isTeamBattle = game.gameMode === "TEAMBATTLE";
    const team1Players = players.filter(p => p.teamNumber === 1);
    const team2Players = players.filter(p => p.teamNumber === 2);
    const maxTeamSize = Math.ceil(maxSlots / 2);

    return (
        <div className="lobby">
            <div className="lobby-card">
                <h2>{game.name ? game.name.toUpperCase() : `GAME ${id}`}</h2>

                <div className="lobby-info">
                    <div>game mode: <strong>{game.gameMode ?? "Team Battle"}</strong></div>
                    <div>players: <strong>{players.length}/{maxSlots}</strong></div>
                    <div>game password: <span className="mono">{game.password ? game.password : "----"}</span></div>
                    <div>host: <strong>{game.host ?? "-"}</strong></div>
                </div>

                {isTeamBattle ? (
                    <div style={{ display: 'flex', justifyContent: 'space-between', gap: '2rem', width: '100%' }}>
                        <div style={{ flex: 1 }}>
                            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '10px' }}>
                                <h3 style={{ margin: 0 }}>TEAM 1</h3>
                                <button className="btn btn-invite" style={{ padding: '5px 10px', fontSize: '0.8em', width: 'auto', backgroundColor: '#595959ff' }} onClick={() => handleSwitchTeam(1)}>JOIN</button>
                            </div>
                            {renderPlayerList(team1Players, maxTeamSize, true)}
                        </div>
                        <div style={{ flex: 1 }}>
                            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '10px' }}>
                                <h3 style={{ margin: 0 }}>TEAM 2</h3>
                                <button className="btn btn-invite" style={{ padding: '5px 10px', fontSize: '0.8em', width: 'auto', backgroundColor: '#595959ff' }} onClick={() => handleSwitchTeam(2)}>JOIN</button>
                            </div>
                            {renderPlayerList(team2Players, maxTeamSize, true)}
                        </div>
                    </div>
                ) : (
                    renderPlayerList(players, maxSlots)
                )}

                <div className="lobby-buttons">
                    <button className="btn btn-leave" onClick={handleLeave}>LEAVE GAME</button>
                    <button className="btn btn-invite" onClick={handleInvite}>INVITE FRIEND</button>
                    <button className="btn btn-invite" onClick={handleSubmit} disabled={!game || tokenService.getUser()?.username !== game.host}>START GAME</button>
                </div>

                {modal}
            </div>

            <InviteFriendModal
                open={openInviteModal}
                onClose={() => setOpenInviteModal(false)}
                gameId={game.id}
            />
        </div>
    )
}
