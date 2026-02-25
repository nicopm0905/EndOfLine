import { useState } from "react";
import { Button, Card, CardBody, CardTitle, CardText, Progress } from "reactstrap";
import tokenService from "../services/token.service";
import useFetchState from "../util/useFetchState";
import { Link } from "react-router-dom";
import deleteFromList from "../util/deleteFromList";
import getErrorModal from "../util/getErrorModal";
import jwt_decode from "jwt-decode";
import "../statistics/StatisticsGeneral.css";
import "./achievementList.css";

const imgnotfound = "https://cdn-icons-png.flaticon.com/512/5778/5778223.png";
const jwt = tokenService.getLocalAccessToken();

export default function AchievementList() {
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [alerts, setAlerts] = useState([]);

  const userRoles = jwt ? jwt_decode(jwt).authorities : [];
  const isAdmin = userRoles.includes("ADMIN");
  const user = tokenService.getUser();
  const playerId = user ? user.id : null;

  const [achievements, setAchievements] = useFetchState(
    [],
    `/api/v1/achievements`,
    jwt,
    setMessage,
    setVisible
  );

  const [playerProgress] = useFetchState(
    [],
    !isAdmin && playerId ? `/api/v1/player-achievements/player/${playerId}/progress` : null,
    jwt,
    setMessage,
    setVisible
  );

  const modal = getErrorModal(setVisible, visible, message);

  const playerAchievementMap = new Map(
    (playerProgress ?? []).map(pa => [pa.achievementId, pa])
  );

  return (
    <div className="dashboard-wrapper">
      <div className="dashboard-header" style={{ marginBottom: "2rem" }}>
        <h1 className="welcome-title">
          <span className="username-highlight">Achievements</span>
        </h1>
        <p className="player-summary">
          Unlock new badges by playing!
        </p>
      </div>

      <div style={{ padding: "0 2rem" }}>
        {modal}
        {alerts.map((a) => a.alert)}

        {isAdmin && (
          <div className="text-center mb-4">
            <Link to="/achievements/new" style={{ textDecoration: "none" }}>
              <Button
                style={{
                  backgroundColor: "#00c8ff",
                  color: "black",
                  fontWeight: "bold",
                  border: "none",
                  boxShadow: "0 0 15px rgba(0, 200, 255, 0.5)",
                  padding: "0.8rem 2rem",
                  borderRadius: "30px"
                }}
              >
                + New Achievement
              </Button>
            </Link>
          </div>
        )}

        <div className="achievements-grid" style={{
          display: "grid",
          gridTemplateColumns: "repeat(auto-fill, minmax(300px, 1fr))",
          gap: "2rem"
        }}>
          {achievements.map((a) => {
            const playerAchievement = playerAchievementMap.get(a.id);
            const progress = playerAchievement ? playerAchievement.progress : 0;
            const completed = playerAchievement ? playerAchievement.completed : false;

            return (
              <Card
                key={a.id}
                className={`ranking-card ${completed ? "completed-glow" : ""}`}
                style={{
                  backgroundColor: "rgba(16, 25, 40, 0.86)",
                  border: completed ? "2px solid #00ffbf" : "2px solid rgba(0, 200, 255, 0.25)",
                  borderRadius: "20px",
                  color: "#e5f6ff",
                  transition: "transform 0.3s ease, box-shadow 0.3s ease"
                }}
              >
                <CardBody className="text-center d-flex flex-column h-100">
                  <div className="achievement-image-container" style={{ marginBottom: "1.5rem" }}>
                    <img
                      src={a.badgeImage ? a.badgeImage : imgnotfound}
                      alt={a.name}
                      className="achievement-image"
                      style={{
                        width: "80px",
                        height: "80px",
                        objectFit: "contain",
                        filter: completed ? "drop-shadow(0 0 10px #00ffbf)" : "drop-shadow(0 0 5px rgba(0,200,255,0.5))"
                      }}
                    />
                  </div>

                  <CardTitle tag="h3" style={{ color: completed ? "#00ffbf" : "#00c8ff", fontWeight: "bold", marginBottom: "0.5rem" }}>
                    {a.name}
                  </CardTitle>

                  <CardText style={{ color: "rgba(229, 246, 255, 0.8)", marginBottom: "1rem", flexGrow: 1 }}>
                    {a.description}
                  </CardText>

                  <CardText style={{ fontSize: "0.9rem", color: "rgba(255,255,255,0.5)", marginBottom: "1rem" }}>
                    <small>
                      Target: {a.threshold} {a.metric.replace(/_/g, " ")}
                    </small>
                  </CardText>

                  {!isAdmin && (
                    <div className="achievement-progress w-100 mt-auto">
                      <div className="d-flex justify-content-between mb-1" style={{ fontSize: "0.9rem", color: "#00c8ff" }}>
                        <span>Progress</span>
                        <span>{progress}/{a.threshold}</span>
                      </div>

                      <Progress
                        value={Math.min((progress / a.threshold) * 100, 100)}
                        color={completed ? "success" : "info"}
                        className="mt-2"
                        style={{ height: "8px", backgroundColor: "rgba(255,255,255,0.1)" }}
                      />

                      {completed && (
                        <div
                          style={{
                            color: "#00ffbf",
                            fontWeight: "700",
                            marginTop: "10px",
                            textShadow: "0 0 10px rgba(0, 255, 191, 0.4)"
                          }}
                        >
                          COMPLETED!
                        </div>
                      )}
                    </div>
                  )}

                  {isAdmin && (
                    <div className="achievement-actions mt-3 d-flex justify-content-center gap-2">
                      <Link to={`/achievements/${a.id}`} style={{ textDecoration: "none" }}>
                        <Button size="sm" style={{ backgroundColor: "rgba(0,200,255,0.2)", border: "1px solid #00c8ff", color: "#00c8ff" }}>
                          Edit
                        </Button>
                      </Link>

                      <Button
                        size="sm"
                        style={{ backgroundColor: "rgba(255,0,85,0.2)", border: "1px solid #ff0055", color: "#ff0055" }}
                        onClick={() =>
                          deleteFromList(
                            `/api/v1/achievements/${a.id}`,
                            a.id,
                            [achievements, setAchievements],
                            [alerts, setAlerts],
                            setMessage,
                            setVisible
                          )
                        }
                      >
                        Delete
                      </Button>

                      <Link
                        to={`/achievements/${a.id}/progress`}
                        style={{ textDecoration: "none" }}
                      >
                        <Button size="sm" style={{ backgroundColor: "rgba(0,255,191,0.2)", border: "1px solid #00ffbf", color: "#00ffbf" }}>
                          Progress
                        </Button>
                      </Link>
                    </div>
                  )}
                </CardBody>
              </Card>
            );
          })}
        </div>
      </div>
    </div>
  );
}
