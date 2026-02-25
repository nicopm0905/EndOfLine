import "../../static/css/auth/authButton.css";
import "../../static/css/auth/authPage.css";
import tokenService from "../../services/token.service";
import FormGenerator from "../../components/formGenerator/formGenerator";
import { registerFormUserInputs } from "./form/registerFormUserInputs";
import { useEffect, useRef, useState } from "react";
import "../../statistics/StatisticsGeneral.css";

export default function Register() {
  let [type, setType] = useState(null);
  let [authority, setAuthority] = useState(null);
  let [clinics, setClinics] = useState([]);

  const registerFormRef = useRef();

  function handleButtonClick(event) {
    const target = event.target;
    let value = target.value;
    if (value === "Back") {
      setType(null);
      setAuthority(null);
    } else {
      setType(value);
      if (value === "Player") {
        setAuthority(2);
      } else if (value === "Admin") {
        setAuthority(1);
      }
    }
  }

  function handleSubmit({ values }) {

    if (!registerFormRef.current.validate()) return;

    const request = values;
    request["authority"] = authority;
    let state = "";

    fetch("/api/v1/auth/signup", {
      headers: { "Content-Type": "application/json" },
      method: "POST",
      body: JSON.stringify(request),
    })
      .then(function (response) {
        if (response.status === 200) {
          const loginRequest = {
            username: request.username,
            password: request.password,
          };

          fetch("/api/v1/auth/signin", {
            headers: { "Content-Type": "application/json" },
            method: "POST",
            body: JSON.stringify(loginRequest),
          })
            .then(function (response) {
              if (response.status === 200) {
                state = "200";
                return response.json();
              } else {
                state = "";
                return response.json();
              }
            })
            .then(function (data) {
              if (state !== "200") alert(data.message);
              else {
                tokenService.setUser(data);
                tokenService.updateLocalAccessToken(data.token);
                window.location.href = "/";
              }
            })
            .catch((message) => {
              alert(message);
            });
        }
      })
      .catch((message) => {
        alert(message);
      });
  }

  if (type) {
    return (
      <div className="dashboard-wrapper" style={{ display: "flex", justifyContent: "center", alignItems: "center" }}>
        <div style={{ width: "100%", maxWidth: "600px" }}>
          <div className="ranking-card" style={{ padding: "3rem" }}>
            <h1 className="text-center" style={{ marginBottom: "2rem" }}>Register</h1>
            <div style={{ width: "100%" }}>
              <FormGenerator
                ref={registerFormRef}
                inputs={registerFormUserInputs}
                onSubmit={handleSubmit}
                numberOfColumns={1}
                listenEnterKey
                buttonText="Save"
                buttonClassName="auth-button"
              />
            </div>
          </div>
        </div>
      </div>
    );
  } else {
    return (
      <div className="dashboard-wrapper" style={{ display: "flex", justifyContent: "center", alignItems: "center" }}>
        <div style={{ width: "100%", maxWidth: "600px", textAlign: "center" }}>
          <div className="ranking-card" style={{ padding: "3rem" }}>
            <h1 style={{ marginBottom: "1rem" }}>Register</h1>
            <h2 className="text-center text-md" style={{ color: "#e5f6ff", marginBottom: "2rem", fontSize: "1.2rem" }}>
              What type of user will you be?
            </h2>
            <div className="options-row" style={{ display: "flex", justifyContent: "center", gap: "2rem" }}>
              <button
                className="auth-button"
                value="Player"
                onClick={handleButtonClick}
              >
                Player
              </button>
              <button
                className="auth-button"
                value="Admin"
                onClick={handleButtonClick}
              >
                Admin
              </button>
            </div>
          </div>
        </div>
      </div>
    );
  }
}
