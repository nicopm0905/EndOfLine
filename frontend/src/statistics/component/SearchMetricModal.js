import { useState, useMemo } from "react";
import { useNavigate } from "react-router-dom";
import "./SearchMetricModal.css";

const ALL_METRICS = [
    { key: 'victories', name: 'Victories', icon: '🥇', category: 'General', description: 'Total matches won' },
    { key: 'games_played', name: 'Games Played', icon: '🎮', category: 'General', description: 'Total matches completed' },
    { key: 'defeats', name: 'Defeats', icon: '💔', category: 'General', description: 'Total matches lost' },
    { key: 'winrate', name: 'Win Rate', icon: '📈', category: 'General', description: 'Victory percentage' },
    { key: 'total_play_time', name: 'Total Play Time', icon: '⌛', category: 'General', description: 'Total time played' },
    { key: 'totalScore', name: 'Total Score', icon: '⭐', category: 'General', description: 'Accumulated points' },
    { key: 'highest_score', name: 'Highest Score', icon: '🌟', category: 'General', description: 'Best single match score' },
    { key: 'lowest_score', name: 'Lowest Score', icon: '⬇️', category: 'General', description: 'Worst single match score' },
    { key: 'average_score', name: 'Average Score', icon: '📊', category: 'General', description: 'Mean score per match' },
    { key: 'average_duration', name: 'Average Duration', icon: '🕒', category: 'General', description: 'Average match duration' },
    { key: 'shortest_game', name: 'Shortest Game', icon: '⚡', category: 'General', description: 'Fastest match completed' },
    { key: 'longest_game', name: 'Longest Game', icon: '🐢', category: 'General', description: 'Longest match played' },
    { key: 'total_cards_used', name: 'Total Cards Used', icon: '🃏', category: 'Gameplay', description: 'All cards played' },
    { key: 'power_cards_used', name: 'Power Cards Used', icon: '💥', category: 'Gameplay', description: 'Special cards used' },
    { key: 'average_cards_per_game', name: 'Average Cards Per Game', icon: '🎴', category: 'Gameplay', description: 'Cards played per match' },
    { key: 'max_line_length', name: 'Max Line Length', icon: '📏', category: 'Gameplay', description: 'Longest line achieved' },
    { key: 'total_lines_completed', name: 'Total Lines Completed', icon: '✅', category: 'Gameplay', description: 'All lines finished' },
    { key: 'average_line_length', name: 'Average Line Length', icon: '📐', category: 'Gameplay', description: 'Mean line size' },
    { key: 'completed_puzzles', name: 'Completed Puzzles', icon: '🧩', category: 'Puzzle', description: 'Total puzzles solved' },
    { key: 'highest_score_puzzle', name: 'Highest Score Puzzle', icon: '🏆', category: 'Puzzle', description: 'Best puzzle score' },
    { key: 'total_puzzle_score', name: 'Total Puzzle Score', icon: '🎯', category: 'Puzzle', description: 'All puzzle points' },
    { key: 'average_puzzle_score', name: 'Average Puzzle Score', icon: '🎪', category: 'Puzzle', description: 'Mean puzzle score' },
    { key: 'messages_sent', name: 'Messages Sent', icon: '💬', category: 'Social', description: 'Total messages in chat' },
    { key: 'friends_count', name: 'Friends Count', icon: '👥', category: 'Social', description: 'Total friends' }
];

export function SearchMetricModal({ isOpen, onClose }) {
    const [searchQuery, setSearchQuery] = useState("");
    const navigate = useNavigate();

    const filteredMetrics = useMemo(() => {
        if (!searchQuery.trim()) return ALL_METRICS;
        
        const query = searchQuery.toLowerCase();
        return ALL_METRICS.filter(metric =>
            metric.name.toLowerCase().includes(query) ||
            metric.category.toLowerCase().includes(query) ||
            metric.description.toLowerCase().includes(query)
        );
    }, [searchQuery]);

    const handleMetricClick = (metricKey) => {
        navigate(`/statistics/ranking/${metricKey}`);
        onClose();
        setSearchQuery(""); 
    };

    const groupedMetrics = useMemo(() => {
        const groups = {};
        filteredMetrics.forEach(metric => {
            if (!groups[metric.category]) {
                groups[metric.category] = [];
            }
            groups[metric.category].push(metric);
        });
        return groups;
    }, [filteredMetrics]);

    if (!isOpen) return null;

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                <div className="modal-header">
                    <h2 className="modal-title">🔍 Search Rankings</h2>
                    <button className="modal-close" onClick={onClose}>✕</button>
                </div>

                <div className="search-bar-container">
                    <input
                        type="text"
                        className="search-input"
                        placeholder="Search metrics by name, category, or description..."
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        autoFocus
                    />
                    {searchQuery && (
                        <button className="clear-search" onClick={() => setSearchQuery("")}>
                            ✕
                        </button>
                    )}
                </div>

                <div className="metrics-results">
                    {filteredMetrics.length > 0 ? (
                        Object.entries(groupedMetrics).map(([category, metrics]) => (
                            <div key={category} className="metric-category-group">
                                <h3 className="category-header">{category}</h3>
                                <div className="metric-list">
                                    {metrics.map(metric => (
                                        <button
                                            key={metric.key}
                                            className="metric-item"
                                            onClick={() => handleMetricClick(metric.key)}
                                        >
                                            <span className="metric-icon">{metric.icon}</span>
                                            <div className="metric-info">
                                                <span className="metric-name">{metric.name}</span>
                                                <span className="metric-description">{metric.description}</span>
                                            </div>
                                            <span className="metric-arrow">→</span>
                                        </button>
                                    ))}
                                </div>
                            </div>
                        ))
                    ) : (
                        <div className="no-results">
                            <p>😕 No metrics found</p>
                            <p className="no-results-hint">Try searching for something else</p>
                        </div>
                    )}
                </div>

            </div>
        </div>
    );
}
