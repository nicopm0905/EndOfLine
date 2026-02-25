import { useMemo, useState } from "react";

import tokenService from "../../services/token.service";
import useFetchState from "../../util/useFetchState";
import getErrorModal from "../../util/getErrorModal";
import "../statisticsList.css";

const jwt = tokenService.getLocalAccessToken();

export default function StatisticSocialPlayer() {
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

    const socialMetrics = useMemo(() => {
        if (!statistics) return [];
        
        return [
            {
                key: "messagesSent",
                label: "Messages Sent",
                value: statistics.messagesSent ?? 0,
                description: "Total chat messages",
                color: "#00c8ff",
            },
            {
                key: "friendsCount",
                label: "Friends Count",
                value: statistics.friendsCount ?? 0,
                description: "Active friendships",
                color: "#00ffbf",
            }
        ];
    }, [statistics]);

    return (
        <div className="statistics-wrapper">
            {modal}
            
            <div className="statistics-header">
                <h1 className="statistics-title">
                    Social Statistics - {playerUsername}
                </h1>
                <p className="statistics-subtitle">
                    Your social interactions
                </p>
            </div>

            <div className="statistics-cards">
                {socialMetrics.map((metric) => (
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