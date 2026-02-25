import React, { useState } from "react";
import { useParams } from "react-router-dom";
import useFetchState from "../util/useFetchState";
import tokenService from "../services/token.service";
import { Table, Progress } from "reactstrap";
import "../statistics/StatisticsGeneral.css";

export default function AchievementProgressAdmin() {
  const { achievementId } = useParams();
  const jwt = tokenService.getLocalAccessToken();

  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);

  const [playerProgress] = useFetchState(
    [],
    `/api/v1/player-achievements/achievement/${achievementId}/progress`,
    jwt,
    setMessage,
    setVisible
  );

  return (
    <div className="dashboard-wrapper">
      <div className="dashboard-header" style={{ marginBottom: "2rem" }}>
        <h1 className="welcome-title">
          <span className="username-highlight">Achievement Progress</span>
        </h1>
        <p className="player-summary">
          Achievement #{achievementId}
        </p>
      </div>

      <div style={{ display: "flex", justifyContent: "center", width: "100%" }}>
        <div className="ranking-card" style={{ width: "100%", maxWidth: "1000px" }}>
          {visible && message && (
            <div style={{ color: "#ff0055", marginBottom: "1rem", textAlign: "center", fontWeight: "bold" }}>
              {message}
            </div>
          )}

          <div className="table-responsive">
            <Table borderless responsive className="transparent-table align-middle text-center" style={{ marginBottom: 0 }}>
              <thead style={{ borderBottom: "2px solid rgba(0, 200, 255, 0.3)" }}>
                <tr>
                  <th style={{ color: "#00c8ff" }}>#</th>
                  <th style={{ color: "#00c8ff" }}>Jugador</th>
                  <th style={{ color: "#00c8ff" }}>Progreso</th>
                  <th style={{ color: "#00c8ff" }}>Completado</th>
                </tr>
              </thead>
              <tbody>
                {playerProgress.length > 0 ? (
                  playerProgress.map((pa, index) => (
                    <tr key={pa.playerId} style={{ borderBottom: "1px solid rgba(255, 255, 255, 0.1)" }}>
                      <td style={{ color: "#e5f6ff" }}>{index + 1}</td>
                      <td style={{ color: "#e5f6ff" }}>{pa.username}</td>
                      <td style={{ width: "40%" }}>
                        <div className="d-flex flex-column align-items-center">
                          <span style={{ color: "#e5f6ff", marginBottom: "5px" }}>
                            {pa.progress}/{pa.threshold}
                          </span>
                          <Progress
                            value={Math.min((pa.progress / pa.threshold) * 100, 100)}
                            color={pa.completed ? "success" : "info"}
                            className="w-75"
                            style={{ height: "10px", backgroundColor: "rgba(255,255,255,0.1)" }}
                          />
                        </div>
                      </td>
                      <td>
                        {pa.completed ? (
                          <span style={{ color: "#00ffbf", fontWeight: "700", textShadow: "0 0 10px rgba(0,255,191,0.5)" }}>
                            ✔ Completado
                          </span>
                        ) : (
                          <span style={{ color: "#ffb347" }}>En progreso</span>
                        )}
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan="4" className="text-center" style={{ color: "rgba(255,255,255,0.5)", fontStyle: "italic" }}>
                      No players with progress for this achievement.
                    </td>
                  </tr>
                )}
              </tbody>
            </Table>
          </div>
        </div>
      </div>
    </div>
  );
}
