import React, { useMemo, useState } from 'react';
import '../gameList/joinGame/joinGame.css';
import './spectate.css';
import tokenService from "../services/token.service";
import getErrorModal from "../util/getErrorModal";
import useIntervalFetchState from '../util/useIntervalFetchState';
import { useNavigate } from 'react-router-dom';

const MODES_ORDER = ["SOLITAIRE", "SOLITARY_PUZZLE", "VERSUS", "BATTLE_ROYALE", "TEAMBATTLE"];

const SpectateView = () => {
  const jwt = tokenService.getLocalAccessToken();
  const navigate = useNavigate();
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);

  const [games] = useIntervalFetchState(
    [],
    `/api/v1/gameList/active/friends`,
    jwt,
    setMessage,
    setVisible,
    null,
    3000
  );

  const modal = getErrorModal(setVisible, visible, message);

  const grouped = useMemo(() => {
    return games.reduce((acc, g) => {
      const mode = g.gameMode || 'UNKNOWN';
      if (!acc[mode]) acc[mode] = [];
      acc[mode].push(g);
      return acc;
    }, {});
  }, [games]);

  const modeKeys = useMemo(() => {
    const dynamic = Object.keys(grouped);
    const ordered = MODES_ORDER.filter(m => dynamic.includes(m));
    const rest = dynamic.filter(m => !MODES_ORDER.includes(m));
    return [...ordered, ...rest];
  }, [grouped]);

  return (
    <div className="join-game-container">
      <h2>Friends' games in course</h2>
      <p className="spectate-note">
        To be able to spectate a game, you must be friends with all the players participating in it.
      </p>
      {modeKeys.length === 0 ? (
        <p className="empty-msg">No active games to spectate.</p>
      ) : (
        modeKeys.map(mode => (
          <div key={mode} className="mode-section">
            <h3>{mode}</h3>
            <div className="game-list">
              {grouped[mode].map(game => (
                <div className="game-card" key={game.id}>
                  <div className="game-info">
                    <div className="game-name">{game.name}</div>
                    <div className="game-details">
                      Host: <span>{game.host}</span> &nbsp;|&nbsp;
                      Players: <span>{game.players?.length ?? 0}/{game.numPlayers}</span>
                    </div>
                  </div>
                  <button
                    className="join-button"
                    onClick={() => navigate(`/spectate/${game.id}`)}
                  >
                    Spectate
                  </button>
                </div>
              ))}
            </div>
          </div>
        ))
      )}
      {modal}
    </div>
  );
};

export default SpectateView;
