import { useMemo, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import jwt_decode from "jwt-decode";

import tokenService from "../../services/token.service";
import useFetchState from "../../util/useFetchState";
import getErrorModal from "../../util/getErrorModal";
import "./rankingDetail.css";

const jwt = tokenService.getLocalAccessToken();
const METRIC_MAP = {
    'victories': 'VICTORIES',
    'games_played': 'GAMES_PLAYED',
    'defeats': 'DEFEATS',
    'winrate': 'WIN_RATE',
    'total_play_time': 'TOTAL_PLAY_TIME',
    'average_duration': 'AVERAGE_DURATION',
    'shortest_game': 'SHORTEST_GAME',
    'longest_game': 'LONGEST_GAME',
    'totalScore': 'TOTAL_SCORE',
    'highest_score': 'HIGHEST_SCORE',
    'lowest_score': 'LOWEST_SCORE',
    'average_score': 'AVERAGE_SCORE',
    'total_cards_used': 'TOTAL_CARDS_USED',
    'power_cards_used': 'POWER_CARDS_USED',
    'average_cards_per_game': 'AVERAGE_CARDS_PER_GAME',
    'max_line_length': 'MAX_LINE_LENGTH',
    'total_lines_completed': 'TOTAL_LINES_COMPLETED',
    'average_line_length': 'AVERAGE_LINE_LENGTH',
    'completed_puzzles': 'COMPLETED_PUZZLES',
    'highest_score_puzzle': 'HIGHEST_SCORE_PUZZLE',
    'total_puzzle_score': 'TOTAL_PUZZLE_SCORE',
    'average_puzzle_score': 'AVERAGE_PUZZLE_SCORE',
    'messages_sent': 'MESSAGES_SENT',
    'friends_count': 'FRIENDS_COUNT',
};


export default function RankingDetailPage() {
    const { metric } = useParams();
    const navigate = useNavigate();
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);

    const user = tokenService.getUser();
    const currentPlayerId = user?.id ?? null;
    const enumMetric = METRIC_MAP[metric]

    const [ranking] = useFetchState(
        [],
        `/api/v1/statistics/ranking?metric=${enumMetric}&limit=100`,
        jwt,
        setMessage,
        setVisible
    );

    const modal = getErrorModal(setVisible, visible, message);

    const metricConfig = useMemo(() => {
        const configs = {
            'victories': { title: "Victories Ranking", icon: "🥇", color: "#ffd700", description: "Players ranked by total victories", suffix: "" },
            games_played: { title: "Games Played Ranking", icon: "🎮", color: "#00c8ff", description: "Players ranked by matches completed", suffix: "" },
            defeats: { title: "Defeats Ranking", icon: "💔", color: "#ff6b6b", description: "Players ranked by matches lost", suffix: "" },
            winrate: { title: "Win Rate Ranking", icon: "📈", color: "#00ffbf", description: "Players ranked by win percentage", suffix: "%" },
            total_play_time: { title: "Play Time Ranking", icon: "⌛", color: "#b58cff", description: "Players ranked by time played", suffix: "s" },
            average_duration: { title: "Average Duration Ranking", icon: "🕒", color: "#95e1d3", description: "Players ranked by average match duration", suffix: "s" },
            shortest_game: { title: "Shortest Game Ranking", icon: "⚡", color: "#4ecdc4", description: "Players ranked by fastest match", suffix: "s" },
            longest_game: { title: "Longest Game Ranking", icon: "🐢", color: "#95e1d3", description: "Players ranked by longest match", suffix: "s" },
            totalScore: { title: "Total Score Ranking", icon: "⭐", color: "#ff6ec7", description: "Players ranked by accumulated points", suffix: "" },
            highest_score: { title: "Highest Score Ranking", icon: "🌟", color: "#ffe66d", description: "Players ranked by best single match score", suffix: "" },
            lowest_score: { title: "Lowest Score Ranking", icon: "⬇️", color: "#a8dadc", description: "Players ranked by worst single match score", suffix: "" },
            average_score: { title: "Average Score Ranking", icon: "📊", color: "#f08a76", description: "Players ranked by mean score per match", suffix: "" },
            total_cards_used: { title: "Total Cards Used Ranking", icon: "🃏", color: "#ff6ec7", description: "Players ranked by cards played", suffix: "" },
            power_cards_used: { title: "Power Cards Used Ranking", icon: "💥", color: "#ffd700", description: "Players ranked by special cards", suffix: "" },
            average_cards_per_game: { title: "Average Cards Per Game Ranking", icon: "🎴", color: "#00ffbf", description: "Players ranked by cards per match", suffix: "" },
            max_line_length: { title: "Max Line Length Ranking", icon: "📏", color: "#00c8ff", description: "Players ranked by longest line", suffix: "" },
            total_lines_completed: { title: "Total Lines Completed Ranking", icon: "✅", color: "#4ecdc4", description: "Players ranked by lines finished", suffix: "" },
            average_line_length: { title: "Average Line Length Ranking", icon: "📐", color: "#95e1d3", description: "Players ranked by mean line size", suffix: "" },
            completed_puzzles: { title: "Completed Puzzles Ranking", icon: "🧩", color: "#ffb347", description: "Players ranked by puzzles solved", suffix: "" },
            highest_score_puzzle: { title: "Highest Score Puzzle Ranking", icon: "🏆", color: "#ffd700", description: "Players ranked by best puzzle score", suffix: "" },
            total_puzzle_score: { title: "Total Puzzle Score Ranking", icon: "�", color: "#ff6ec7", description: "Players ranked by puzzle points", suffix: "" },
            average_puzzle_score: { title: "Average Puzzle Score Ranking", icon: "🎪", color: "#00ffbf", description: "Players ranked by mean puzzle score", suffix: "" },
            messages_sent: { title: "Messages Sent Ranking", icon: "💬", color: "#b58cff", description: "Players ranked by chat messages", suffix: "" },
            friends_count: { title: "Friends Count Ranking", icon: "👥", color: "#00c8ff", description: "Players ranked by total friends", suffix: "" }
        };
        
        return configs[metric] || {
            title: `${metric} Ranking`,
            icon: "🏆",
            color: "#00c8ff",
            description: "Global player ranking",
            suffix: ""
        };
    }, [metric]);

    const players = useMemo(() => {
        return Array.isArray(ranking) ? ranking : [];
    }, [ranking]);

    return (
        <div className="ranking-detail-wrapper">
            {modal}

            <div className="ranking-detail-header">
                <button 
                    className="back-button"
                    onClick={() => navigate('/statistics')}
                >
                    ←
                </button>
                
                <div className="ranking-title-section">
                    <span className="ranking-title-icon" style={{ color: metricConfig.color }}>
                        {metricConfig.icon}
                    </span>
                    <h1 className="ranking-title">{metricConfig.title}</h1>
                </div>
                
                <p className="ranking-description">{metricConfig.description}</p>
                <p className="ranking-count">Total Players: {players.length}</p>
            </div>

            <div className="ranking-table-container">
                {players.length > 0 ? (
                    <table className="ranking-table">
                        <thead>
                            <tr>
                                <th className="rank-col">Rank</th>
                                <th className="player-col">Player</th>
                                <th className="value-col">
                                    {metricConfig.title.replace(' Ranking', '')}
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                            {players.map((player, index) => (
                                <tr 
                                    key={player.playerId || index}
                                    className={player.playerId === currentPlayerId ? "current-player-row" : ""}
                                >
                                    <td className="rank-col">
                                        <span 
                                            className="rank-badge"
                                            style={{ 
                                                backgroundColor: index < 3 ? metricConfig.color : 'rgba(0, 200, 255, 0.2)' 
                                            }}
                                        >
                                            #{index + 1}
                                        </span>
                                    </td>
                                    <td className="player-col">
                                        <div className="player-info">
                                            <span className="player-username">
                                                {player.username || "Unknown"}
                                            </span>
                                            {player.playerId === currentPlayerId && (
                                                <span className="you-badge">YOU</span>
                                            )}
                                        </div>
                                    </td>
                                    <td className="value-col">
                                        <span 
                                            className="value-text"
                                            style={{ color: metricConfig.color }}
                                        >
                                            {typeof player.value === 'number'
                                                ? metric === 'winrate'
                                                    ? `${player.value.toFixed(1)}%`
                                                    : player.value.toLocaleString()
                                                : player.value}
                                            {metricConfig.suffix && metric !== 'winrate' && metricConfig.suffix}
                                        </span>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                ) : (
                    <div className="no-data-message">
                        <p>No ranking data available</p>
                    </div>
                )}
            </div>
        </div>
    );
}