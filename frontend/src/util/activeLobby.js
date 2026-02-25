const ACTIVE_LOBBY_KEY = "activeLobbyId";

const notifyActiveLobbyChange = () => {
    window.dispatchEvent(new Event("active-lobby-change"));
};

export const getActiveLobbyId = () => {
    return window.sessionStorage.getItem(ACTIVE_LOBBY_KEY);
};

export const setActiveLobbyId = (lobbyId) => {
    if (lobbyId === null || lobbyId === undefined || lobbyId === "") return;
    window.sessionStorage.setItem(ACTIVE_LOBBY_KEY, String(lobbyId));
    notifyActiveLobbyChange();
};

export const clearActiveLobbyId = () => {
    window.sessionStorage.removeItem(ACTIVE_LOBBY_KEY);
    notifyActiveLobbyChange();
};
