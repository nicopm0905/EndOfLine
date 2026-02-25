import { useMemo, useState } from "react";
import { Link } from "react-router-dom";
import jwt_decode from "jwt-decode";

import tokenService from '../services/token.service';
import useFetchState from "../util/useFetchState";
import getErrorModal from "../util/getErrorModal";
import "./StatisticsGeneral.css"
import {CategoryButton} from "./component/CategoryButton"
import {RankingCard} from "./component/RankingCard"
import {SearchMetricModal} from "./component/SearchMetricModal"

const jwt = tokenService.getLocalAccessToken();

export default function StatisticGeneral() {
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [isSearchModalOpen, setIsSearchModalOpen] = useState(false);

    const user = tokenService.getUser();
    const playerId = user.id ?? null;
    const playerUsername = user.username ;
    const userRoles = jwt ? jwt_decode(jwt).authorities : [];
    const isAdmin = userRoles.includes("ADMIN");

    const [top5Victories] = useFetchState(
        [],
        `/api/v1/statistics/ranking?metric=VICTORIES&limit=5`,  
        jwt,
        setMessage,
        setVisible
    )

    const [top5Duration] = useFetchState(
        [],
        `/api/v1/statistics/ranking?metric=TOTAL_PLAY_TIME&limit=5`, 
        jwt,
        setMessage,
        setVisible
    )

    const [top5Score] = useFetchState(
        [],
        `/api/v1/statistics/ranking?metric=TOTAL_SCORE&limit=5`,  
        jwt,
        setMessage,
        setVisible
    )

    const [myStats] = useFetchState(
        [],
        `/api/v1/statistics/player/${playerId}`,
        jwt,
        setMessage,
        setVisible
    );

    const modal = getErrorModal(setVisible, visible, message)

    const playerSummary = useMemo(() => {
        if (!myStats) return { gamesPlayed: 0, victories: 0, winRate: 0 };
        return {
            gamesPlayed: myStats.gamesPlayed ?? 0,
            victories: myStats.victories ?? 0,
            winRate: myStats.gamesPlayed > 0 
                ? ((myStats.victories / myStats.gamesPlayed) * 100).toFixed(1)
                : 0
        };
    }, [myStats]);

    return (
        <div className="dashboard-wrapper">
            {modal}
            
            <button 
                className="floating-search-button"
                onClick={() => setIsSearchModalOpen(true)}
                title="Search All Rankings"
            >
                <span className="search-icon">🔍</span>
                <span className="search-text">Search Rankings</span>
            </button>

           <SearchMetricModal 
                isOpen={isSearchModalOpen}
                onClose={() => setIsSearchModalOpen(false)}
            />

            <div className="dashboard-header">
                <h1 className="welcome-title">
                    <span className="username-highlight">{playerUsername}</span>
                </h1>
                <p className="player-summary">
                    {playerSummary.gamesPlayed} games {      }
                    
                    {playerSummary.victories} victories {      }
                    
                    {playerSummary.winRate}% win rate 
                </p>
            </div>

            <div className="rankings-section">
                <h2 className="section-title">🏆 Global Rankings</h2>
                <div className="ranking-cards">
                    <RankingCard 
                        title="Top Victories"
                        metric="victories"
                        icon="🥇"
                        color="#ffd700"
                        topPlayers={top5Victories}
                        currentPlayerId={playerId}
                    />
                    <RankingCard 
                        title="Total Play Time"
                        metric="total_play_time"
                        icon="⌛️"
                        color="#00ffbf"
                        topPlayers={top5Duration}
                        currentPlayerId={playerId}
                    />
                    <RankingCard 
                        title="Total Score"
                        metric="totalScore"
                        icon="⭐"
                        color="#ff6ec7"
                        topPlayers={top5Score}
                        currentPlayerId={playerId}
                    />
                </div>
            </div>

            <div className="categories-section">
                <h2 className="section-title">📊 Your Statistics</h2>
                <div className="category-grid">
                    <CategoryButton 
                        to="/statistics/general"
                        icon="🎮"
                        title="General"
                        subtitle="Core game metrics"
                        color="#00c8ff"
                    />
                    <CategoryButton 
                        to="/statistics/gameplay"
                        icon="🎯"
                        title="Gameplay"
                        subtitle="Performance stats"
                        color="#00ffbf"
                    />
                    <CategoryButton 
                        to="/statistics/puzzle"
                        icon="🧩"
                        title="Puzzle"
                        subtitle="Puzzle achievements"
                        color="#ffb347"
                    />
                    <CategoryButton 
                        to="/statistics/social"
                        icon="👥"
                        title="Social"
                        subtitle="Friends & interactions"
                        color="#b58cff"
                    />
                </div>
            </div>
        </div>
    );
}