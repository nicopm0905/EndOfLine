import { useMemo, useState } from "react";
import { Link } from "react-router-dom";
import jwt_decode from "jwt-decode";

import tokenService from "../services/token.service";
import useFetchState from "../util/useFetchState";
import getErrorModal from "../util/getErrorModal";
import "./statisticsList.css";

const jwt = tokenService.getLocalAccessToken();

export default function StatisticsList() {
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);

    const user = tokenService.getUser();
    const playerId = user?.id ?? null;
    const playerUsername = user?.username ?? "Player";
    const userRoles = jwt ? jwt_decode(jwt).authorities : [];
    const isAdmin = userRoles.includes("ADMIN");

    const [statistics] = useFetchState(
        [],
        playerId ? `/api/v1/statistics/player/${playerId}` : null,
        jwt,
        setMessage,
        setVisible
    );

    const modal = getErrorModal(setVisible, visible, message);

    const rows = useMemo(() => {
        if (!statistics) return [];
        return Array.isArray(statistics) ? statistics : [statistics];
    }, [statistics]);

    const metricCards = useMemo(() => {
        const stat = rows[0] ?? {};
        return [
            {
                key: "gamesPlayed",
                label: "Games played",
                value: stat.gamesPlayed ?? 0,
                description: "Total matches completed",
                color: "#00c8ff",
            },
            {
                key: "victories",
                label: "Victories",
                value: stat.victories ?? 0,
                description: "Matches won",
                color: "#00ffbf",
            },
            {
                key: "maxLineLength",
                label: "Max line length",
                value: stat.maxLineLength ?? 0,
                description: "Longest path in any game",
                color: "#ffb347",
            },
            {
                key: "totalPlayTime",
                label: "Total play time",
                value: stat.totalPlayTime ?? 0,
                description: "Seconds spent playing",
                color: "#ff6ec7",
            },
            {
                key: "usedCards",
                label: "Used cards",
                value: stat.usedCards ?? 0,
                description: "Total cards placed across all games",
                color: "#ff477e",
            },
            {
                key: "averageDuration",
                label: "Avg duration",
                value: stat.averageDuration ?? 0,
                description: "Average match duration in seconds",
                color: "#b58cff",
            },
            {
                key: "powerMostUsed",
                label: "Top power",
                value: stat.powerMostUsed ?? "—",
                description: "Most frequently used power-up",
                color: "#ffe66d",
            },
            {
                key: 'higuestScorePuzzle',
                label: "Higuest Score Puzzle",
                value: stat.higuestScorePuzzle ?? 0,
                description: "Maximum score obtained in puzzle mode",
                color: "#f08a76ff",

            },
            {
                key: 'completedPuzzle',
                label: "Completed Puzzle",
                value: stat.completedPuzzle ?? 0,
                description: "Total number of puzzles completed",
                color: "#5cff95ff",

            }
        ];
    }, [rows]);

    return (
        <div className="statistics-wrapper">
            {modal}
            <div className="statistics-cards">
                    {metricCards.map((metric) => (
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
                                        ? metric.value
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