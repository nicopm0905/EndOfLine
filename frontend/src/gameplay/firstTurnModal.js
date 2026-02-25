import React, { useState, useEffect } from 'react';
import './FirstTurnModal.css';
import tokenService from "../services/token.service"; 

const COLOR_PREFIX = {
    RED: 'CR', BLUE: 'CB', GREEN: 'CG', YELLOW: 'CY',
    ORANGE: 'CO', PURPLE: 'CM', CYAN: 'CW', PINK: 'CV'
};

const FirstTurnModal = ({ gameSessionId, players, onFinish, actualWinnerId }) => {
    const [cardsData, setCardsData] = useState({});
    const [isFlipped, setIsFlipped] = useState(false);
    const [loading, setLoading] = useState(true);

    const jwt = tokenService.getLocalAccessToken();

    useEffect(() => {
        let mounted = true;

        const fetchInitiatives = async () => {
            try {
                const promises = players.map(async (pgs) => {
                    const username = pgs.player?.username;
                    const playerId = pgs.id;
                    
                    const response = await fetch(`/api/v1/gameList/${gameSessionId}/player/${username}/initiative`, {
                        headers: { Authorization: `Bearer ${jwt}` }
                    });
                    
                    if (!response.ok) {
                        console.warn(`Failed to load card for ${username}`);
                        return { 
                            playerId: playerId, 
                            username: username || "Unknown", 
                            color: pgs.playerColor, 
                            card: null 
                        }; 
                    }
                    
                    const card = await response.json();
                    
                    return { 
                        playerId: playerId, 
                        username: username, 
                        color: pgs.playerColor, 
                        card: card 
                    };
                });

                const results = await Promise.all(promises);
                
                if (!mounted) return;

                const dataMap = {};
                results.forEach(res => {
                    if (res.card) {
                        dataMap[res.playerId] = res;
                    }
                });
                
                setCardsData(dataMap);
                setLoading(false);
                setTimeout(() => {
                    if (mounted) setIsFlipped(true); 

                    setTimeout(() => {
                        if (mounted) {
                            console.log("Animation finished. Closing modal and starting game...");
                            onFinish();
                        }
                    }, 3500); 

                }, 1000);

            } catch (error) {
                console.error("Visual error in initiative:", error);
                if (mounted) onFinish(); 
            }
        };

        if (players && players.length > 0) {
            fetchInitiatives();
        } else {
            onFinish();
        }
        return () => { mounted = false; };
    }, []);

    const getCardImage = (playerColor, imageId) => {
        const prefix = COLOR_PREFIX[playerColor] || 'CB'; 
        return `/assets/cards/${prefix}_${imageId}.png`;
    };

    if (loading) return (
        <div className="initiative-modal-overlay">
            <h2 className="initiative-title">Preparing decks...</h2>
        </div>
    );

    const isCrowded = players.length > 4;
    const sortedPlayersById = [...players].sort((a, b) => a.id - b.id);
    

    return (
        <div className="initiative-modal-overlay">
            <h2 className="initiative-title">
                {players.length} Jugadores - Iniciativa
            </h2>
            
            <div className={`cards-container ${isCrowded ? 'crowded-mode' : ''}`}>
                {sortedPlayersById.map((pgs) => {
                    const data = cardsData[pgs.id];
                    if (!data) return null; 
                    const isWinner = actualWinnerId ? (pgs.id === actualWinnerId) : false;

                    return (
                        <div key={pgs.id} className="player-initiative-slot">
                            <div 
                                className={`flip-card ${isFlipped ? 'flipped' : ''}`}
                                style={isCrowded ? { width: '120px', height: '120px' } : {}} 
                            >
                                <div className="flip-card-inner">
                                    <div className="flip-card-front">
                                        <div className="deck-pattern"></div>
                                    </div>
                                    <div className="flip-card-back" style={{
                                        borderColor: isWinner ? '#ffd700' : 'white',
                                        boxShadow: isWinner ? '0 0 20px #ffd700' : 'none'
                                    }}>
                                        {data.card ? (
                                            <>
                                                <img 
                                                    src={getCardImage(data.color, data.card.imageId)} 
                                                    alt="Initiative" 
                                                />
                                                {isFlipped && (
                                                    <div className="initiative-badge" style={isCrowded ? {width: '24px', height:'24px', fontSize:'0.8rem'} : {}}>
                                                        {data.card.initiative}
                                                    </div>
                                                )}
                                            </>
                                        ) : <div>Error</div>}
                                    </div>
                                </div>
                            </div>
                            
                            <span className={`player-name ${isWinner ? 'winner-text' : ''}`} style={{fontSize: isCrowded ? '0.8rem' : '1rem'}}>
                                {data.username}
                                {isWinner && isFlipped && <span> &#11088;</span>}
                            </span>
                        </div>
                    );
                })}
            </div>
        </div>
    );
};

export default FirstTurnModal;