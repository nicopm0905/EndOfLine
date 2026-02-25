import React, { useState } from "react";
import { Alert } from "reactstrap";
import FormGenerator from "../../components/formGenerator/formGenerator";
import tokenService from "../../services/token.service";
import "../../static/css/auth/authButton.css";
import { loginFormInputs } from "./form/loginFormInputs";
import "../../statistics/StatisticsGeneral.css";

export default function Login() {
  const [message, setMessage] = useState(null)
  const loginFormRef = React.createRef();


  async function handleSubmit({ values }) {

    const reqBody = values;
    setMessage(null);
    await fetch("/api/v1/auth/signin", {
      headers: { "Content-Type": "application/json" },
      method: "POST",
      body: JSON.stringify(reqBody),
    })
      .then(function (response) {
        if (response.status === 200) return response.json();
        else return Promise.reject("Invalid login attempt");
      })
      .then(function (data) {
        tokenService.setUser(data);
        tokenService.updateLocalAccessToken(data.token);
        window.location.href = "/";
      })
      .catch((error) => {
        setMessage(error);
      });
  }


  return (
    <div className="dashboard-wrapper" style={{ display: "flex", justifyContent: "center", alignItems: "center" }}>

      <div style={{ width: "100%", maxWidth: "500px" }}>
        <div className="ranking-card" style={{ padding: "3rem" }}>
          <h1 className="text-center" style={{ marginBottom: "2rem" }}>Login</h1>
          {message ? (
            <Alert color="primary" style={{ backgroundColor: "rgba(255, 0, 85, 0.2)", border: "1px solid #ff0055", color: "#ff0055" }}>{message}</Alert>
          ) : (
            <></>
          )}

          <div style={{ width: "100%" }}>
            <FormGenerator
              ref={loginFormRef}
              inputs={loginFormInputs}
              onSubmit={handleSubmit}
              numberOfColumns={1}
              listenEnterKey
              buttonText="Login"
              buttonClassName="auth-button"
            />
          </div>
        </div>
      </div>
    </div>
  );
}