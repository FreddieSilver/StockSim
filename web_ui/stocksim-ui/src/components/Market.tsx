import { useEffect, useState } from 'react';
import {
    LineChart, Line, XAxis, YAxis, Tooltip, ResponsiveContainer, ReferenceLine
} from 'recharts';
import type { StockData, StockUpdate } from '../types';
import { api } from '../api';
import '../styles/App.css';

export function Market() {
    const [stocks, setStocks] = useState<Record<number, StockData>>({});
    const [status, setStatus] = useState<string>('Connecting...');
    const [selectedStockId, setSelectedStockId] = useState<number | null>(null);

    useEffect(() => {
        const eventSource = new EventSource('/stocks/stream');

        eventSource.onopen = () => setStatus('🟢 Live Market Open');

        eventSource.addEventListener('PRICE-UPDATE', (event) => {
            const updates: StockUpdate[] = JSON.parse(event.data);
            const currentTime = new Date().toLocaleTimeString('en-US', { hour12: false, hour: "2-digit", minute: "2-digit", second: "2-digit" });

            setStocks((prev) => {
                const next = { ...prev };
                updates.forEach((update) => {
                    const current = next[update.stock_id];

                    // Maintain a rolling history for the chart
                    const history = current?.history ? [...current.history] : [];
                    history.push({ time: currentTime, price: update.price });
                    if (history.length > 60) history.shift(); // Keep max 60 points in memory to prevent lag

                    next[update.stock_id] = {
                        id: update.stock_id,
                        ticker: update.ticker || current?.ticker || `ID:${update.stock_id}`,
                        previousPrice: current ? current.price : null,
                        price: update.price,
                        history: history,
                    };
                });
                return next;
            });
        });

        eventSource.onerror = () => setStatus('🔴 Connection Lost. Reconnecting...');

        return () => eventSource.close();
    }, []);

    // Handle clicking a stock row
    const toggleStockSelection = async (id: number) => {
        if (selectedStockId === id) {
            setSelectedStockId(null);
            return;
        }

        setSelectedStockId(id);

        try {
            const history = await api.getStockHistory(id);
            setStocks((prev) => {
                // We merge the fetched history with the existing stock state
                if (!prev[id]) return prev;
                return {
                    ...prev,
                    [id]: {
                        ...prev[id],
                        history: history
                    }
                };
            });
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
                    {status.replace("🟢 ", "").replace("🔴 ", "")}
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

                {Object.values(stocks).length === 0 && (
                    <div className="loading-text">
                        Waiting for market data...
                    </div>
                )}

                {Object.values(stocks).map((stock) => {
                    const isUp = stock.previousPrice !== null && stock.price > stock.previousPrice;
                    const isDown = stock.previousPrice !== null && stock.price < stock.previousPrice;
                    const priceClass = isUp ? "price-up" : isDown ? "price-down" : "price-neutral";
                    const change = stock.previousPrice !== null ? stock.price - stock.previousPrice : 0;
                    const changePercent = stock.previousPrice !== null ? (change / stock.previousPrice) * 100 : 0;

                    const isSelected = selectedStockId === stock.id;

                    return (
                        <div key={stock.id} className="stock-item-container">
                            {/* Clickable Row */}
                            <div
                                className={`stock-row ${isSelected ? 'selected' : ''}`}
                                onClick={() => toggleStockSelection(stock.id)}
                            >
                                <div className="stock-symbol">
                                    <div className="stock-icon">
                                        {stock.ticker.slice(0, 2)}
                                    </div>
                                    <div>
                                        <div className="stock-name">{stock.ticker}</div>
                                        <div className="stock-ticker">Stock #{stock.id}</div>
                                    </div>
                                </div>

                                <div className="stock-price">${stock.price.toFixed(2)}</div>

                                <div className={priceClass}>
                                    {stock.previousPrice === null ? "—" : `${change >= 0 ? "+" : ""}$${change.toFixed(2)}`}
                                </div>

                                <div className={priceClass}>
                                    {stock.previousPrice === null ? "—" : `${changePercent >= 0 ? "+" : ""}${changePercent.toFixed(2)}%`}
                                </div>

                                <div className={priceClass}>
                                    {isUp ? "▲" : isDown ? "▼" : "—"}
                                </div>
                            </div>

                            {/* Collapsible Chart Section */}
                            {isSelected && stock.history && (
                                <div className="stock-chart-container">
                                    <ResponsiveContainer width="100%" height={250}>
                                        <LineChart data={stock.history}>
                                            <XAxis
                                                dataKey="time"
                                                stroke="#64748b"
                                                fontSize={12}
                                                tickMargin={10}
                                                minTickGap={20}
                                            />
                                            <YAxis
                                                domain={['auto', 'auto']}
                                                stroke="#64748b"
                                                fontSize={12}
                                                tickFormatter={(val) => `$${val}`}
                                                width={60}
                                            />
                                            <Tooltip
                                                contentStyle={{ backgroundColor: '#10151e', borderColor: '#303b4c', borderRadius: '8px' }}
                                                itemStyle={{ color: '#8b5cf6' }}
                                                labelStyle={{ color: '#94a3b8', marginBottom: '4px' }}
                                                formatter={(value: number) => [`$${value.toFixed(2)}`, "Price"]}
                                            />
                                            {/* Draws a reference line at the oldest price point to show baseline */}
                                            {stock.history.length > 0 && (
                                                <ReferenceLine y={stock.history[0]?.price} stroke="#303b4c" strokeDasharray="3 3" />
                                            )}
                                            <Line
                                                type="monotone"
                                                dataKey="price"
                                                stroke="#8b5cf6"
                                                strokeWidth={2}
                                                dot={false}
                                                isAnimationActive={false} // Disable animation to prevent jumpiness on fast streams
                                            />
                                        </LineChart>
                                    </ResponsiveContainer>
                                </div>
                            )}
                        </div>
                    );
                })}
            </div>
        </div>
    );
}