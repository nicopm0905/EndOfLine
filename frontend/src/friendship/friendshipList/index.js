import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Button } from "reactstrap";
import tokenService from "../../services/token.service";
import deleteFromList from "../../util/deleteFromList";
import getErrorModal from "../../util/getErrorModal";
import "./friendshipList.css";
import { usePresence } from "../../context/PresenceContext";

const Pagination = ({ friendshipsPerPage, totalFriendships, paginate, currentPage }) => {
  const pageNumbers = [];
  for (let i = 1; i <= Math.ceil(totalFriendships / friendshipsPerPage); i++) {
    pageNumbers.push(i);
  }

  const getPageStyle = (pageNumber) => ({
    backgroundColor: "#343F4B",
    color: currentPage === pageNumber ? "#75FBFD" : "#EF87E0",
    border: "none",
    padding: "5px 10px",
    margin: "10px 5px",
    borderRadius: "5px",
    cursor: "pointer",
  });

  return (
    <nav>
      <ul className="pagination">
        {pageNumbers.map((number) => (
          <li key={number} className="page-item">
            <a
              onClick={(e) => {
                e.preventDefault();
                paginate(number);
              }}
              href="!#"
              style={getPageStyle(number)}
              className="page-link"
            >
              {number}
            </a>
          </li>
        ))}
      </ul>
    </nav>
  );
};

export default function FriendshipList() {
  const jwt = tokenService.getLocalAccessToken();
  const user = tokenService.getUser();

  const [friendshipType, setFriendshipType] = useState("ACCEPTED");
  const [currentPage, setCurrentPage] = useState(1);
  const [friendshipsPerPage] = useState(5);

  const [message, setMessage] = useState("");
  const [visible, setVisible] = useState(false);
  const [alerts, setAlerts] = useState([]);

  const [friendships, setFriendships] = useState([]);

  const { getFriendStatus, updateCounter } = usePresence();

  const navigate = useNavigate();
  const handleClick = () => navigate("/friendships/create");

  const modal = getErrorModal(setVisible, visible, message);

  const [, forceUpdate] = useState({});
  useEffect(() => {
    forceUpdate({});
  }, [updateCounter]);

  useEffect(() => {
    let abort = false;
    const loadFriendships = async () => {
      try {
        const res = await fetch(`/api/v1/friendships/all`, {
          headers: {
            Authorization: `Bearer ${jwt}`,
            "Content-Type": "application/json"
          },
        });

        if (!res.ok) {
          const text = await res.text();
          throw new Error(text || `Error fetching friendships (${res.status})`);
        }

        const data = await res.json();
        const all = Array.isArray(data) ? data : [];

        const mine = all.filter(
          f => f.sender?.id === user.id || f.receiver?.id === user.id
        );

        let filtered = [];
        if (friendshipType === "PENDING") {
          filtered = mine.filter(f => (f.state || f.friendState) === "PENDING");
        } else if (friendshipType === "ACCEPTED") {
          filtered = mine.filter(f => (f.state || f.friendState) === "ACCEPTED");
        }

        setFriendships(filtered);
      } catch (error) {
        console.error("Error loading friendships:", error);
        setMessage(error.message);
        setVisible(true);
      }
    };

    loadFriendships();
  }, [jwt, user.id, friendshipType]);

  const updateFriendshipState = async (friendshipId, newState) => {
    try {
      const response = await fetch(`/api/v1/friendships/${friendshipId}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${jwt}`,
        },
        body: JSON.stringify({ state: newState }),
      });

      if (!response.ok) {
        const txt = await response.text().catch(() => "");
        throw new Error(`Failed to update friendship state (${response.status}${txt ? " - " + txt : ""})`);
      }

      setFriendships((prev) =>
        prev.map((f) => (f.id === friendshipId ? { ...f, state: newState } : f))
      );

      if (friendshipType === "PENDING" && newState === "ACCEPTED") {
        setFriendships((prev) => prev.filter((f) => f.id !== friendshipId));
      }
    } catch (error) {
      setMessage(String(error.message || error));
      setVisible(true);
    }
  };

  const columnStyles = {
    pending: {
      nickname: { flex: 3, textAlign: "center", paddingLeft: "10px" },
      avatar: { flex: 2, textAlign: "center" },
      actions: { flex: 3, textAlign: "center" },
    },
    accepted: {
      nickname: { flex: 3, textAlign: "center", paddingLeft: "10px" },
      avatar: { flex: 2, textAlign: "center" },
      actions: { flex: 1.5, textAlign: "center" },
    },
  };

  const sorted = [...friendships].sort((a, b) => {
    if (friendshipType !== "PENDING") return 0;
    const aRecv = a?.receiver?.id === user.id;
    const bRecv = b?.receiver?.id === user.id;
    if (aRecv && !bRecv) return -1;
    if (!aRecv && bRecv) return 1;
    return 0;
  });

  const indexOfLast = currentPage * friendshipsPerPage;
  const indexOfFirst = indexOfLast - friendshipsPerPage;
  const currentFriendships = sorted.slice(indexOfFirst, indexOfLast);
  const paginate = (pageNumber) => setCurrentPage(pageNumber);

  const displayUserDetails = (friendship) => {
    const isSender = friendship?.sender?.id === user.id;
    const otherUser = isSender ? friendship?.receiver : friendship?.sender;

    const status = getFriendStatus(otherUser?.username) || "OFFLINE";
    let statusColor = "#ff0000ff";
    let boxShadow = "none";
    if (status === "ONLINE") {
      statusColor = "#00ff00";
      boxShadow = "0 0 8px #00ff00, 0 0 12px #00ff00";
    }
    if (status === "IN_GAME" || status === "PLAYING") {
      statusColor = "#ffa600ff";
      boxShadow = "0 0 8px #ffa600ff, 0 0 12px #ffa600ff";
    }
    const currentColumnStyle =
      friendshipType === "PENDING" ? columnStyles.pending : columnStyles.accepted;

    return (
      <div
        key={friendship.id}
        style={{
          display: "flex",
          justifyContent: "space-between",
          width: "100%",
          padding: "15px 10px",
          borderBottom: "1px solid #444",
          alignItems: "center",
          backgroundColor: "rgba(0,0,0,0.2)"
        }}
      >
        <span style={{
          ...currentColumnStyle.nickname,
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          gap: "12px"
        }}>
          <span style={{
            height: '10px',
            width: '10px',
            backgroundColor: statusColor,
            borderRadius: '50%',
            boxShadow: boxShadow,
            transition: 'all 0.3s ease'
          }}></span>
          {otherUser?.username ?? "(no nickname)"}
        </span>
        <span style={currentColumnStyle.avatar}>
          {otherUser?.avatarId ? (
            <img
              src={`/avatars/avatar${otherUser.avatarId}.png`}
              alt={`${otherUser?.username || "avatar"}`}
              style={{ borderRadius: "50%", width: "40px", height: "40px", objectFit: "cover" }}
            />
          ) : (
            <div style={{ opacity: 0.7 }}>No avatar</div>
          )}
        </span>
        <span style={currentColumnStyle.actions}>
          {friendshipType === "PENDING" && !isSender ? (
            <div>
              <Button
                aria-label={`update-${friendship.id}`}
                size="sm"
                style={{ marginRight: "5px" }}
                className="positive-button"
                onClick={() => updateFriendshipState(friendship.id, "ACCEPTED")}
              >
                Accept
              </Button>

              <Button
                aria-label={`delete-${friendship.id}`}
                size="sm"
                color="danger"
                className="negative-button"
                onClick={() =>
                  deleteFromList(
                    `/api/v1/friendships/${friendship.id}`,
                    friendship.id,
                    [friendships, setFriendships],
                    [alerts, setAlerts],
                    setMessage,
                    setVisible
                  )
                }
              >
                Deny
              </Button>
            </div>
          ) : (
            <Button
              aria-label={`delete-${friendship.id}`}
              size="sm"
              color="danger"
              className="negative-button"
              onClick={() =>
                deleteFromList(
                  `/api/v1/friendships/${friendship.id}`,
                  friendship.id,
                  [friendships, setFriendships],
                  [alerts, setAlerts],
                  setMessage,
                  setVisible
                )
              }
            >
              Delete
            </Button>
          )}
        </span>
      </div>
    );
  };

  return (
    <div className="friendship-list">
      <div className="friendship-container">
        <h1>{friendshipType === "ACCEPTED" ? "Friends" : "Pending Invitations"}</h1>

        <div style={{ display: "flex", flexDirection: "column", alignItems: "center", width: "100%" }}>
          <div
            style={{
              display: "flex",
              width: "100%",
              padding: "10px",
              justifyContent: "space-between",
              color: "#EF87E0",
            }}
          >
            <span
              style={
                friendshipType === "PENDING" ? columnStyles.pending.nickname : columnStyles.accepted.nickname
              }
            >
              {currentFriendships.length > 0 ? "Username" : ""}
            </span>
            <span
              style={
                friendshipType === "PENDING" ? columnStyles.pending.avatar : columnStyles.accepted.avatar
              }
            >
              {currentFriendships.length > 0 ? "Avatar" : ""}
            </span>
            <span
              style={
                friendshipType === "PENDING" ? columnStyles.pending.actions : columnStyles.accepted.actions
              }
            />
          </div>

          {currentFriendships.length > 0 ? (
            currentFriendships.map((friendship) => displayUserDetails(friendship))
          ) : (
            <div style={{ textAlign: "center", width: "100%" }}>
              {friendshipType === "ACCEPTED" ? "You don't have any friends yet." : "You don't have any pending invitations."}
            </div>
          )}
        </div>

        <Pagination
          friendshipsPerPage={friendshipsPerPage}
          totalFriendships={sorted.length}
          paginate={paginate}
          currentPage={currentPage}
        />

        {modal}

        <div style={{ width: "100%", display: "flex", justifyContent: "flex-end" }}>
          <Button
            className="normal-button"
            size="lg"
            style={{ marginRight: "10px" }}
            onClick={() => {
              setFriendshipType(friendshipType === "PENDING" ? "ACCEPTED" : "PENDING");
              setCurrentPage(1);
            }}
          >
            {friendshipType === "PENDING" ? "Friendships" : "Pending"}
          </Button>
          <Button className="positive-button" size="lg" onClick={handleClick}>
            Add Friendship
          </Button>
        </div>
      </div>
    </div>
  );
}
