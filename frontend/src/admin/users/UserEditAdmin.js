import { useState } from "react";
import { Link } from "react-router-dom";
import { Form, Input, Label } from "reactstrap";
import tokenService from "../../services/token.service";
import "../../statistics/StatisticsGeneral.css";
import getErrorModal from "../../util/getErrorModal";
import getIdFromUrl from "../../util/getIdFromUrl";
import useFetchData from "../../util/useFetchData";
import useFetchState from "../../util/useFetchState";

const jwt = tokenService.getLocalAccessToken();

export default function UserEditAdmin() {
  const emptyItem = {
    id: null,
    firstName: "",
    lastName: "",
    email: "",
    username: "",
    password: "",
    avatarId: 1,
    authority: null,
  };

  const id = getIdFromUrl(2);
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [user, setUser] = useFetchState(
    emptyItem,
    `/api/v1/users/${id}`,
    jwt,
    setMessage,
    setVisible,
    id
  );
  const auths = useFetchData(`/api/v1/users/authorities`, jwt);

  function handleChange(event) {
    const target = event.target;
    const value = target.value;
    const name = target.name;
    if (name === "authority") {
      const auth = auths.find((a) => a.id === Number(value));
      setUser({ ...user, authority: auth });
    } else setUser({ ...user, [name]: value });
  }

  function handleSubmit(event) {
    event.preventDefault();

    fetch("/api/v1/users" + (user.id ? "/" + user.id : ""), {
      method: user.id ? "PUT" : "POST",
      headers: {
        Authorization: `Bearer ${jwt}`,
        Accept: "application/json",
        "Content-Type": "application/json",
      },
      body: JSON.stringify(user),
    })
      .then((response) => response.json())
      .then((json) => {
        if (json.message) {
          setMessage(json.message);
          setVisible(true);
        } else window.location.href = "/users";
      })
      .catch((message) => alert(message));
  }

  const modal = getErrorModal(setVisible, visible, message);
  const authOptions = auths.map((auth) => (
    <option key={auth.id} value={auth.id}>
      {auth.authority}
    </option>
  ));

  const inputStyle = {
    backgroundColor: "rgba(255, 255, 255, 0.05)",
    border: "1px solid rgba(0, 200, 255, 0.3)",
    color: "#e5f6ff",
    borderRadius: "10px",
    padding: "0.8rem"
  };

  const labelStyle = {
    color: "#00c8ff",
    fontWeight: "600",
    marginBottom: "0.5rem",
    display: "block"
  };

  return (
    <div className="dashboard-wrapper" style={{ display: "flex", justifyContent: "center", alignItems: "center" }}>
      <div style={{ width: "100%", maxWidth: "600px" }}>
        {modal}

        <div className="ranking-card" style={{ padding: "3rem" }}>
          <h2 className="text-center" style={{ color: "#ffffff", marginBottom: "2rem", fontSize: "2rem", fontWeight: "bold" }}>
            {user.id ? <span className="username-highlight">Edit User</span> : <span className="username-highlight">New User</span>}
          </h2>

          <Form onSubmit={handleSubmit}>
            <div className="custom-form-input" style={{ marginBottom: "1.5rem" }}>
              <Label for="username" style={labelStyle}>
                Username:
              </Label>
              <Input
                type="text"
                required
                name="username"
                id="username"
                value={user.username || ""}
                onChange={handleChange}
                style={inputStyle}
              />
            </div>

            <div className="custom-form-input" style={{ marginBottom: "1.5rem" }}>
              <Label for="password" style={labelStyle}>
                Password:
              </Label>
              <Input
                type="password"
                required
                name="password"
                id="password"
                value={user.password || ""}
                onChange={handleChange}
                style={inputStyle}
              />
            </div>

            <div className="custom-form-input" style={{ marginBottom: "1.5rem" }}>
              <Label for="firstName" style={labelStyle}>
                First name:
              </Label>
              <Input
                type="text"
                required
                name="firstName"
                id="firstName"
                value={user.firstName || ""}
                onChange={handleChange}
                style={inputStyle}
              />
            </div>

            <div className="custom-form-input" style={{ marginBottom: "1.5rem" }}>
              <Label for="lastName" style={labelStyle}>
                Last Name:
              </Label>
              <Input
                type="text"
                required
                name="lastName"
                id="lastName"
                value={user.lastName || ""}
                onChange={handleChange}
                style={inputStyle}
              />
            </div>

            <div className="custom-form-input" style={{ marginBottom: "1.5rem" }}>
              <Label for="email" style={labelStyle}>
                Email:
              </Label>
              <Input
                type="text"
                required
                name="email"
                id="email"
                value={user.email || ""}
                onChange={handleChange}
                style={inputStyle}
              />
            </div>

            <div className="custom-form-input" style={{ marginBottom: "1.5rem" }}>
              <Label for="avatarId" style={labelStyle}>
                Avatar ID
              </Label>
              <Input
                type="number"
                name="avatarId"
                id="avatarId"
                min="1"
                value={user.avatarId || 1}
                onChange={handleChange}
                style={inputStyle}
              />
            </div>

            <div className="custom-form-input" style={{ marginBottom: "2rem" }}>
              <Label for="authority" style={labelStyle}>
                Authority
              </Label>
              {user.id ? (
                <Input
                  type="select"
                  disabled
                  name="authority"
                  id="authority"
                  value={user.authority?.id || ""}
                  onChange={handleChange}
                  style={{ ...inputStyle, opacity: 0.6 }}
                >
                  <option value="">None</option>
                  {authOptions}
                </Input>
              ) : (
                <Input
                  type="select"
                  required
                  name="authority"
                  id="authority"
                  value={user.authority?.id || ""}
                  onChange={handleChange}
                  style={{ ...inputStyle, color: "black" }}
                >
                  <option value="">None</option>
                  {authOptions}
                </Input>
              )}
            </div>

            <div className="custom-button-row" style={{ display: "flex", gap: "1rem", justifyContent: "center" }}>
              <button
                className="auth-button"
                style={{
                  backgroundColor: "#00c8ff",
                  color: "black",
                  fontWeight: "bold",
                  padding: "0.8rem 2rem",
                  borderRadius: "30px",
                  border: "none",
                  boxShadow: "0 5px 15px rgba(0, 200, 255, 0.4)",
                  cursor: "pointer",
                  fontSize: "1rem",
                  transition: "transform 0.2s"
                }}
              >
                Save
              </button>
              <Link
                to={`/users`}
                style={{
                  backgroundColor: "transparent",
                  color: "#00c8ff",
                  fontWeight: "bold",
                  padding: "0.8rem 2rem",
                  borderRadius: "30px",
                  border: "2px solid #00c8ff",
                  textDecoration: "none",
                  fontSize: "1rem"
                }}
              >
                Cancel
              </Link>
            </div>
          </Form>
        </div>
      </div>
    </div>
  );
}
