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
                    Trade stocks.
                    <br />
                    <span className="hero-gradient-text">
            Just for fun.
          </span>
                </h1>

                <p>
                    Fuck real trading, StockSim is a real-time stock market simulator game where
                    you can track live price movements, build your portfolio,
                    and experience a fictional stock market without risking real money.
                </p>

                <div className="hero-actions">
                    <Link to="/market" className="btn-primary">
                        Explore Stocks →
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
                        No risk
                    </div>
                </div>

                <div className="hero-stat">
                    <div className="hero-stat-label">
                        Experience
                    </div>
                    <div className="hero-stat-value">
                        Be a Baller
                    </div>
                </div>
            </div>
        </section>
    );
}