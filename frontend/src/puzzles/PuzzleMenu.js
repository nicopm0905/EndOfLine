import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import tokenService from '../services/token.service';
import './PuzzleMenu.css';

export default function PuzzleMenu() {
    const [puzzles, setPuzzles] = useState([]);
    const navigate = useNavigate();
    const jwt = tokenService.getLocalAccessToken();

    useEffect(() => {
        fetch('/api/v1/gameList/puzzle', { headers: { Authorization: `Bearer ${jwt}` } })
            .then(res => res.json())
            .then(data => setPuzzles(data))
            .catch(err => console.error(err));
    }, []);

    const handleStartPuzzle = async (puzzleId) => {
        try {
            const response = await fetch(`/api/v1/gameList/puzzle/${puzzleId}`, {
                method: 'POST',
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                const gameStartData = await response.json();
                navigate(`/gameList/${gameStartData.gameId}/play`, { state: { gameStartData } });
            } else {
                alert("Error starting puzzle");
            }
        } catch (error) {
            console.error("Error:", error);
        }
    };

    const handleStartSolitaire = async (solitaireId) => {
        try {
            const response = await fetch(`/api/v1/gameList/solitaire/${solitaireId}`, {
                method: 'POST',
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                const gameStartData = await response.json();
                navigate(`/gameList/${gameStartData.gameId}/play`, { state: { gameStartData } });
            } else {
                alert("Error starting solitaire game");
            }
        } catch (error) {
            console.error("Error:", error);
        }
    };

    return (
        <div className="puzzle-menu-container">
            <h1 className="puzzle-title">SOLO MODE - PUZZLES</h1>
            <div className="puzzle-grid">
                {puzzles.map((puzzle) => (
                    <div key={puzzle.id} className="puzzle-card">
                        <div className="puzzle-number">{puzzle.id}</div>
                        <div className="puzzle-name">{puzzle.name}</div>
                        <button className="play-btn" onClick={() => handleStartSolitaire(puzzle.id)}>SOLITAIRE ▶</button>
                        <button className="play-btn" onClick={() => handleStartPuzzle(puzzle.id)}>PUZZLE ▶</button>
                    </div>
                ))}
            </div>
        </div>
    );
}