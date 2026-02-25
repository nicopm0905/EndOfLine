import { useMemo, useState } from "react";

import tokenService from "../../services/token.service";
import useFetchState from "../../util/useFetchState";
import getErrorModal from "../../util/getErrorModal";
import "../statisticsList.css";

const jwt = tokenService.getLocalAccessToken();

export default function StatisticGameplayPlayer() {
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

    const gameplayMetrics = useMemo(() => {
        if (!statistics) return [];
        
        return [
            {
                key: "totalCardsUsed",
                label: "Total Cards Used",
                value: statistics.totalCardsUsed ?? 0,
                description: "Cards played across all games",
                color: "#00c8ff",
            },
            {
                key: "powerCardsUsed",
                label: "Power Cards Used",
                value: statistics.powerCardsUsed ?? 0,
                description: "Special cards activated",
                color: "#ffd700",
            },
            {
                key: "averageCardsPerGame",
                label: "Avg Cards/Game",
                value: statistics.gamesPlayed > 0
                    ? Math.round(statistics.totalCardsUsed / statistics.gamesPlayed)
                    : 0,
                description: "Average cards played per match",
                color: "#00ffbf",
            },
            {
                key: "maxLineLength",
                label: "Max Line Length",
                value: statistics.maxLineLength ?? 0,
                description: "Longest continuous path",
                color: "#ff6ec7",
            },
            {
                key: "totalLinesCompleted",
                label: "Lines Completed",
                value: statistics.totalLinesCompleted ?? 0,
                description: "Total lines finished",
                color: "#ffb347",
            },
            {
                key: "averageLineLength",
                label: "Avg Line Length",
                value: statistics.averageLineLength ?? 0,
                description: "Average path length",
                color: "#b58cff",
            }
        ];
    }, [statistics]);

    return (
        <div className="statistics-wrapper">
            {modal}
            
            <div className="statistics-header">
                <h1 className="statistics-title">
                    Gameplay Statistics - {playerUsername}
                </h1>
                <p className="statistics-subtitle">
                    Your performance in game
                </p>
            </div>

            <div className="statistics-cards">
                {gameplayMetrics.map((metric) => (
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