import React, { useEffect, useRef } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { gsap } from 'gsap';
import './CardNav.css';

const CardNav = ({
    items,
    username,
    avatarSrc,
    onAvatarClick,
    isAuthenticated,
    isTransparent = false,
    unreadCount,
    onNotificationClick
}) => {
    const cardsRef = useRef([]);
    const location = useLocation();

    cardsRef.current = [];

    const headerClassNames = ['card-nav'];
    if (isTransparent) {
        headerClassNames.push('card-nav--transparent');
    }

    useEffect(() => {
        const targets = cardsRef.current.filter(Boolean);
        if (!targets.length) {
            return;
        }
        gsap.fromTo(
            targets,
            { y: -16, opacity: 0 },
            {
                y: 0,
                opacity: 1,
                duration: 0.5,
                ease: 'power2.out',
                stagger: 0.08
            }
        );
    }, [items]);

    const leftItems = items.filter((item) => item.position !== 'right');
    const rightItems = items.filter((item) => item.position === 'right');

    return (
        <header className={headerClassNames.join(' ')} role="navigation" aria-label="Primary">
            <Link to="/" className="card-nav__brand">
                <img src="/logoEOL.png" alt="End Of Line" className="card-nav__logo" />
                <span className="card-nav__title">End Of Line</span>
            </Link>

            <nav className="card-nav__links" aria-label="Main links">
                {leftItems.map((item, index) => {
                    const isActive = location.pathname.startsWith(item.activeMatch ?? item.to);
                    return (
                        <Link
                            key={item.key ?? item.to}
                            to={item.to}
                            ref={(el) => {
                                cardsRef.current[index] = el;
                            }}
                            className={`card-nav__item ${isActive ? 'card-nav__item--active' : ''} ${item.variant === 'accent' ? 'card-nav__item--accent' : ''}`.trim()}
                        >
                            <span className="card-nav__item-label">{item.label}</span>
                            {item.subLabel ? (
                                <span className="card-nav__item-sublabel">{item.subLabel}</span>
                            ) : null}
                        </Link>
                    );
                })}
            </nav>

            <div className="card-nav__right">
                <nav className="card-nav__links card-nav__links--right" aria-label="Secondary links">
                    {rightItems.map((item, index) => {
                        const isActive = location.pathname.startsWith(item.activeMatch ?? item.to);
                        const refIndex = leftItems.length + index;
                        return (
                            <Link
                                key={item.key ?? item.to}
                                to={item.to}
                                ref={(el) => {
                                    cardsRef.current[refIndex] = el;
                                }}
                                className={`card-nav__item card-nav__item--compact ${isActive ? 'card-nav__item--active' : ''}`.trim()}
                            >
                                <span className="card-nav__item-label">{item.label}</span>
                            </Link>
                        );
                    })}
                </nav>
                {isAuthenticated && (
                    <div 
                        onClick={onNotificationClick}
                        style={{
                            cursor: 'pointer', 
                            position: 'relative', 
                            marginRight: '25px',
                            display: 'flex',
                            alignItems: 'center',
                            transition: 'transform 0.2s'
                        }}
                        onMouseOver={(e) => e.currentTarget.style.transform = 'scale(1.1)'}
                        onMouseOut={(e) => e.currentTarget.style.transform = 'scale(1)'}
                    >
                        <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#00c8ff" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                            <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"></path>
                            <polyline points="22,6 12,13 2,6"></polyline>
                        </svg>

                        {unreadCount > 0 && (
                            <span style={{
                                position: 'absolute', 
                                top: -8, 
                                right: -8,
                                background: '#ff003c', 
                                color: 'white', 
                                borderRadius: '50%',
                                width: '20px', 
                                height: '20px', 
                                fontSize: '11px',
                                fontWeight: 'bold',
                                display: 'flex', 
                                alignItems: 'center', 
                                justifyContent: 'center',
                                boxShadow: '0 0 8px #ff003c',
                                border: '1px solid #fff'
                            }}>
                                {unreadCount}
                            </span>
                        )}
                    </div>
                )}
                {isAuthenticated ? (
                    <button
                        type="button"
                        className="card-nav__avatar-button"
                        onClick={onAvatarClick}
                        aria-label="Open profile"
                    >
                        <img src={avatarSrc} alt={`${username} avatar`} className="card-nav__avatar" />
                        <span className="card-nav__username">{username}</span>
                    </button>
                ) : (
                    <Link to="/login" className="card-nav__login" ref={(el) => {
                        cardsRef.current[leftItems.length + rightItems.length] = el;
                    }}>
                        Login
                    </Link>
                )}
            </div>
        </header>
    );
};

export default CardNav;
