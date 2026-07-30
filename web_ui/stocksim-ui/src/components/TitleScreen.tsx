import { Link } from "react-router";
import { useAuth } from "../AuthContext";
import "../styles/App.css";

export default function TitleScreen() {
    const { user } = useAuth();

    return (
        <section className="hero-section">
            <div className="hero-content">
                <div className="hero-eyebrow">
                    <span className="hero-eyebrow-dot" />
                    Live simulated markets
                </div>

                <h1>
                    Trade the market.
                    <br />
                    <span className="hero-gradient-text">
            Risk nothing.
          </span>
                </h1>

                <p>
                    StockSim is a real-time stock market simulator where
                    you can track live price movements, build your portfolio,
                    and experience the market without risking real money.
                </p>

                <div className="hero-actions">
                    <Link to="/market" className="btn-primary">
                        Explore Markets →
                    </Link>

                    {!user && (
                        <Link
                            to="/register"
                            className="btn-primary-outline"
                        >
                            Create an account
                        </Link>
                    )}
                </div>
            </div>

            <div className="hero-stats">
                <div className="hero-stat">
                    <div className="hero-stat-label">
                        Market data
                    </div>
                    <div className="hero-stat-value">
                        Real-time
                    </div>
                </div>

                <div className="hero-stat">
                    <div className="hero-stat-label">
                        Trading
                    </div>
                    <div className="hero-stat-value">
                        Zero risk
                    </div>
                </div>

                <div className="hero-stat">
                    <div className="hero-stat-label">
                        Experience
                    </div>
                    <div className="hero-stat-value">
                        Build your portfolio
                    </div>
                </div>
            </div>
        </section>
    );
}