import React, { useEffect, useState } from "react";
import { Route, Routes } from "react-router-dom";
import jwt_decode from "jwt-decode";
import { ErrorBoundary } from "react-error-boundary";
import AppNavbar from "./AppNavbar";
import Home from "./home";
import PrivateRoute from "./privateRoute";
import Register from "./auth/register";
import Login from "./auth/login";
import Logout from "./auth/logout";
import tokenService from "./services/token.service";
import UserListAdmin from "./admin/users/UserListAdmin";
import UserEditAdmin from "./admin/users/UserEditAdmin";
import SwaggerDocs from "./public/swagger";
import DeveloperList from "./developers";
import GameList from "./gameList";
import CreateGame from "./gameList/createGame";
import JoinGame from "./gameList/joinGame"
import Profile from "./profile/Profile";
import ProfileEdit from "./profile/ProfileEdit";
import Lobby from "./lobby";
import AchievementList from "./achievements/achievementList";
import AchievementEdit from "./achievements/achievementEdit";
import AchievementProgressAdmin from "./achievements/AchievementProgressAdmin";
import GameView from "./gameplay";
import StatisticsList from "./statistics/StatisticsList";
import CreateFriendship from "./friendship/createFriendship";
import FriendshipList from "./friendship/friendshipList";
import ChatMessage from "./gameplay/chatMessage";
import PlayerHand from "./gameplay/playerControlsArea/playerHand/playerHand";
import StatisticGeneral from "./statistics/StatisticGeneral";
import StatisticGeneralPlayer from "./statistics/individualStatistics/StatisticGeneralPlayer";
import StatisticGameplayPlayer from "./statistics/individualStatistics/StatisticGamePlayPlayer";
import StatisticPuzzlePlayer from "./statistics/individualStatistics/StatisticPuzzlePlayer";
import StatisticSocialPlayer from "./statistics/individualStatistics/StatisticSocialPlayer";
import RankingDetailPage from "./statistics/ranking/RankingDetailPage";
import RerollButton from "./gameplay/playerControlsArea/playerHand/RerollButton";
import PuzzleMenu from "./puzzles/PuzzleMenu";
import { useLocation } from "react-router-dom";
import { getActiveLobbyId } from "./util/activeLobby";
import Spectate from "./spectate";
import SpectateView from "./spectate/SpectateView";
import MusicControl from "./music/musicControl";
import { PresenceProvider } from "./context/PresenceContext";



function ErrorFallback({ error, resetErrorBoundary }) {
  return (
    <div role="alert">
      <p>Something went wrong:</p>
      <pre>{error.message}</pre>
      <button onClick={resetErrorBoundary}>Try again</button>
    </div>
  )
}

function App() {
  const jwt = tokenService.getLocalAccessToken();
  const location = useLocation();
  const hideNavbar = location.pathname.startsWith("/gameList/") && location.pathname.includes("/play");
  const [activeLobbyId, setActiveLobbyId] = useState(getActiveLobbyId());

  let roles = []
  if (jwt) {
    roles = getRolesFromJWT(jwt);
  }

  function getRolesFromJWT(jwt) {
    return jwt_decode(jwt).authorities;
  }

  useEffect(() => {
    const handleActiveLobbyChange = () => {
      setActiveLobbyId(getActiveLobbyId());
    };

    window.addEventListener("active-lobby-change", handleActiveLobbyChange);
    return () => window.removeEventListener("active-lobby-change", handleActiveLobbyChange);
  }, []);

  const lobbyPath = activeLobbyId ? `/gameList/${activeLobbyId}` : null;
  const shouldShowLobbyBanner = Boolean(activeLobbyId) && !location.pathname.startsWith(lobbyPath);

  let adminRoutes = <></>;
  let ownerRoutes = <></>;
  let userRoutes = <></>;
  let vetRoutes = <></>;
  let publicRoutes = <></>;

  roles.forEach((role) => {
    if (role === "ADMIN") {
      adminRoutes = (
        <>
          <Route path="/users" exact={true} element={<PrivateRoute><UserListAdmin /></PrivateRoute>} />
          <Route path="/users/:username" exact={true} element={<PrivateRoute><UserEditAdmin /></PrivateRoute>} />  
          <Route path="/developers" element={<DeveloperList />} /> 
          <Route path="/gameList" element={<GameList />} />  
         
          <Route path="/developers" element={<DeveloperList />} />    
          <Route path="/achievements/" exact={true} element={<PrivateRoute><AchievementList /></PrivateRoute>} />
          <Route path="/achievements/:achievementId" exact={true} element={<PrivateRoute><AchievementEdit /></PrivateRoute>} /> 
          <Route path="/achievements/:achievementId/progress" exact={true} element={<PrivateRoute><AchievementProgressAdmin /></PrivateRoute>} />
          <Route path="/profile" element={<Profile />} />  
          <Route path="/profile/edit" element={<PrivateRoute><ProfileEdit /></PrivateRoute>} /> 
        </>)
    }
    if (role === "PLAYER") {
      ownerRoutes = (
        <>
          <Route path="/achievements/" exact={true} element={<PrivateRoute><AchievementList /></PrivateRoute>} />
          <Route path="/profile" element={<Profile />} />
          <Route path="/profile/edit" element={<PrivateRoute><ProfileEdit /></PrivateRoute>} />
          <Route path="/gameList/:gameId" exact={true} element={<PrivateRoute><Lobby /></PrivateRoute>} /> 
          <Route path="/statistics" exact={true} element={<PrivateRoute><StatisticGeneral /></PrivateRoute>} />
          <Route path="/statistics/general" exact={true} element={<PrivateRoute><StatisticGeneralPlayer /></PrivateRoute>} />
          <Route path="/statistics/gameplay" exact={true} element={<PrivateRoute><StatisticGameplayPlayer /></PrivateRoute>} />
          <Route path="/statistics/puzzle" exact={true} element={<PrivateRoute><StatisticPuzzlePlayer /></PrivateRoute>} />
          <Route path="/statistics/social" exact={true} element={<PrivateRoute><StatisticSocialPlayer /></PrivateRoute>} />
          <Route path="/statistics/ranking/:metric" exact={true} element={<PrivateRoute><RankingDetailPage /></PrivateRoute>} />
          <Route path="/friendships/create" exact={true} element={<PrivateRoute><CreateFriendship /></PrivateRoute>} />
          <Route path="/friendships" exact={true} element={<PrivateRoute><FriendshipList /></PrivateRoute>} />
          <Route path="/puzzles" exact={true} element={<PrivateRoute><PuzzleMenu /></PrivateRoute>} />
          <Route path="spectate" element={<SpectateView />} />
          <Route path="spectate/:id" element={<Spectate />} />


        </>)
    }    
  })
  if (!jwt) {
    publicRoutes = (
      <>        
        <Route path="/register" element={<Register />} />
        <Route path="/login" element={<Login />} />
      </>
    )
  } else {
    userRoutes = (
      <>       
        <Route path="/logout" element={<Logout />} />
        <Route path="/login" element={<Login />} />
        <Route path="gameList/createGame" element={<CreateGame />} />
        <Route path="gameList/joinGame" element={<JoinGame />} />
        <Route path="gameList/:id/play" element={<GameView />} />
        <Route path="gameList/:id/chat" element={<ChatMessage />} />
        <Route path="gameList/:id/playerHand" element={<PlayerHand />} />
        <Route path="gameList/:id/playerHand" element={<RerollButton />} />
      </>
    )
  }

  return (
    <PresenceProvider>
      <div className="app-shell">
        <ErrorBoundary FallbackComponent={ErrorFallback}>
          {!hideNavbar && <AppNavbar />}
          <main className="app-main">
            {shouldShowLobbyBanner && (
              <div className="lobby-return-banner" role="status" aria-live="polite">
                <div className="lobby-return-banner__text">
                  Waiting for players...
                </div>
                <button
                  className="lobby-return-banner__button"
                  onClick={() => (window.location.href = `/gameList/${activeLobbyId}`)}
                >
                  Back to lobby
                </button>
              </div>
            )}
            <MusicControl/>
            <Routes>
              <Route path="/" exact={true} element={<Home />} />
              <Route path="/docs" element={<SwaggerDocs />} />
              {publicRoutes}
              {userRoutes}
              {adminRoutes}
              {ownerRoutes}
              {vetRoutes}
            </Routes>
          </main>
        </ErrorBoundary>
      </div>
    </PresenceProvider>
  );
}

export default App;
