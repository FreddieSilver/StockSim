import { useEffect, useReducer } from 'react';
import { useParams, useNavigate } from 'react-router';
import { api } from '../api';
import { StockChart, TradePanel, useTradeOrder } from './SharedComponents';
import type { StockDetail, PricePoint, StockUpdate } from '../types';
import '../styles/App.css';

type Timeframe = "1M" | "5M" | "30M" | "ALL";

type State = {
    stock: StockDetail | null;
    history: PricePoint[];
    livePrice: number | null;
    timeframe: Timeframe;
};

type Action =
    | { type: "set-initial-data"; stock: StockDetail; history: PricePoint[] }
    | { type: "set-history"; history: PricePoint[] }
    | { type: "update-live-price"; price: number; currentTime: string }
    | { type: "set-timeframe"; timeframe: Timeframe };

const initialState: State = { stock: null, history: [], livePrice: null, timeframe: "ALL" };

function reducer(state: State, action: Action): State {
    switch (action.type) {
        case "set-initial-data":
            return { ...state, stock: action.stock, history: action.history, livePrice: action.stock.price };
        case "set-history":
            return { ...state, history: action.history };
        case "update-live-price": {
            const newHistory = [...state.history, { time: action.currentTime, price: action.price }];

            let maxPoints = 30; // 1 Min
            if (state.timeframe === "5M") maxPoints = 150;
            if (state.timeframe === "30M") maxPoints = 900;
            if (state.timeframe === "ALL") maxPoints = 2000;

            if (newHistory.length > maxPoints) newHistory.shift();
            return { ...state, livePrice: action.price, history: newHistory };
        }
        case "set-timeframe":
            return { ...state, timeframe: action.timeframe };
        default: return state;
    }
}

export function StockScreen() {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();
    const [state, dispatch] = useReducer(reducer, initialState);

    const ticker = state.stock ? state.stock.company.ticker : "";
    const { quantity, setQuantity, tradeLoading, handleTrade, user, ownedQuantity } = useTradeOrder(Number(id), ticker);

    useEffect(() => {
        if (!id) return;
        const fetchInitialData = async () => {
            try {
                const rawHistory = await api.getStockHistory(Number(id), state.timeframe);
                const formattedHistory = rawHistory.map((p: any) => ({ time: p.timestamp || p.time, price: p.price }));

                if (state.stock) {
                    dispatch({ type: "set-history", history: formattedHistory });
                } else {
                    const stockData = await api.getStock(Number(id));
                    dispatch({ type: "set-initial-data", stock: stockData, history: formattedHistory });
                }
            } catch (error) {
                navigate('/market');
            }
        };
        fetchInitialData()
    }, [id, state.timeframe, navigate]);

    useEffect(() => {
        if (!id) return;
        const eventSource = new EventSource('/stocks/stream');
        eventSource.addEventListener('PRICE-UPDATE', (event) => {
            const updates: StockUpdate[] = JSON.parse(event.data);
            const myUpdate = updates.find(u => u.stock_id === Number(id));
            if (myUpdate) {
                const currentTime = new Date().toLocaleTimeString('en-US', { hour12: false, hour: "2-digit", minute: "2-digit", second: "2-digit" });
                dispatch({ type: "update-live-price", price: myUpdate.price, currentTime });
            }
        });
        return () => eventSource.close();
    }, [id]);

    if (!state.stock) return <div className="loading-text">Loading stock data...</div>;

    const name = state.stock.company.name;
    const description = state.stock.company.description;
    const displayPrice = (state.livePrice ?? state.stock.price).toFixed(2);

    return (
        <div className="dashboard-container">
            <div className="market-header">
                <div className="market-title-group">
                    <h1>{name}</h1>
                    <p>{ticker}</p>
                </div>
                <div className="stock-price" style={{ fontSize: '32px' }}>${displayPrice}</div>
            </div>

            <div className="stock-page-layout">
                <div className="stock-page-main">
                    <div className="stock-chart-container" style={{ background: 'var(--surface)', border: '1px solid var(--border)', borderRadius: 'var(--radius-lg)', padding: '20px' }}>
                        <div style={{ display: 'flex', gap: '8px', marginBottom: '15px' }}>
                            {(["1M", "5M", "30M", "ALL"] as const).map(tf => (
                                <button
                                    key={tf}
                                    onClick={() => dispatch({ type: "set-timeframe", timeframe: tf })}
                                    className={state.timeframe === tf ? "btn-primary" : "btn-primary-outline"}
                                    style={{ padding: '6px 14px', minHeight: '32px', fontSize: '13px' }}
                                >
                                    {tf === "1M" ? "1 Min" : tf === "5M" ? "5 Min" : tf === "30M" ? "30 Min" : "All Time"}
                                </button>
                            ))}
                        </div>
                        <StockChart data={state.history} height={400} yAxisWidth={80} />
                    </div>

                    <div className="company-details-card">
                        <h2>About {name}</h2>
                        <p className="company-description">{description}</p>
                        <div className="company-stats-grid">
                            <div className="company-stat-box">
                                <span className="stat-label">Volatility</span>
                                <span className="stat-value">{(state.stock.company.volatility * 100).toFixed(2)}%</span>
                            </div>
                            <div className="company-stat-box">
                                <span className="stat-label">Base Drift</span>
                                <span className="stat-value">{(state.stock.company.drift * 100).toFixed(2)}%</span>
                            </div>
                        </div>
                    </div>
                </div>

                <div className="stock-page-sidebar">
                    <TradePanel
                        stockId={Number(id)}
                        ticker={ticker}
                        price={displayPrice}
                        user={user}
                        quantity={quantity}
                        setQuantity={setQuantity}
                        onTrade={handleTrade}
                        tradeLoading={tradeLoading}
                        ownedQuantity={ownedQuantity}
                        showInfoLink={false}
                    />
                </div>
            </div>
        </div>
    );
}