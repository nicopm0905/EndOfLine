import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Form, Label, Input, Button } from "reactstrap";
import tokenService from "../../services/token.service";
import getErrorModal from "../../util/getErrorModal";
import "./createFriendship.css";

export default function CreateFriendship() {
  const jwt = tokenService.getLocalAccessToken();
  const user = tokenService.getUser();
  const navigate = useNavigate();

  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [username, setUsername] = useState("");

  const modal = getErrorModal(setVisible, visible, message);

  const handleChange = (event) => {
    setUsername(event.target.value);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    try {
      const nick = encodeURIComponent(username.trim());
      const idResponse = await fetch(`/api/v1/players/username/${nick}`, {
        headers: {
          Authorization: `Bearer ${jwt}`,
          "Content-Type": "application/json",
        },
      });

      if (!idResponse.ok) {
        const text = await idResponse.text().catch(() => idResponse.statusText || "");
        throw new Error(
          `Player with username ${username} does not exist. (${idResponse.status} ${text})`
        );
      }

      const player = await idResponse.json();
      if (!player?.id) throw new Error("Response did not include player.id");

      if (Number(player.id) === Number(user.id)) {
        throw new Error("You cannot send a friendship request to yourself.");
      }

      const response = await fetch(`/api/v1/friendships`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${jwt}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          sender: user.id,
          receiver: player.id,
          state: "PENDING",
        }),
      });

      if (!response.ok) {
        const text = await response.text().catch(() => "");
        throw new Error(text || "Failed to send friendship request.");
      }

      navigate("/friendships");
    } catch (error) {
      setMessage(error.message);
    } finally {
      setVisible(true);
    }
  };

  return (
    <div className="friendship-list">
      <div className="friendship-container">
        <h1 className="text-center">Send Friendship</h1>
        {modal}
        <Form onSubmit={handleSubmit}>
          <div className="custom-form-input">
            <Label htmlFor="username">Friend's Nickname</Label>
            <Input
              type="text"
              required
              name="username"
              id="username"
              value={username}
              onChange={handleChange}
            />
          </div>
          <div style={{ display: "flex", justifyContent: "space-between", marginTop: "20px" }}>
            <Button
              size="lg"
              className="negative-button"
              onClick={() => navigate("/friendships")}
              type="button"
            >
              Cancel
            </Button>
            <Button size="lg" className="positive-button" type="submit">
              Send
            </Button>
          </div>
        </Form>
      </div>
    </div>
  );
}
