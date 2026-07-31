import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router';
import { LineChart, Line, XAxis, YAxis, Tooltip, ResponsiveContainer, ReferenceLine, Brush } from 'recharts';
import { api } from '../api';
import { useAuth } from '../AuthContext';
import type { StockDetail, PricePoint, StockUpdate } from '../types';
import '../styles/App.css';

// Helper to safely extract values from Kotlin backend wrappers
const extractValue = (obj: any) => (obj && typeof obj === 'object' && 'value' in obj ? obj.value : obj);

export function StockScreen() {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();
    const { user, refreshUser } = useAuth();

    const [stock, setStock] = useState<StockDetail | null>(null);
    const [history, setHistory] = useState<PricePoint[]>([]);
    const [livePrice, setLivePrice] = useState<number | null>(null);

    const [quantity, setQuantity] = useState<number>(1);
    const [tradeLoading, setTradeLoading] = useState<boolean>(false);
    const [brushRange, setBrushRange] = useState<{ startIndex?: number, endIndex?: number }>({});

    // 1. Fetch initial stock data and history
    useEffect(() => {
        if (!id) return;
        const fetchInitialData = async () => {
            try {
                const stockData = await api.getStock(Number(id));
                const historyData = await api.getStockHistory(Number(id));
                setStock(stockData);
                setHistory(historyData);
                setLivePrice(extractValue(stockData.price));
            } catch (error) {
                console.error("Failed to load stock");
                navigate('/market'); // Redirect if stock doesn't exist
            }
        };
        fetchInitialData();
    }, [id, navigate]);

    // 2. Connect to SSE for live price updates
    useEffect(() => {
        if (!id) return;
        const eventSource = new EventSource('/stocks/stream');

        eventSource.addEventListener('PRICE-UPDATE', (event) => {
            const updates: StockUpdate[] = JSON.parse(event.data);
            const myUpdate = updates.find(u => u.stock_id === Number(id));

            if (myUpdate) {
                const currentTime = new Date().toLocaleTimeString('en-US', { hour12: false, hour: "2-digit", minute: "2-digit", second: "2-digit" });
                setLivePrice(myUpdate.price);
                setHistory(prev => {
                    const newHistory = [...prev, { time: currentTime, price: myUpdate.price }];
                    if (newHistory.length > 60) newHistory.shift(); // Keep last 60 ticks
                    return newHistory;
                });
            }
        });

        return () => eventSource.close();
    }, [id]);

    const handleTrade = async (type: "BUY" | "SELL") => {
        if (!id || !user) return;
        setTradeLoading(true);
        try {
            await api.placeOrder({ stockId: Number(id), type, quantity });
            alert(`Successfully placed ${type} order for ${quantity} shares!`);
            await refreshUser();
        } catch (error: any) {
            alert(error.message || "Trade failed");
        } finally {
            setTradeLoading(false);
        }
    };

    if (!stock) return <div className="loading-text">Loading stock data...</div>;

    const ticker = extractValue(stock.company.ticker);
    const name = extractValue(stock.company.name);
    const description = extractValue(stock.company.description);
    const volatility = stock.company.volatility;
    const drift = stock.company.drift;
    const displayPrice = livePrice?.toFixed(2) || extractValue(stock.price).toFixed(2);

    return (
        <div className="dashboard-container">
            <div className="market-header">
                <div className="market-title-group">
                    <h1>{name}</h1>
                    <p>{ticker}</p>
                </div>
                <div className="stock-price" style={{ fontSize: '32px' }}>
                    ${displayPrice}
                </div>
            </div>

            <div className="stock-page-layout">
                {/* Left Column: Graph & Company Details */}
                <div className="stock-page-main">
                    <div className="stock-chart-container" style={{ background: 'var(--surface)', border: '1px solid var(--border)', borderRadius: 'var(--radius-lg)', padding: '20px' }}>
                        <ResponsiveContainer width="100%" height={400}>
                            <LineChart data={history}>
                                <XAxis dataKey="time" stroke="#64748b" fontSize={12} tickMargin={10} minTickGap={20} />
                                <YAxis domain={['auto', 'auto']} stroke="#64748b" fontSize={12} tickFormatter={(val) => `$${val}`} width={60} />
                                <Tooltip contentStyle={{ backgroundColor: '#10151e', borderColor: '#303b4c', borderRadius: '8px' }} itemStyle={{ color: '#8b5cf6' }} />
                                {history.length > 0 && <ReferenceLine y={history[0]?.price} stroke="#303b4c" strokeDasharray="3 3" />}
                                <Line type="monotone" dataKey="price" stroke="#8b5cf6" strokeWidth={2} dot={false} isAnimationActive={false} />
                                <Brush dataKey="time" height={30} stroke="#8b5cf6" fill="#10151e" startIndex={brushRange.startIndex} endIndex={brushRange.endIndex} onChange={(newRange) => setBrushRange({ startIndex: newRange.startIndex, endIndex: newRange.endIndex })} />
                            </LineChart>
                        </ResponsiveContainer>
                    </div>

                    <div className="company-details-card">
                        <h2>About {name}</h2>
                        <p className="company-description">{description}</p>

                        <div className="company-stats-grid">
                            <div className="company-stat-box">
                                <span className="stat-label">Volatility</span>
                                <span className="stat-value">{(volatility * 100).toFixed(2)}%</span>
                            </div>
                            <div className="company-stat-box">
                                <span className="stat-label">Base Drift</span>
                                <span className="stat-value">{(drift * 100).toFixed(2)}%</span>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Right Column: Trading Panel */}
                <div className="stock-page-sidebar">
                    <div className="stock-trade-panel" style={{ height: '100%' }}>
                        <h3 style={{ margin: "0 0 10px 0" }}>Trade {ticker}</h3>

                        {user ? (
                            <div style={{ display: "flex", flexDirection: "column", gap: "15px", marginTop: "20px" }}>
                                <div>
                                    <label style={{ fontSize: "12px", color: "var(--text-muted)" }}>Available Balance</label>
                                    <div style={{ fontSize: "16px", fontWeight: "bold" }}>${user.balance.toFixed(2)}</div>
                                </div>

                                <div>
                                    <label style={{ fontSize: "12px", color: "var(--text-muted)" }}>Shares to Trade</label>
                                    <input
                                        type="number"
                                        min="1"
                                        value={quantity}
                                        onChange={(e) => setQuantity(Number(e.target.value))}
                                        className="login-form-input"
                                        style={{ marginTop: '5px' }}
                                    />
                                </div>

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
                            </div>
                        ) : (
                            <p style={{ color: "var(--text-muted)", fontSize: "14px", marginTop: '20px' }}>
                                Please log in to trade {ticker}.
                            </p>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}