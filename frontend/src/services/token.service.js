class TokenService {
    getLocalRefreshToken() {
        const user = JSON.parse(sessionStorage.getItem("user"));
        return user?.refreshToken;
    }

    getLocalAccessToken() {
        const jwt = JSON.parse(sessionStorage.getItem("jwt"));
        return jwt ? jwt : null;
    }

    updateLocalAccessToken(token) {
        window.sessionStorage.setItem("jwt", JSON.stringify(token));
    }

    getUser() {
        return JSON.parse(sessionStorage.getItem("user"));
    }

    setUser(user) {
        window.sessionStorage.setItem("user", JSON.stringify(user));
    }

    removeUser() {
        window.sessionStorage.removeItem("user");
        window.sessionStorage.removeItem("jwt");
    }

}
const tokenService = new TokenService();

export default tokenService;