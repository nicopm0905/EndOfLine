import React, { createContext, useContext, useState, useCallback } from 'react';
import { usePresenceWebSocket } from '../hooks/usePresenceWebSocket';

const PresenceContext = createContext();

export const usePresence = () => {
  const context = useContext(PresenceContext);
  if (!context) {
    throw new Error('usePresence must be used within a PresenceProvider');
  }
  return context;
};

export const PresenceProvider = ({ children }) => {
  const [friendStatuses, setFriendStatuses] = useState({});
  const [updateCounter, setUpdateCounter] = useState(0);

  const handleStatusUpdate = useCallback((data) => {
    setFriendStatuses(prev => {
      const newStatuses = {
        ...prev,
        [data.username.toLowerCase()]: data.status
      };
      return newStatuses;
    });
    setUpdateCounter(prev => prev + 1);
  }, []);

  usePresenceWebSocket(handleStatusUpdate);

  const getFriendStatus = useCallback((username) => {
    return friendStatuses[username?.toLowerCase()] || 'OFFLINE';
  }, [friendStatuses]);

  return (
    <PresenceContext.Provider value={{ friendStatuses, getFriendStatus, handleStatusUpdate, updateCounter }}>
      {children}
    </PresenceContext.Provider>
  );
};
