import { useAuth } from "../AuthContext";
import "../styles/App.css";
import { useEffect, useReducer } from "react";
import { api } from "../api.ts";
import type {Holding, Order, User} from "../types.ts";

type State = {
  user: User | null;
  holdings: Holding[];
  orders: Order[];
  loading: boolean;
  isDepositing: boolean;
  error: string | null;
};

type Action =
  | { type: "fetching" }
  | { type: "fetch-success"; user: User; holdings: Holding[]; orders: Order[] }
  | {type: "fetch-user"; user: User}
  | { type: "fetch-error"; message: string }
  | { type: "depositing" }
  | { type: "deposit-success" };

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case "fetching":
      return { ...state, loading: true, error: null };
    case "fetch-success":
      return { ...state, user: action.user, holdings: action.holdings, orders: action.orders, loading: false, error: null };
    case "fetch-error":
      return { ...state, loading: false, error: action.message };
    case "depositing":
      return { ...state, isDepositing: true };
    case "deposit-success":
      return { ...state, isDepositing: false };
    default:
      return state;
  }
}

const initialState: State = {
  user: null,
  holdings: [],
  orders: [],
  isDepositing: false,
  loading: true,
  error: null,
};

export function UserProfile() {
  const { user, refreshUser } = useAuth();
  const [state, dispatch] = useReducer(reducer, initialState);

  useEffect(() => {
    const fetchProfile = async () => {
      dispatch({ type: "fetching" });
      try {
        const [userData, holdingsData, ordersData] = await Promise.all([
          api.getMe(),
          api.getMyHoldings(),
          api.getMyOrders()
        ]);
        dispatch({ type: "fetch-success", user: userData, holdings: holdingsData, orders: ordersData });
      } catch (err) {
        console.error("Failed to fetch user data:", err);
        dispatch({ type: "fetch-error", message: "Failed to load profile." });
      }
    };

    fetchProfile();
  }, []);

  const handleDeposit = async () => {
    dispatch({ type: "depositing" });
    try {
      await api.deposit(20);
      await refreshUser(); // Instantly update navbar balance

      // Update local page state
      const updatedData = await api.getMe();
      dispatch({ type: "fetch-user", user: updatedData as User });
    } catch (err) {
      alert("Failed to deposit money.");
    } finally {
      dispatch({ type: "deposit-success" });
    }
  };

  if (state.loading) {
    return <div className="user-profile-loading">Loading profile…</div>;
  }

  if (state.error) {
    return <div className="user-profile-error">{state.error}</div>;
  }

  if (!user || !state.user) {
    return <div className="user-profile-no-user">You must be logged in to view your profile.</div>;
  }

  const assetsValue = state.holdings.reduce((total, h) => total + (h.quantity * h.stock.price), 0);
  const totalPortfolioValue = state.user.balance + assetsValue;


  return (
      <div className="user-profile-container">
        <h1 className="user-profile-title">Portfolio</h1>

        {/* TOP CARD: STATS & DEPOSIT */}
        <div className="user-profile-card" style={{ marginBottom: '30px' }}>
          <header className="user-profile-header">
            <div className="user-avatar">
              {state.user.username.charAt(0).toUpperCase()}
            </div>
            <div>
              <h2 className="user-name">{state.user.username}</h2>
              <div className="user-email">{state.user.email}</div>
            </div>
          </header>

          <div className="profile-stats">
            <div className="profile-stat">
              <div className="profile-stat-label">Total Portfolio Value</div>
              <div className="profile-stat-value">${totalPortfolioValue.toFixed(2)}</div>
            </div>

            <div className="profile-stat">
              <div className="profile-stat-label">Available Cash (Buying Power)</div>
              <div className="profile-stat-value">${state.user.balance.toFixed(2)}</div>
              <button
                  onClick={handleDeposit}
                  disabled={state.isDepositing}
                  className="btn-primary"
                  style={{ marginTop: '12px', width: '100%', minHeight: '32px' }}
              >
                {state.isDepositing ? "Processing..." : "+ Deposit $20"}
              </button>
            </div>

            <div className="profile-stat">
              <div className="profile-stat-label">Invested Assets Value</div>
              <div className="profile-stat-value">${assetsValue.toFixed(2)}</div>
            </div>
          </div>
        </div>

        {/* MIDDLE SECTION: HOLDINGS */}
        <h2 style={{ fontSize: '20px', marginBottom: '15px' }}>My Assets</h2>
        <div className="stock-grid" style={{ marginBottom: '40px' }}>
          <div className="stock-row stock-header" style={{ gridTemplateColumns: '1fr 1fr 1fr 1fr' }}>
            <span>Asset</span>
            <span>Shares Owned</span>
            <span>Current Price</span>
            <span>Total Value</span>
          </div>
          {state.holdings.length === 0 ? (
              <div className="loading-text" style={{ padding: '30px' }}>You don't own any stocks yet.</div>
          ) : (
              state.holdings.map(h => (
                  <div className="stock-row" key={h.stock.id} style={{ gridTemplateColumns: '1fr 1fr 1fr 1fr' }}>
                    <div style={{ fontWeight: 'bold' }}>{h.stock.company.ticker}</div>
                    <div>{h.quantity}</div>
                    <div>${h.stock.price.toFixed(2)}</div>
                    <div style={{ color: 'var(--green)', fontWeight: 'bold' }}>
                      ${(h.quantity * h.stock.price).toFixed(2)}
                    </div>
                  </div>
              ))
          )}
        </div>

        {/* BOTTOM SECTION: TRADE HISTORY */}
        <h2 style={{ fontSize: '20px', marginBottom: '15px' }}>Order History</h2>
        <div className="stock-grid">
          <div className="stock-row stock-header" style={{ gridTemplateColumns: '1fr 1fr 1fr 1fr 1fr' }}>
            <span>Type</span>
            <span>Asset</span>
            <span>Shares</span>
            <span>Price (At Order)</span>
            <span>Status</span>
          </div>
          {state.orders.length === 0 ? (
              <div className="loading-text" style={{ padding: '30px' }}>No trade history.</div>
          ) : (
              state.orders.map(o => (
                  <div className="stock-row" key={o.stock.id} style={{ gridTemplateColumns: '1fr 1fr 1fr 1fr 1fr' }}>
                    <div className={o.type === 'BUY' ? 'price-up' : 'price-down'}>
                      {o.type}
                    </div>
                    <div style={{ fontWeight: 'bold' }}>{o.stock.company.ticker}</div>
                    <div>{o.quantity}</div>
                    <div>${o.priceValueAtOrder.toFixed(2)}</div>
                    <div style={{ color: 'var(--text-muted)' }}>{o.status}</div>
                  </div>
              ))
          )}
        </div>
      </div>
  );
}
