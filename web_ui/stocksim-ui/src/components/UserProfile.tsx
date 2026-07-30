import { useAuth } from "../AuthContext";
import "../styles/App.css";
import { useEffect, useReducer } from "react";
import { api } from "../api.ts";
import type {User} from "../types.ts";

type State = {
  user: User | null;
  loading: boolean;
  error: string | null;
};

type Action =
  | { type: "fetching" }
  | { type: "fetch-success"; user: User }
  | { type: "fetch-error"; message: string };

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case "fetching":
      return { ...state, loading: true, error: null };
    case "fetch-success":
      return { ...state, user: action.user, loading: false, error: null };
    case "fetch-error":
      return { ...state, loading: false, error: action.message };
    default:
      return state;
  }
}

const initialState: State = {
  user: null,
  loading: true,
  error: null,
};

export function UserProfile() {
  const { user } = useAuth();
  const [state, dispatch] = useReducer(reducer, initialState);

  useEffect(() => {
    const fetchProfile = async () => {
      dispatch({ type: "fetching" });
      try {
        const data = await api.getMe();
        dispatch({ type: "fetch-success", user: data });
      } catch (err) {
        console.error("Failed to fetch user data:", err);
        dispatch({ type: "fetch-error", message: "Failed to load profile." });
      }
    };

    fetchProfile();
  }, []);

  if (state.loading) {
    return <div className="user-profile-loading">Loading profile…</div>;
  }

  if (state.error) {
    return <div className="user-profile-error">{state.error}</div>;
  }

  if (!user) {
    return <div className="user-profile-no-user">You must be logged in to view your profile.</div>;
  }


  return (
      <div className="user-profile-container">
        <h1 className="user-profile-title">
          Portfolio
        </h1>

        <div className="user-profile-card">
          <header className="user-profile-header">
            <div className="user-avatar">
              {user.username.charAt(0).toUpperCase()}
            </div>

            <div>
              <h2 className="user-name">
                {user.username}
              </h2>

              <div className="user-email">
                {user.email}
              </div>
            </div>
          </header>

          <div className="profile-stats">
            <div className="profile-stat">
              <div className="profile-stat-label">
                Available balance
              </div>

              <div className="profile-stat-value">
                ${user.balance.toFixed(2)}
              </div>
            </div>

            <div className="profile-stat">
              <div className="profile-stat-label">
                Portfolio value
              </div>

              <div className="profile-stat-value">
                ${user.balance.toFixed(2)}
              </div>
            </div>

            <div className="profile-stat">
              <div className="profile-stat-label">
                Today's return
              </div>

              <div className="profile-stat-value price-up">
                +0.00%
              </div>
            </div>
          </div>
        </div>
      </div>
  );
}
