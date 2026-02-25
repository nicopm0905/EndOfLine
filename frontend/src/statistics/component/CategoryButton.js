import { Link } from "react-router-dom";

export function CategoryButton({ to, icon, title, subtitle, color }) {
    return (
        <Link to={to} className="category-button" style={{ "--clr": color }}>
            <div className="category-content">
                <div className="category-icon">{icon}</div>
                <h3 className="category-title">{title}</h3>
                <p className="category-subtitle">{subtitle}</p>
            </div>
            <div className="category-arrow">→</div>
            <div className="category-glow" aria-hidden="true"></div>
        </Link>
    );
}