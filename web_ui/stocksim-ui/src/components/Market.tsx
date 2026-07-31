import { useEffect, useReducer, useState } from 'react';
import { LineChart, Line, XAxis, YAxis, Tooltip, ResponsiveContainer, ReferenceLine, Brush } from 'recharts';
import type { StockData, StockUpdate, PricePoint, UserAuth } from '../types';
import { api } from '../api';
import '../styles/App.css';
import { useAuth } from "../AuthContext";
import {Link} from "react-router";

type State = {
    stocks: Record<number, StockData>;
    marketStatus: string;
    selectedStockId: number | null;
    quantity: number;
    tradeLoading: boolean;
};

type Action =
    | { type: "update-stocks"; updates: StockUpdate[]; currentTime: string }
    | { type: "set-history"; stockId: number; history: PricePoint[] }
    | { type: "set-status"; status: string }
    | { type: "select-stock"; stockId: number | null }
    | { type: "set-quantity"; quantity: number }
    | { type: "set-trade-loading"; loading: boolean };

const initialState: State = {
    stocks: {},
    marketStatus: 'Connecting...',
    selectedStockId: null,
    quantity: 1,
    tradeLoading: false,
};

function reducer(state: State, action: Action): State {
    switch (action.type) {
        case "update-stocks": {
            const nextStocks = { ...state.stocks };
            action.updates.forEach((update) => {
                const current = nextStocks[update.stock_id];

                // Maintain a rolling history for the chart
                const history = current?.history ? [...current.history] : [];
                history.push({ time: action.currentTime, price: update.price });
                if (history.length > 60) history.shift(); // Keep max 60 points in memory

                nextStocks[update.stock_id] = {
                    id: update.stock_id,
                    ticker: update.ticker || current?.ticker || `ID:${update.stock_id}`,
                    previousPrice: current ? current.price : null,
                    price: update.price,
                    history: history,
                };
            });
            return { ...state, stocks: nextStocks };
        }
        case "set-history": {
            if (!state.stocks[action.stockId]) return state;
            return {
                ...state,
                stocks: {
                    ...state.stocks,
                    [action.stockId]: {
                        ...state.stocks[action.stockId],
                        history: action.history
                    }
                }
            };
        }
        case "set-status":
            return { ...state, marketStatus: action.status };
        case "select-stock":
            return { ...state, selectedStockId: action.stockId };
        case "set-quantity":
            return { ...state, quantity: action.quantity };
        case "set-trade-loading":
            return { ...state, tradeLoading: action.loading };
        default:
            return state;
    }
}

export function Market() {
    const { user, refreshUser } = useAuth();
    const [state, dispatch] = useReducer(reducer, initialState);

    useEffect(() => {
        const eventSource = new EventSource('/stocks/stream');

        eventSource.onopen = () => dispatch({ type: "set-status", status: '🟢 Live Market Open' });

        eventSource.addEventListener('PRICE-UPDATE', (event) => {
            const updates: StockUpdate[] = JSON.parse(event.data);
            const currentTime = new Date().toLocaleTimeString('en-US', { hour12: false, hour: "2-digit", minute: "2-digit", second: "2-digit" });

            dispatch({ type: "update-stocks", updates, currentTime });
        });

        eventSource.onerror = () => dispatch({ type: "set-status", status: '🔴 Connection Lost. Reconnecting...' });

        return () => eventSource.close();
    }, []);

    const handleTrade = async (type: "BUY" | "SELL") => {
        if (!state.selectedStockId || !user) return;
        dispatch({ type: "set-trade-loading", loading: true });
        try {
            await api.placeOrder({
                stockId: state.selectedStockId,
                type: type,
                quantity: state.quantity
            });
            alert(`Successfully placed ${type} order for ${state.quantity} shares!`);
            await refreshUser(); // Instantly update user balance in the navbar
        } catch (error: any) {
            alert(error.message || "Trade failed");
        } finally {
            dispatch({ type: "set-trade-loading", loading: false });
        }
    };

    // Handle clicking a stock row
    const toggleStockSelection = async (id: number) => {
        if (state.selectedStockId === id) {
            dispatch({ type: "select-stock", stockId: null });
            return;
        }

        dispatch({ type: "select-stock", stockId: id });

        try {
            const history = await api.getStockHistory(id);
            dispatch({ type: "set-history", stockId: id, history });
        } catch (error) {
            console.error("Failed to fetch stock history:", error);
        }
    };

    return (
        <div className="dashboard-container">
            <div className="market-header">
                <div className="market-title-group">
                    <h1>Markets</h1>
                    <p>Track simulated market prices in real time. Click a stock to view its chart.</p>
                </div>

                <div className="status-badge">
                    {state.marketStatus.replace("🟢 ", "").replace("🔴 ", "")}
                </div>
            </div>

            <div className="stock-grid">
                <div className="stock-row stock-header">
                    <span>Asset</span>
                    <span>Price</span>
                    <span>Change</span>
                    <span>Movement</span>
                    <span>Action</span>
                </div>

                {Object.values(state.stocks).length === 0 && (
                    <div className="loading-text">Waiting for market data...</div>
                )}

                {Object.values(state.stocks).map((stock) => {
                    const isUp = stock.previousPrice !== null && stock.price > stock.previousPrice;
                    const isDown = stock.previousPrice !== null && stock.price < stock.previousPrice;
                    const priceClass = isUp ? "price-up" : isDown ? "price-down" : "price-neutral";
                    const change = stock.previousPrice !== null ? stock.price - stock.previousPrice : 0;
                    const changePercent = stock.previousPrice !== null ? (change / stock.previousPrice) * 100 : 0;

                    const isSelected = state.selectedStockId === stock.id;

                    return (
                        <div key={stock.id} className="stock-item-container">
                            {/* Clickable Row */}
                            <div
                                className={`stock-row ${isSelected ? 'selected' : ''}`}
                                onClick={() => toggleStockSelection(stock.id)}
                            >
                                <div className="stock-symbol">
                                    <div className="stock-icon">{stock.ticker.slice(0, 2)}</div>
                                    <div>
                                        <div className="stock-name">{stock.ticker}</div>
                                        <div className="stock-ticker">Stock #{stock.id}</div>
                                    </div>
                                </div>

                                <div className="stock-price">${stock.price.toFixed(2)}</div>
                                <div className={priceClass}>{stock.previousPrice === null ? "—" : `${change >= 0 ? "+" : ""}$${change.toFixed(2)}`}</div>
                                <div className={priceClass}>{stock.previousPrice === null ? "—" : `${changePercent >= 0 ? "+" : ""}${changePercent.toFixed(2)}%`}</div>
                                <div className={priceClass}>{isUp ? "▲" : isDown ? "▼" : "—"}</div>
                            </div>

                            {/* Expanded View */}
                            {isSelected && stock.history && (
                                <ExpandedStockView
                                    stock={stock}
                                    user={user}
                                    quantity={state.quantity}
                                    tradeLoading={state.tradeLoading}
                                    setQuantity={(q) => dispatch({ type: "set-quantity", quantity: q })}
                                    handleTrade={handleTrade}
                                />
                            )}
                        </div>
                    );
                })}
            </div>
        </div>
    );
}

// ============================================================================
// EXTRACTED COMPONENT: This solves the zoom reset issue and paves the way
// for moving this to a dedicated page (e.g. /stocks/:ticker) in the future!
// ============================================================================

interface ExpandedStockViewProps {
    stock: StockData;
    user: UserAuth | null;
    quantity: number;
    tradeLoading: boolean;
    setQuantity: (q: number) => void;
    handleTrade: (type: "BUY" | "SELL") => void;
}

function ExpandedStockView({ stock, user, quantity, tradeLoading, setQuantity, handleTrade }: ExpandedStockViewProps) {
    // We store the Brush state here so that when new data streams in, Recharts doesn't reset the zoom!
    const [brushRange, setBrushRange] = useState<{ startIndex?: number, endIndex?: number }>({
        startIndex: undefined,
        endIndex: undefined
    });

    return (
        <div className="stock-expanded-view">
            {/* THE GRAPH */}
            <div className="stock-chart-container">
                <ResponsiveContainer width="100%" height={300}>
                    <LineChart data={stock.history}>
                        <XAxis dataKey="time" stroke="#64748b" fontSize={12} tickMargin={10} minTickGap={20} />
                        <YAxis domain={['auto', 'auto']} stroke="#64748b" fontSize={12} tickFormatter={(val) => `$${val}`} width={60} />
                        <Tooltip
                            contentStyle={{ backgroundColor: '#10151e', borderColor: '#303b4c', borderRadius: '8px' }}
                            itemStyle={{ color: '#8b5cf6' }}
                        />
                        {stock.history && stock.history.length > 0 && <ReferenceLine y={stock.history[0]?.price} stroke="#303b4c" strokeDasharray="3 3" />}
                        <Line type="monotone" dataKey="price" stroke="#8b5cf6" strokeWidth={2} dot={false} isAnimationActive={false} />

                        {/* ZOOM SLIDER: State is controlled so it doesn't reset on stream update */}
                        <Brush
                            dataKey="time"
                            height={30}
                            stroke="#8b5cf6"
                            fill="#10151e"
                            startIndex={brushRange.startIndex}
                            endIndex={brushRange.endIndex}
                            onChange={(newRange) => setBrushRange({
                                startIndex: newRange.startIndex,
                                endIndex: newRange.endIndex
                            })}
                        />
                    </LineChart>
                </ResponsiveContainer>
            </div>

            {/* THE TRADING PANEL */}
            <div className="stock-trade-panel">
                <h3 style={{ margin: "0 0 10px 0" }}>Trade {stock.ticker}</h3>
                <div style={{ marginBottom: "20px", color: "var(--text-secondary)" }}>
                    Current Price: <strong style={{ color: "var(--text)", fontSize: "18px" }}>${stock.price.toFixed(2)}</strong>
                </div>

                {user ? (
                    <div style={{ display: "flex", flexDirection: "column", gap: "10px" }}>
                        <label style={{ fontSize: "12px", color: "var(--text-muted)" }}>Shares to Trade</label>
                        <input
                            type="number"
                            min="1"
                            value={quantity}
                            onChange={(e) => setQuantity(Number(e.target.value))}
                            className="login-form-input"
                        />
                        <div style={{ display: "flex", gap: "10px", marginTop: "10px" }}>
                            <button
                                className="btn-primary"
                                style={{ background: 'var(--green)', width: '100%' }}
                                onClick={() => handleTrade("BUY")}
                                disabled={tradeLoading}
                            >Buy</button>
                            <button
                                className="btn-primary"
                                style={{ background: 'var(--red)', width: '100%' }}
                                onClick={() => handleTrade("SELL")}
                                disabled={tradeLoading}
                            >Sell</button>
                        </div>

                        {/* --- NEW STOCK INFO BUTTON --- */}
                        <div style={{ marginTop: '10px', paddingTop: '15px', borderTop: '1px solid var(--border)' }}>
                            <Link
                                to={`/stock/${stock.id}`}
                                className="btn-primary-outline"
                                style={{ width: '100%', textDecoration: 'none', display: 'flex' }}
                            >
                                View Stock Info →
                            </Link>
                        </div>
                    </div>
                ) : (
                    <p style={{ color: "var(--text-muted)", fontSize: "14px" }}>
                        Please log in to trade.
                    </p>
                )}
            </div>
        </div>
    );
}