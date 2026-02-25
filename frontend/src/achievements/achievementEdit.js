import {
    useState
} from "react";
import tokenService from "../services/token.service";
import { Link } from "react-router-dom";
import { Form, Input, Label } from "reactstrap";
import getErrorModal from "./../util/getErrorModal";
import getIdFromUrl from "./../util/getIdFromUrl";
import useFetchState from "./../util/useFetchState";
import { useNavigate } from "react-router-dom";
import "../statistics/StatisticsGeneral.css";

const jwt = tokenService.getLocalAccessToken();

export default function AchievementEdit() {
    const id = getIdFromUrl(2);
    const emptyAchievement = {
        id: id === "new" ? null : id,
        name: "",
        description: "",
        badgeImage: "",
        threshold: 1,
        metric: "GAMES_PLAYED",
        actualDescription: ""
    };

    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [achievement, setAchievement] = useFetchState(
        emptyAchievement, `/api/v1/achievements/${id}`,
        jwt,
        setMessage,
        setVisible,
        id
    );

    const modal = getErrorModal(setVisible, visible, message);
    const navigate = useNavigate();

    function handleSubmit(event) {
        event.preventDefault();
        fetch(
            "/api/v1/achievements" + (achievement.id ? "/" + achievement.id : ""),
            {
                method: achievement.id ? "PUT" : "POST",
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    Accept: "application/json",
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(achievement),
            }
        )
            .then((response) => response.text())
            .then((data) => {
                if (data === "")
                    navigate("/achievements");
                else {
                    let json = JSON.parse(data);
                    if (json.message) {
                        setMessage(JSON.parse(data).message);
                        setVisible(true);
                    } else
                        navigate("/achievements");
                }
            })
            .catch((message) => alert(message));
    }

    function handleChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;
        setAchievement({ ...achievement, [name]: value });
    }

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
                        {achievement.id ? <span className="username-highlight">Edit Achievement</span> : <span className="username-highlight">Add Achievement</span>}
                    </h2>

                    <Form onSubmit={handleSubmit}>
                        <div className="custom-form-input" style={{ marginBottom: "1.5rem" }}>
                            <Label for="name" style={labelStyle}> Name:  </Label>
                            <Input type="text" required name="name" id="name" value={achievement.name || ""} onChange={handleChange} style={inputStyle} />
                        </div>
                        <div className="custom-form-input" style={{ marginBottom: "1.5rem" }}>
                            <Label for="description" style={labelStyle}> Description: </Label>
                            <Input type="text" required name="description" id="description" value={achievement.description || ""} onChange={handleChange} style={inputStyle} />
                        </div>
                        <div className="custom-form-input" style={{ marginBottom: "1.5rem" }}>
                            <Label for="badgeImage" style={labelStyle}> Image URL: </Label>
                            <Input type="text" required name="badgeImage" id="badgeImage" value={achievement.badgeImage || ""} onChange={handleChange} style={inputStyle} />
                        </div>
                        <div className="custom-form-input" style={{ marginBottom: "1.5rem" }}>
                            <Label for="metric" style={labelStyle}> Metric:  </Label>
                            <Input type="select" required name="metric" id="metric" value={achievement.metric || ""} onChange={handleChange} style={{ ...inputStyle, color: "#000" }} >
                                <option value="">None</option>
                                <option value="GAMES_PLAYED">GAMES_PLAYED</option>
                                <option value="VICTORIES">VICTORIES</option>
                                <option value="TOTAL_PLAY_TIME">TOTAL_PLAY_TIME</option>
                                <option value="MAX_LINE_LENGTH">MAX_LINE_LENGTH</option>
                                <option value="TOTAL_SCORE">TOTAL_SCORE</option>
                            </Input>
                        </div>
                        <div className="custom-form-input" style={{ marginBottom: "2rem" }}>
                            <Label for="threshold" style={labelStyle}> Threshold: </Label>
                            <Input type="number" required name="threshold" id="threshold" value={achievement.threshold || ""} onChange={handleChange} style={inputStyle} />
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
                                SAVE
                            </button>
                            <Link
                                to={`/achievements`}
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
                                CANCEL
                            </Link>
                        </div>
                    </Form>
                </div>
            </div>
        </div>
    );
}