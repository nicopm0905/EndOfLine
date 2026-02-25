import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import tokenService from '../services/token.service';
import jwt_decode from 'jwt-decode';
import '../statistics/StatisticsGeneral.css';
import './Profile.css';

export default function ProfileEdit() {
    const [username, setUsername] = useState("");
    const [userId, setUserId] = useState(null);
    const [avatarId, setAvatarId] = useState(1);
    const [authority, setAuthority] = useState(null);
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        email: '',
        password: ''
    });
    const [showLogoutModal, setShowLogoutModal] = useState(false);
    const [message, setMessage] = useState('');
    const navigate = useNavigate();
    const jwt = tokenService.getLocalAccessToken();

    useEffect(() => {
        if (!jwt) {
            navigate('/');
        } else {
            setUsername(jwt_decode(jwt).sub);
            setAvatarId(jwt_decode(jwt).avatarId || 1);
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
            if (response.ok) {
                const data = await response.json();
                setUserId(data.id);
                setAuthority(data.authority);
                setAvatarId(data.avatarId || 1);
                setFormData({
                    firstName: data.firstName || '',
                    lastName: data.lastName || '',
                    email: data.email || '',
                    password: ''
                });
            }
        } catch (error) {
            console.error('Error fetching user info:', error);
        }
    };

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!userId) {
            setMessage('Error: user ID not available');
            return;
        }

        if (!formData.password || formData.password.trim() === '') {
            setMessage('Password is required');
            return;
        }

        try {
            const updateData = {
                username: username,
                firstName: formData.firstName,
                lastName: formData.lastName,
                email: formData.email,
                avatarId: avatarId,
                authority: authority,
                password: formData.password
            };

            const response = await fetch(`/api/v1/users/${userId}`, {
                method: 'PUT',
                headers: {
                    'Authorization': `Bearer ${jwt}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(updateData)
            });

            if (response.ok) {
                setMessage('Profile updated successfully. Redirecting...');
                setTimeout(() => {
                    window.location.href = '/profile';
                }, 1500);
            } else {
                const errorData = await response.json();
                setMessage(`Error updating profile: ${errorData.message || 'Unknown error'}`);
            }
        } catch (error) {
            console.error('Error updating profile:', error);
            setMessage('Error updating profile');
        }
    };

    const handleAvatarChange = (newAvatarId) => {
        setAvatarId(newAvatarId);
    };


    const handleLogout = () => {
        if (!userId) {
            console.error('User ID not available');
            return;
        }
        tokenService.removeUser();
        window.location.href = '/';
    };

    const isAdmin = authority === 'ADMIN';

    return (
        <div className="dashboard-wrapper">
            <div className="dashboard-header" style={{ marginBottom: "2rem" }}>
                <h1 className="welcome-title">
                    <span className="username-highlight">My Profile</span>
                </h1>
            </div>

            <div style={{ display: "flex", gap: "2rem", flexWrap: "wrap", justifyContent: "center", alignItems: "flex-start", width: "100%" }}>
                <div className="ranking-card" style={{ padding: "2rem", width: "300px", flexShrink: 0, textAlign: "center", display: "flex", flexDirection: "column" }}>
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
                    <nav style={{ display: "flex", flexDirection: "column", gap: "1rem", flex: 1 }}>
                        <Link to="/profile" className="auth-button" style={{ textDecoration: "none", backgroundColor: "#00c8ff", color: "black", textAlign: "center" }}>INFORMATION</Link>
                        <Link to="/achievements" className="view-all-btn">ACHIEVEMENTS</Link>
                        {!isAdmin && (
                            <Link to="/statistics" className="view-all-btn">STATISTICS</Link>
                        )}
                        {!isAdmin && (
                            <Link to="/friendships" className="view-all-btn">FRIENDSHIPS</Link>
                        )}
                        <div style={{ height: "1px", backgroundColor: "rgba(255,255,255,0.1)", margin: "auto 0 1rem 0" }}></div>
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

                <div className="ranking-card" style={{ padding: "3rem", flex: 1, minWidth: "300px" }}>
                    <div style={{ marginBottom: "2rem", borderBottom: "1px solid rgba(0, 200, 255, 0.3)", paddingBottom: "1rem" }}>
                        <h2 style={{ color: "#00c8ff", margin: 0 }}>EDIT PROFILE</h2>
                    </div>

                    <div style={{ display: "grid", gridTemplateColumns: "250px 1fr", gap: "3rem", alignItems: "start" }}>
                        <div>
                            <h4 style={{ color: "#00c8ff", marginBottom: "1rem", textAlign: "center" }}>Select Avatar</h4>
                            <div style={{ display: "grid", gridTemplateColumns: "repeat(2, 1fr)", gap: "15px" }}>
                                {[1, 2, 3, 4, 5, 6, 7].map((id) => (
                                    <img
                                        key={id}
                                        src={`/avatars/avatar${id}.png`}
                                        alt={`avatar ${id}`}
                                        style={{
                                            width: "100%",
                                            borderRadius: "50%",
                                            cursor: "pointer",
                                            border: avatarId === id ? "3px solid #00c8ff" : "2px solid transparent",
                                            boxShadow: avatarId === id ? "0 0 15px #00c8ff" : "none",
                                            opacity: avatarId === id ? 1 : 0.6,
                                            transition: "all 0.2s"
                                        }}
                                        onClick={() => handleAvatarChange(id)}
                                        onMouseOver={(e) => {
                                            if (avatarId !== id) e.target.style.opacity = 1;
                                        }}
                                        onMouseOut={(e) => {
                                            if (avatarId !== id) e.target.style.opacity = 0.6;
                                        }}
                                    />
                                ))}
                            </div>
                        </div>

                        <form onSubmit={handleSubmit} style={{ display: "flex", flexDirection: "column", gap: "1.5rem" }}>
                            <div>
                                <label style={{ color: "#00c8ff", fontWeight: "bold", display: "block", marginBottom: "0.5rem" }}>USERNAME</label>
                                <input
                                    type="text"
                                    value={username}
                                    style={{
                                        width: "100%",
                                        background: "rgba(255,255,255,0.05)",
                                        border: "1px solid rgba(0,200,255,0.3)",
                                        color: "#e5f6ff",
                                        borderRadius: "10px",
                                        padding: "0.8rem",
                                        opacity: 0.7,
                                        cursor: "not-allowed"
                                    }}
                                    disabled
                                />
                            </div>

                            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "1rem" }}>
                                <div>
                                    <label style={{ color: "#00c8ff", fontWeight: "bold", display: "block", marginBottom: "0.5rem" }}>NAME</label>
                                    <input
                                        type="text"
                                        name="firstName"
                                        value={formData.firstName}
                                        onChange={handleChange}
                                        placeholder="Name"
                                        required
                                        style={{
                                            width: "100%",
                                            background: "rgba(255,255,255,0.05)",
                                            border: "1px solid rgba(0,200,255,0.3)",
                                            color: "#e5f6ff",
                                            borderRadius: "10px",
                                            padding: "0.8rem"
                                        }}
                                    />
                                </div>
                                <div>
                                    <label style={{ color: "#00c8ff", fontWeight: "bold", display: "block", marginBottom: "0.5rem" }}>LASTNAME</label>
                                    <input
                                        type="text"
                                        name="lastName"
                                        value={formData.lastName}
                                        onChange={handleChange}
                                        placeholder="Lastname"
                                        required
                                        style={{
                                            width: "100%",
                                            background: "rgba(255,255,255,0.05)",
                                            border: "1px solid rgba(0,200,255,0.3)",
                                            color: "#e5f6ff",
                                            borderRadius: "10px",
                                            padding: "0.8rem"
                                        }}
                                    />
                                </div>
                            </div>

                            <div>
                                <label style={{ color: "#00c8ff", fontWeight: "bold", display: "block", marginBottom: "0.5rem" }}>EMAIL</label>
                                <input
                                    type="email"
                                    name="email"
                                    value={formData.email}
                                    onChange={handleChange}
                                    placeholder="email@example.com"
                                    required
                                    style={{
                                        width: "100%",
                                        background: "rgba(255,255,255,0.05)",
                                        border: "1px solid rgba(0,200,255,0.3)",
                                        color: "#e5f6ff",
                                        borderRadius: "10px",
                                        padding: "0.8rem"
                                    }}
                                />
                            </div>

                            <div>
                                <label style={{ color: "#00c8ff", fontWeight: "bold", display: "block", marginBottom: "0.5rem" }}>PASSWORD *</label>
                                <input
                                    type="password"
                                    name="password"
                                    value={formData.password}
                                    onChange={handleChange}
                                    placeholder="Enter to confirm changes"
                                    required
                                    style={{
                                        width: "100%",
                                        background: "rgba(255,255,255,0.05)",
                                        border: "1px solid rgba(0,200,255,0.3)",
                                        color: "#e5f6ff",
                                        borderRadius: "10px",
                                        padding: "0.8rem"
                                    }}
                                />
                                <small style={{ color: '#00d9ff', fontSize: '0.8rem', marginTop: '5px', display: 'block', opacity: 0.8 }}>
                                    * Required to save changes
                                </small>
                            </div>

                            <button
                                type="submit"
                                className="auth-button"
                                style={{ marginTop: "1rem", width: "100%" }}
                            >
                                UPDATE PROFILE
                            </button>

                            {message && <p style={{ color: message.includes('Error') ? '#ff0055' : '#00ffbf', textAlign: 'center', marginTop: '1rem' }}>{message}</p>}
                        </form>
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