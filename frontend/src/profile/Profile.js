import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import tokenService from '../services/token.service';
import jwt_decode from 'jwt-decode';
import '../statistics/StatisticsGeneral.css';
import './Profile.css';

export default function Profile() {
    const [username, setUsername] = useState("");
    const [userId, setUserId] = useState(null);
    const [avatarId, setAvatarId] = useState(1);
    const [roles, setRoles] = useState([]);
    const [userInfo, setUserInfo] = useState({
        firstName: '',
        lastName: '',
        email: ''
    });
    const [showLogoutModal, setShowLogoutModal] = useState(false);
    const navigate = useNavigate();
    const jwt = tokenService.getLocalAccessToken();

    useEffect(() => {
        if (!jwt) {
            navigate('/');
        } else {
            const decoded = jwt_decode(jwt);
            const decodedRoles = Array.isArray(decoded.roles)
                ? decoded.roles
                : Array.isArray(decoded.authorities)
                    ? decoded.authorities
                    : [];
            setUsername(jwt_decode(jwt).sub);
            setAvatarId(jwt_decode(jwt).avatarId || 1);
            setRoles(decodedRoles);
            fetchUserInfo(jwt_decode(jwt).sub);
        }
    }, [jwt, navigate]);

    const fetchUserInfo = async (username) => {
        try {
            const response = await fetch(`/api/v1/users/username/${username}`, {
                headers: {
                    'Authorization': `Bearer ${jwt}`,
                    'Content-Type': 'application/json'
                }
            });
            const text = await response.text();
            if (!response.ok) {
                console.error('Fetch user info error, status:', response.status, 'body:', text);
                return;
            }
            let data = {};
            try {
                data = text ? JSON.parse(text) : {};
            } catch (parseErr) {
                console.error('Failed to parse user info JSON:', parseErr, 'raw:', text);
                return;
            }
            setUserId(data.id);
            setAvatarId(data.avatarId || 1);
            setUserInfo({
                firstName: data.firstName || '',
                lastName: data.lastName || '',
                email: data.email || ''
            });
        } catch (error) {
            console.error('Error fetching user info:', error);
        }
    }

    const isAdmin = roles.includes('ADMIN');

    const handleLogout = () => {
        if (!userId) {
            console.error('User ID not available');
            return;
        }
        tokenService.removeUser();
        window.location.href = '/';
    };

    return (
        <div className="dashboard-wrapper">
            <div className="dashboard-header" style={{ marginBottom: "2rem" }}>
                <h1 className="welcome-title">
                    <span className="username-highlight">My Profile</span>
                </h1>
            </div>

            <div style={{ display: "flex", gap: "2rem", flexWrap: "wrap", justifyContent: "center", alignItems: "flex-start" }}>
                <div className="ranking-card" style={{ padding: "2rem", width: "300px", flexShrink: 0, textAlign: "center" }}>
                    <div style={{ marginBottom: "2rem" }}>
                        <img
                            src={`/avatars/avatar${avatarId}.png`}
                            alt="avatar"
                            style={{
                                width: "150px",
                                height: "150px",
                                borderRadius: "50%",
                                border: "4px solid #00c8ff",
                                boxShadow: "0 0 20px rgba(0, 200, 255, 0.4)"
                            }}
                        />
                        <h3 style={{ marginTop: "1rem", color: "#e5f6ff" }}>{username}</h3>
                    </div>
                    <nav style={{ display: "flex", flexDirection: "column", gap: "1rem" }}>
                        <Link to="/profile" className="auth-button" style={{ textDecoration: "none", backgroundColor: "#00c8ff", color: "black", textAlign: "center" }}>INFORMATION</Link>
                        <Link to="/achievements" className="view-all-btn">ACHIEVEMENTS</Link>
                        {!isAdmin && (
                            <Link to="/statistics" className="view-all-btn">STATISTICS</Link>
                        )}
                        {!isAdmin && (
                            <Link to="/friendships" className="view-all-btn">FRIENDSHIPS</Link>
                        )}
                        <div style={{ height: "1px", backgroundColor: "rgba(255,255,255,0.1)", margin: "1rem 0" }}></div>
                        <button
                            onClick={() => setShowLogoutModal(true)}
                            style={{
                                background: "rgba(255, 0, 85, 0.2)",
                                border: "1px solid #ff0055",
                                color: "#ff0055",
                                padding: "0.8rem",
                                borderRadius: "10px",
                                fontWeight: "bold",
                                cursor: "pointer",
                                width: "100%"
                            }}
                        >
                            LOGOUT
                        </button>
                    </nav>
                </div>

                <div className="ranking-card" style={{ padding: "3rem", flex: 1, minWidth: "300px", maxWidth: "800px" }}>
                    <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "2rem", borderBottom: "1px solid rgba(0, 200, 255, 0.3)", paddingBottom: "1rem" }}>
                        <h2 style={{ color: "#00c8ff", margin: 0 }}>USER INFORMATION</h2>
                        <Link to="/profile/edit" className="view-all-btn">EDIT PROFILE</Link>
                    </div>
                    <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "2rem" }}>
                        <div style={{ background: "rgba(255,255,255,0.05)", padding: "1.5rem", borderRadius: "10px", border: "1px solid rgba(0,200,255,0.1)" }}>
                            <span style={{ display: "block", color: "#00c8ff", fontSize: "0.9rem", fontWeight: "bold", marginBottom: "0.5rem" }}>USERNAME</span>
                            <span style={{ color: "#e5f6ff", fontSize: "1.2rem" }}>{username}</span>
                        </div>
                        <div style={{ background: "rgba(255,255,255,0.05)", padding: "1.5rem", borderRadius: "10px", border: "1px solid rgba(0,200,255,0.1)" }}>
                            <span style={{ display: "block", color: "#00c8ff", fontSize: "0.9rem", fontWeight: "bold", marginBottom: "0.5rem" }}>EMAIL</span>
                            <span style={{ color: "#e5f6ff", fontSize: "1.2rem" }}>{userInfo.email}</span>
                        </div>
                        <div style={{ background: "rgba(255,255,255,0.05)", padding: "1.5rem", borderRadius: "10px", border: "1px solid rgba(0,200,255,0.1)" }}>
                            <span style={{ display: "block", color: "#00c8ff", fontSize: "0.9rem", fontWeight: "bold", marginBottom: "0.5rem" }}>NAME</span>
                            <span style={{ color: "#e5f6ff", fontSize: "1.2rem" }}>{userInfo.firstName}</span>
                        </div>
                        <div style={{ background: "rgba(255,255,255,0.05)", padding: "1.5rem", borderRadius: "10px", border: "1px solid rgba(0,200,255,0.1)" }}>
                            <span style={{ display: "block", color: "#00c8ff", fontSize: "0.9rem", fontWeight: "bold", marginBottom: "0.5rem" }}>LASTNAME</span>
                            <span style={{ color: "#e5f6ff", fontSize: "1.2rem" }}>{userInfo.lastName}</span>
                        </div>
                    </div>
                </div>

                {showLogoutModal && (
                    <div className="delete-modal" style={{ background: "rgba(0,0,0,0.8)" }}>
                        <div className="delete-modal-content" style={{ background: "#1a2333", border: "2px solid #00c8ff", color: "white" }}>
                            <h2 className="delete-modal-title" style={{ color: "#00c8ff" }}>LOGOUT</h2>
                            <p className="delete-modal-text">ARE YOU SURE YOU WANT TO LOGOUT ?</p>
                            <div className="delete-modal-buttons">
                                <button onClick={handleLogout} className="delete-button-yes" style={{ background: "#00c8ff", color: "black" }}>YES</button>
                                <button onClick={() => setShowLogoutModal(false)} className="delete-button-no" style={{ background: "transparent", border: "1px solid #00c8ff", color: "#00c8ff" }}>NO</button>
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}