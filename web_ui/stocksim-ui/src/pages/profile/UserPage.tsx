import { useAuth } from "../../contexts/AuthContext";
import "../../styles/App.css";
import { useEffect, useReducer, useState, useMemo } from "react";
import { api } from "../../api/api.ts";
import { useNotification } from "../../contexts/NotificationContext";
import type { Holding, Order, User } from "../../types";
import { HoldingsTable, type HoldingsSortKey, type SortDirection } from "./HoldingsTable";
import { OrdersTable, type OrdersSortKey } from "./OrdersTable";

// --- STATE & REDUCER ---
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
    | { type: "fetch-user"; user: User }
    | { type: "fetch-error"; message: string }
    | { type: "depositing" }
    | { type: "deposit-success" };

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case "fetching":
      return { ...state, loading: true, error: null };
    case "fetch-success":
      return { ...state, user: action.user, holdings: action.holdings, orders: action.orders, loading: false, error: null };
    case "fetch-user":
      return { ...state, user: action.user };
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
  user: null, holdings: [], orders: [], isDepositing: false, loading: true, error: null,
};

export function UserPage() {
  const { user, refreshUser } = useAuth();
  const { notify } = useNotification();
  const [state, dispatch] = useReducer(reducer, initialState);

  // Sorting States
  const [holdingsSort, setHoldingsSort] = useState<{ key: HoldingsSortKey, direction: SortDirection }>({ key: 'value', direction: 'desc' });
  const [ordersSort, setOrdersSort] = useState<{ key: OrdersSortKey, direction: SortDirection }>({ key: 'time', direction: 'desc' });

  useEffect(() => {
    const fetchProfile = async () => {
      dispatch({ type: "fetching" });
      try {
        const [userData, holdingsData, ordersData] = await Promise.all([
          api.getMe(),
          api.getMyHoldings(),
          api.getMyOrders()
        ]);
        dispatch({ type: "fetch-success", user: userData as User, holdings: holdingsData, orders: ordersData });
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
      await refreshUser();
      const updatedData = await api.getMe();
      dispatch({ type: "fetch-user", user: updatedData as User });
      notify("Successfully deposited $20.00!", "success");
    } catch (err) {
      notify("Failed to deposit money.", "error");
    } finally {
      dispatch({ type: "deposit-success" });
    }
  };

  // --- CALCULATION LOGIC ---

  // 1. Calculate Average Buy Price by analyzing the user's order history
  const getAvgBuyPrice = (stockId: number) => {
    const buyOrders = state.orders.filter(o => o.stock.id === stockId && o.type === 'BUY');
    if (buyOrders.length === 0) return 0;

    const totalSpent = buyOrders.reduce((sum, o) => sum + (o.priceValueAtOrder * o.quantity), 0);
    const totalShares = buyOrders.reduce((sum, o) => sum + o.quantity, 0);
    return totalSpent / totalShares;
  };

  // 2. Sort Holdings
  const sortedHoldings = useMemo(() => {
    const avgPrices: Record<number, number> = {};
    state.holdings.forEach(h => {
      avgPrices[h.stock.id] = getAvgBuyPrice(h.stock.id);
    });
    const sortable = [...state.holdings];
    return sortable.sort((a, b) => {
      let aVal: any = 0; let bVal: any = 0;

      const aAvg = avgPrices[a.stock.id];
      const bAvg = avgPrices[b.stock.id];

      switch (holdingsSort.key) {
        case 'asset': aVal = a.stock.company.ticker; bVal = b.stock.company.ticker; break;
        case 'shares': aVal = a.quantity; bVal = b.quantity; break;
        case 'price': aVal = a.stock.price; bVal = b.stock.price; break;
        case 'value': aVal = a.quantity * a.stock.price; bVal = b.quantity * b.stock.price; break;
        case 'return': aVal = (a.stock.price - aAvg) * a.quantity; bVal = (b.stock.price - bAvg) * b.quantity; break;
      }

      if (aVal < bVal) return holdingsSort.direction === 'asc' ? -1 : 1;
      if (aVal > bVal) return holdingsSort.direction === 'asc' ? 1 : -1;
      return 0;
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [state.holdings, state.orders, holdingsSort]);

  // 3. Sort Orders
  const sortedOrders = useMemo(() => {
    const sortable = [...state.orders];
    return sortable.sort((a, b) => {
      let aVal: any = 0; let bVal: any = 0;
      switch (ordersSort.key) {
        case 'time':
          aVal = a.timestamp || "";
          bVal = b.timestamp || ""; break;
        case 'type': aVal = a.type; bVal = b.type; break;
        case 'asset': aVal = a.stock.company.ticker; bVal = b.stock.company.ticker; break;
        case 'shares': aVal = a.quantity; bVal = b.quantity; break;
        case 'price': aVal = a.priceValueAtOrder; bVal = b.priceValueAtOrder; break;
        case 'status': aVal = a.status; bVal = b.status; break;
      }
      if (aVal < bVal) return ordersSort.direction === 'asc' ? -1 : 1;
      if (aVal > bVal) return ordersSort.direction === 'asc' ? 1 : -1;
      return 0;
    });
  }, [state.orders, ordersSort]);

  // Handle Sort Clicks
  const toggleHoldingsSort = (key: HoldingsSortKey) => {
    setHoldingsSort(prev => ({ key, direction: prev.key === key && prev.direction === 'desc' ? 'asc' : 'desc' }));
  };
  const toggleOrdersSort = (key: OrdersSortKey) => {
    setOrdersSort(prev => ({ key, direction: prev.key === key && prev.direction === 'desc' ? 'asc' : 'desc' }));
  };

  // Rendering Checks
  if (state.loading) return <div className="user-profile-loading">Loading profile…</div>;
  if (state.error) return <div className="user-profile-error">{state.error}</div>;
  if (!user || !state.user) return <div className="user-profile-no-user">You must be logged in to view your profile.</div>;

  const assetsValue = state.holdings.reduce((total, h) => total + (h.quantity * h.stock.price), 0);
  const totalPortfolioValue = state.user.balance + assetsValue;

  return (
      <div className="user-profile-container">
        <h1 className="user-profile-title">Portfolio</h1>

        {/* TOP CARD: STATS & DEPOSIT */}
        <div className="user-profile-card" style={{ marginBottom: '30px' }}>
          <header className="user-profile-header">
            <div className="user-avatar">{state.user.username.charAt(0).toUpperCase()}</div>
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
              <button onClick={handleDeposit} disabled={state.isDepositing} className="btn-primary" style={{ marginTop: '12px', width: '100%', minHeight: '32px' }}>
                {state.isDepositing ? "Processing..." : "+ Deposit $20"}
              </button>
            </div>

            <div className="profile-stat">
              <div className="profile-stat-label">Invested Assets Value</div>
              <div className="profile-stat-value">${assetsValue.toFixed(2)}</div>
            </div>
          </div>
        </div>

        <h2 style={{ fontSize: '20px', marginBottom: '15px' }}>My Assets</h2>
        <HoldingsTable
            holdings={sortedHoldings}
            sort={holdingsSort}
            onSort={toggleHoldingsSort}
            getAvgBuyPrice={getAvgBuyPrice}
        />

        <h2 style={{ fontSize: '20px', marginBottom: '15px' }}>Order History</h2>
        <OrdersTable
            orders={sortedOrders}
            sort={ordersSort}
            onSort={toggleOrdersSort}
        />
      </div>
  );
}

