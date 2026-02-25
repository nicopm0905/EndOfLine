import { useMemo, useState } from "react";

import tokenService from "../../services/token.service";
import useFetchState from "../../util/useFetchState";
import getErrorModal from "../../util/getErrorModal";
import "../statisticsList.css";

const jwt = tokenService.getLocalAccessToken();

export default function StatisticPuzzlePlayer() {
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

    const puzzleMetrics = useMemo(() => {
        if (!statistics) return [];
        
        return [
            {
                key: "completedPuzzles",
                label: "Completed Puzzles",
                value: statistics.completedPuzzles ?? 0,
                description: "Total puzzles solved",
                color: "#00c8ff",
            },
            {
                key: "highestScorePuzzle",
                label: "Highest Puzzle Score",
                value: statistics.highestScorePuzzle ?? 0,
                description: "Best puzzle performance",
                color: "#ffd700",
            },
            {
                key: "totalPuzzleScore",
                label: "Total Puzzle Score",
                value: statistics.totalPuzzleScore ?? 0,
                description: "Accumulated puzzle points",
                color: "#ffb347",
            },
            {
                key: "averagePuzzleScore",
                label: "Avg Puzzle Score",
                value: statistics.completedPuzzles > 0
                    ? Math.round(statistics.totalPuzzleScore / statistics.completedPuzzles)
                    : 0,
                description: "Average score per puzzle",
                color: "#ff6ec7",
            }
        ];
    }, [statistics]);

    return (
        <div className="statistics-wrapper">
            {modal}
            
            <div className="statistics-header">
                <h1 className="statistics-title">
                    Puzzle Statistics - {playerUsername}
                </h1>
                <p className="statistics-subtitle">
                    Your puzzle achievements
                </p>
            </div>

            <div className="statistics-cards">
                {puzzleMetrics.map((metric) => (
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