import { NavLink, Outlet, useNavigate, Link } from "react-router";
import { useAuth } from "../AuthContext";
import "../styles/App.css";

export function Layout() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = async () => {
        await logout();
        navigate("/login");
    };

    return (
        <div className="layout-wrapper">
            <header className="navbar">
                <div className="navbar-container">

                    <Link to="/" className="navbar-logo">
                        <span className="navbar-logo-mark">
                            ↗
                        </span>

                        <span className="navbar-logo-text">
                            Stock<span>Sim</span>
                        </span>
                    </Link>

                    <nav className="navbar-links">
                        <NavLink
                            to="/market"
                            className={({ isActive }) =>
                                `nav-link ${isActive ? "active" : ""}`
                            }
                        >
                            Markets
                        </NavLink>

                        {user && (
                            <NavLink
                                to="/me"
                                className={({ isActive }) =>
                                    `nav-link ${isActive ? "active" : ""}`
                                }
                            >
                                Portfolio
                            </NavLink>
                        )}
                    </nav>

                    <div className="navbar-auth">
                        {user ? (
                            <>
                                <Link
                                    to="/me"
                                    className="navbar-balance-btn"
                                >
                                    <span className="balance-label">
                                        Balance
                                    </span>

                                    <span className="balance-value">
                                        ${user.balance.toFixed(2)}
                                    </span>
                                </Link>

                                <Link
                                    to="/me"
                                    className="navbar-user"
                                >
                                    <span className="navbar-avatar">
                                        {user.username
                                            .charAt(0)
                                            .toUpperCase()}
                                    </span>

                                    <span className="navbar-username">
                                        {user.username}
                                    </span>
                                </Link>

                                <button
                                    onClick={handleLogout}
                                    className="btn-logout"
                                >
                                    Logout
                                </button>
                            </>
                        ) : (
                            <div className="auth-buttons">
                                <Link
                                    to="/login"
                                    className="nav-link"
                                >
                                    Log in
                                </Link>

                                <Link
                                    to="/register"
                                    className="btn-primary"
                                >
                                    Get started
                                </Link>
                            </div>
                        )}
                    </div>
                </div>
            </header>

            <main className="main-content">
                <Outlet />
            </main>
        </div>
    );
}