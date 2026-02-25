import { useMemo } from "react";
import { Link } from "react-router-dom";

export function RankingCard({ title, metric, icon, color, topPlayers, currentPlayerId }) {
    const players = useMemo(() => {
        return Array.isArray(topPlayers) ? topPlayers : [];
    }, [topPlayers]);

    return (
        <div className="ranking-card" style={{ "--clr": color }}>
            <div className="ranking-header">
                <span className="ranking-icon">{icon}</span>
                <h3>{title}</h3>
            </div>
            
            <ol className="ranking-list">
                {players.length > 0 ? (
                    players.map((player, index) => (
                        <li 
                            key={player.playerId || index}
                            className={player.playerId === currentPlayerId ? "current-player" : ""}
                        >
                            <span className="rank-position">#{index + 1}</span>
                            <span className="rank-username">{player.username || "Unknown"}</span>
                            <span className="rank-value">
                                {metric === 'winrate' 
                                    ? `${player.value.toFixed(1)}%` 
                                    : player.value}
                            </span>
                        </li>
                    ))
                ) : (
                    <li className="no-data">No data available</li>
                )}
            </ol>

            <Link to={`/statistics/ranking/${metric}`} className="view-all-btn">
                View all rankings →
            </Link>
        </div>
    );
}