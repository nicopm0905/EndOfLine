import { Table } from "reactstrap";
import useFetchState from "../util/useFetchState";
import { useState } from "react";
import tokenService from "../services/token.service";
import getErrorModal from "../util/getErrorModal";
import "../statistics/StatisticsGeneral.css";

const jwt = tokenService.getLocalAccessToken();

export default function DeveloperList() {
    const [message, setMessage] = useState(null)
    const [visible, setVisible] = useState(false)
    const [developers, setDevelopers] = useFetchState(
        [],
        `/api/v1/developers`,
        jwt,
        setMessage,
        setVisible
    );

    const imgnotfound = "https://cdn-icons-png.flaticon.com/512/48/48639.png";

    const modal = getErrorModal(setVisible, visible, message)

    const developerList = developers.map((d) => {
        return (
            <tr key={d.id} style={{ borderBottom: "1px solid rgba(255,255,255,0.1)" }}>
                <td className="text-center" style={{ color: "#e5f6ff", verticalAlign: "middle" }}>{d.name}</td>
                <td className="text-center" style={{ color: "#e5f6ff", verticalAlign: "middle" }}>{d.email}</td>
                <td className="text-center" style={{ verticalAlign: "middle" }}>
                    <a href={d.url} target="_blank" rel="noopener noreferrer" style={{ color: "#00c8ff", textDecoration: "none" }}>
                        {d.url}
                    </a>
                </td>
                <td className="text-center" style={{ verticalAlign: "middle" }}>
                    <img
                        src={d.properties.picUrl ? d.properties.picUrl : imgnotfound}
                        alt={d.name}
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

    return (
        <div className="dashboard-wrapper">
            {modal}

            <div className="dashboard-header" style={{ marginBottom: "2rem" }}>
                <h1 className="welcome-title">
                    <span className="username-highlight">Developers</span>
                </h1>
                <p className="player-summary">
                    Team Members: {developers.length}
                </p>
            </div>

            <div style={{ padding: "0 2rem" }}>
                <div className="ranking-card" style={{ overflowX: "auto" }}>
                    <Table borderless responsive className="mt-4 transparent-table" style={{ marginBottom: 0 }}>
                        <thead>
                            <tr style={{ borderBottom: "2px solid rgba(0, 200, 255, 0.3)" }}>
                                <th className="text-center" style={{ color: "#00c8ff", textTransform: "uppercase", letterSpacing: "1px" }}>Name</th>
                                <th className="text-center" style={{ color: "#00c8ff", textTransform: "uppercase", letterSpacing: "1px" }}>Email</th>
                                <th className="text-center" style={{ color: "#00c8ff", textTransform: "uppercase", letterSpacing: "1px" }}>URL</th>
                                <th className="text-center" style={{ color: "#00c8ff", textTransform: "uppercase", letterSpacing: "1px" }}>Image</th>
                            </tr>
                        </thead>
                        <tbody>{developerList}</tbody>
                    </Table>
                </div>
            </div>
        </div>
    );
}