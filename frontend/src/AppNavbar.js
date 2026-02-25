import React, { useState, useEffect, useMemo } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import tokenService from './services/token.service';
import jwt_decode from 'jwt-decode';
import CardNav from './navbar/CardNav';
import Notification from './invitation/Notifications';

function AppNavbar() {
    const [roles, setRoles] = useState([]);
    const [username, setUsername] = useState("");
    const [avatarId, setAvatarId] = useState(1);
    const [isDrawerOpen, setIsDrawerOpen] = useState(false);
    const [unreadCount, setUnreadCount] = useState(0);
    const jwt = tokenService.getLocalAccessToken();
    const navigate = useNavigate();
    const location = useLocation();

    const decodedJwt = useMemo(() => {
        if (!jwt) {
            return null;
        }
        try {
            return jwt_decode(jwt);
        } catch (error) {
            console.error('Invalid JWT payload detected:', error);
            return null;
        }
    }, [jwt]);

    useEffect(() => {
        if (decodedJwt) {
            let authorities = decodedJwt.authorities || [];
            if (!Array.isArray(authorities)) {
                authorities = [authorities];
            }
            setRoles(authorities);
            setUsername(decodedJwt.sub);
            fetchUserAvatar(decodedJwt.sub, jwt);
        } else {
            setRoles([]);
            setUsername('');
            setAvatarId(1);
        }
    }, [decodedJwt, jwt]);

    useEffect(() => {
        if(!jwt || !decodedJwt) return;

        // Admins don't have access to invitations, so skip this fetch to avoid 403 errors
        let authorities = decodedJwt.authorities || [];
        if (!Array.isArray(authorities)) {
            authorities = [authorities];
        }
        if (authorities.includes("ADMIN")) return;
        
        const checkUnread = () => {
            fetch('/api/v1/invitations/received/pending', { 
                 headers: { 'Authorization': `Bearer ${jwt}` }
            })
            .then(r => r.json())
            .then(data => {
                if(Array.isArray(data)) setUnreadCount(data.length);
            })
            .catch(() => {});
        };
        
        checkUnread();
        const interval = setInterval(checkUnread, 10000);
        return () => clearInterval(interval);
    }, [jwt, decodedJwt]);

    const fetchUserAvatar = async (username, jwt) => {
        if (!jwt) {
            setAvatarId(1);
            return;
        }
        try {
            const response = await fetch(`/api/v1/users/username/${username}`, {
                headers: {
                    'Authorization': `Bearer ${jwt}`,
                    'Content-Type': 'application/json'
                }
            });
            if (response.ok) {
                const data = await response.json();
                setAvatarId(data.avatarId || 1);
            }
        } catch (error) {
            console.error('Error fetching user avatar:', error);
            setAvatarId(decodedJwt?.avatarId || 1);
        }
    };

    const avatarSrc = `/avatars/avatar${avatarId}.png`;

    const navigationItems = useMemo(() => {
        const items = [];

        if (roles.includes('ADMIN')) {
            items.push(
                { key: 'admin-users', label: 'Users', to: '/users' },
                { key: 'admin-devs', label: 'Developers', to: '/developers' },
                { key: 'admin-games', label: 'Games', to: '/gameList' },
                { key: 'admin-achievements', label: 'Achievements', to: '/achievements' },
            );
        }

        if (roles.includes('PLAYER')) {
            items.push(
                { key: 'player-create', label: 'Create game', to: '/gameList/createGame' },
                { key: 'player-join', label: 'Join game', to: '/gameList/joinGame' },
                { key: 'player-spectator', label: 'Spectator', to: '/spectate' },
                { key: 'player-stats', label: 'Statistics', to: '/statistics' },
            );
        }

        if (!jwt) {
            items.push(
                { key: 'docs', label: 'Docs', to: '/docs', position: 'right' },
                { key: 'register', label: 'Register', to: '/register', position: 'right' }
            );
        }

        return items;
    }, [jwt, roles]);

    return (
        <>
            <Notification 
                isOpen={isDrawerOpen} 
                onClose={() => setIsDrawerOpen(false)} 
            />

            <CardNav
                items={navigationItems}
                username={jwt ? username : ''}
                avatarSrc={avatarSrc}
                onAvatarClick={() => navigate('/profile')}
                isAuthenticated={Boolean(jwt)}
                isTransparent={location.pathname === '/'}
                
                unreadCount={unreadCount} 
                onNotificationClick={() => setIsDrawerOpen(true)}
            />
        </>
    );
}

export default AppNavbar;