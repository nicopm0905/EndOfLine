import { useState } from "react";
import { Link } from "react-router-dom";
import { Button, ButtonGroup, Table } from "reactstrap";
import tokenService from "../../services/token.service";
import "../../statistics/StatisticsGeneral.css";
import deleteFromList from "../../util/deleteFromList";
import getErrorModal from "../../util/getErrorModal";
import useFetchState from "../../util/useFetchState";

const jwt = tokenService.getLocalAccessToken();

export default function UserListAdmin() {
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [users, setUsers] = useFetchState(
    [],
    `/api/v1/users`,
    jwt,
    setMessage,
    setVisible
  );
  const [alerts, setAlerts] = useState([]);

  const userList = users.map((user) => {
    return (
      <tr key={user.id} style={{ borderBottom: "1px solid rgba(255,255,255,0.1)" }}>
        <td className="text-center" style={{ color: "#e5f6ff", verticalAlign: "middle" }}>{user.username}</td>
        <td className="text-center" style={{ color: "#e5f6ff", verticalAlign: "middle" }}>{user.authority.authority}</td>
        <td className="text-center" style={{ verticalAlign: "middle" }}>
          <ButtonGroup>
            <Button
              size="sm"
              style={{
                backgroundColor: "rgba(0, 200, 255, 0.2)",
                border: "1px solid #00c8ff",
                color: "#00c8ff",
                marginRight: "5px"
              }}
              aria-label={"edit-" + user.username}
              tag={Link}
              to={"/users/" + user.id}
            >
              Edit
            </Button>
            <Button
              size="sm"
              style={{
                backgroundColor: "rgba(255, 0, 85, 0.2)",
                border: "1px solid #ff0055",
                color: "#ff0055"
              }}
              aria-label={"delete-" + user.username}
              onClick={() =>
                deleteFromList(
                  `/api/v1/users/${user.id}`,
                  user.id,
                  [users, setUsers],
                  [alerts, setAlerts],
                  setMessage,
                  setVisible
                )
              }
            >
              Delete
            </Button>
          </ButtonGroup>
        </td>
        <td className="text-center" style={{ verticalAlign: "middle" }}>
          <img
            src={`/avatars/avatar${user.avatarId || 1}.png`}
            alt={`avatar de ${user.username}`}
            style={{
              width: '50px',
              height: '50px',
              borderRadius: '50%',
              border: '2px solid #00d9ff',
              objectFit: 'cover',
              boxShadow: "0 0 10px rgba(0, 200, 255, 0.5)"
            }}
          />
        </td>
      </tr>
    );
  });

  const modal = getErrorModal(setVisible, visible, message);

  return (
    <div className="dashboard-wrapper">
      <div className="dashboard-header" style={{ marginBottom: "2rem" }}>
        <h1 className="welcome-title">
          <span className="username-highlight">Manage Users</span>
        </h1>
        <p className="player-summary">
          Total Users: {users.length}
        </p>
      </div>

      <div style={{ padding: "0 2rem" }}>
        {alerts.map((a) => a.alert)}
        {modal}

        <div style={{ marginBottom: "1.5rem", display: "flex", justifyContent: "flex-end" }}>
          <Button
            style={{
              backgroundColor: "#00c8ff",
              border: "none",
              color: "#000",
              fontWeight: "bold",
              boxShadow: "0 0 15px rgba(0, 200, 255, 0.5)",
              padding: "0.7rem 1.5rem",
              borderRadius: "10px"
            }}
            tag={Link}
            to="/users/new"
          >
            + New User
          </Button>
        </div>

        <div className="ranking-card" style={{ overflowX: "auto" }}>
          <Table aria-label="Users" borderless responsive className="mt-4 transparent-table" style={{ marginBottom: 0 }}>
            <thead>
              <tr style={{ borderBottom: "2px solid rgba(0, 200, 255, 0.3)" }}>
                <th className="text-center" style={{ color: "#00c8ff", textTransform: "uppercase", letterSpacing: "1px" }}>User</th>
                <th className="text-center" style={{ color: "#00c8ff", textTransform: "uppercase", letterSpacing: "1px" }}>Authority</th>
                <th className="text-center" style={{ color: "#00c8ff", textTransform: "uppercase", letterSpacing: "1px" }}>Actions</th>
                <th className="text-center" style={{ color: "#00c8ff", textTransform: "uppercase", letterSpacing: "1px" }}>Avatar</th>
              </tr>
            </thead>
            <tbody>{userList}</tbody>
          </Table>
        </div>
      </div>
    </div>
  );
}
