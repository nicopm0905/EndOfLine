import { useMemo, useState } from "react";
import jwt_decode from "jwt-decode";

import tokenService from "../../services/token.service";
import useFetchState from "../../util/useFetchState";
import getErrorModal from "../../util/getErrorModal";
import "../statisticsList.css";

const jwt = tokenService.getLocalAccessToken();

export default function StatisticGeneralPlayer() {
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);

    const user = tokenService.getUser();
    const playerId = user?.id ?? null;
    const playerUsername = user?.username ?? "Player";

    const [statistics] = useFetchState(
        {},
        `/api/v1/statistics/player/${playerId}`,
        jwt,
        setMessage,
        setVisible
    );

    const modal = getErrorModal(setVisible, visible, message);

    const generalMetrics = useMemo(() => {
        if (!statistics) return [];
        return [
            {
                key: "gamesPlayed",
                label: "Games Played",
                value: statistics.gamesPlayed ?? 0,
                description: "Total matches completed",
                color: "#00c8ff",
            },
            {
                key: "victories",
                label: "Victories",
                value: statistics.victories ?? 0,
                description: "Matches won",
                color: "#00ffbf",
            },
            {
                key: "defeats",
                label: "Defeats",
                value: statistics.defeats ?? 0,
                description: "Matches lost",
                color: "#ff6b6b",
            },
            {
                key: "winRate",
                label: "Win Rate",
                value: statistics.gamesPlayed > 0 
                    ? `${((statistics.victories / statistics.gamesPlayed) * 100).toFixed(1)}%`
                    : "0%",
                description: "Victory percentage",
                color: "#ffd700",
            },
            {
                key: "totalPlayTime",
                label: "Total Play Time",
                value: statistics.totalPlayTime ?? 0,
                description: "Total seconds played",
                color: "#ff6ec7",
            },
            {
                key: "averageDuration",
                label: "Avg Duration",
                value: statistics.gamesPlayed > 0
                    ? Math.round(statistics.totalPlayTime / statistics.gamesPlayed)
                    : 0,
                description: "Average match duration (seconds)",
                color: "#b58cff",
            },
            {
                key: "shortestGame",
                label: "Shortest Game",
                value: statistics.shortestGame ?? 0,
                description: "Fastest match completed",
                color: "#4ecdc4",
            },
            {
                key: "longestGame",
                label: "Longest Game",
                value: statistics.longestGame ?? 0,
                description: "Longest match played",
                color: "#95e1d3",
            },
            {
                key: "totalScore",
                label: "Total Score",
                value: statistics.totalScore ?? 0,
                description: "Accumulated points",
                color: "#ffb347",
            },
            {
                key: "highestScore",
                label: "Highest Score",
                value: statistics.highestScore ?? 0,
                description: "Best single match score",
                color: "#ffe66d",
            },
            {
                key: "lowestScore",
                label: "Lowest Score",
                value: statistics.lowestScore ?? 0,
                description: "Worst single match score",
                color: "#a8dadc",
            },
            {
                key: "averageScore",
                label: "Average Score",
                value: statistics.gamesPlayed > 0
                    ? Math.round(statistics.totalScore / statistics.gamesPlayed)
                    : 0,
                description: "Mean score per match",
                color: "#f08a76",
            }
        ];
    }, [statistics]);

    return (
        <div className="statistics-wrapper">
            {modal}
            <div className="statistics-header">
                <h1 className="statistics-title">
                    General Statistics - {playerUsername}
                </h1>
                <p className="statistics-subtitle">
                    Your general metrics
                </p>
            </div>

            <div className="statistics-cards">
                {generalMetrics.map((metric) => (
                    <div
                        key={metric.key}
                        className="stat-card"
                        style={{ "--clr": metric.color }}
                        role="button"
                        tabIndex={0}
                    >
                        <div className="circle" aria-hidden="true">
                            <div className="circle-label">{metric.label}</div>
                            <div className="circle-value">
                                {typeof metric.value === "number"
                                    ? metric.value.toLocaleString()
                                    : metric.value}
                            </div>
                        </div>
                        <p>{metric.description}</p>
                    </div>
                ))}
            </div>
        </div>
    );
}