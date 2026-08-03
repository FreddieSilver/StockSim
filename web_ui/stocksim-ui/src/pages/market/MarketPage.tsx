import { useReducer } from "react";
import { api } from "../../api/api.ts";
import { StockChart } from "../../components/charts/StockChart";
import { TradePanel } from "../../components/trading/TradePanel";
import { useStockStream } from "../../hooks/useStockStream";
import { useTradeOrder } from "../../hooks/useTradeOrder";
import type { PricePoint, StockData, StockUpdate } from "../../types";
import "../../styles/App.css";

type StockStreamUpdateHandler = (updates: StockUpdate[], currentTime: string) => void;
type StockStreamStatusHandler = (status: string) => void;

type State = {
    stocks: Record<number, StockData>;
    marketStatus: string;
    expandedStockIds: number[];
};

type Action =
    | { type: "update-stocks"; updates: StockUpdate[]; currentTime: string }
    | { type: "set-history"; stockId: number; history: PricePoint[] }
    | { type: "set-status"; status: string }
    | { type: "toggle-stock"; stockId: number };

const initialState: State = { stocks: {}, marketStatus: 'Connecting...', expandedStockIds: [] };

function reducer(state: State, action: Action): State {
    switch (action.type) {
        case "update-stocks": {
            const nextStocks = { ...state.stocks };
            action.updates.forEach((update) => {
                const current = nextStocks[update.stock_id];
                const history = current?.history ? [...current.history] : [];

                history.push({ timestamp: action.currentTime, price: update.price });
                if (history.length > 150) history.shift();

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
        case "set-history":
            if (!state.stocks[action.stockId]) return state;
            return { ...state, stocks: { ...state.stocks, [action.stockId]: { ...state.stocks[action.stockId], history: action.history } } };
        case "set-status":
            return { ...state, marketStatus: action.status };
        case "toggle-stock": {
            const isExpanded = state.expandedStockIds.includes(action.stockId);
            return { ...state, expandedStockIds: isExpanded ? state.expandedStockIds.filter(id => id !== action.stockId) : [...state.expandedStockIds, action.stockId] };
        }
        default: return state;
    }
}

export function MarketPage() {
    const [state, dispatch] = useReducer(reducer, initialState);

    useStockStream({
        onUpdates: ((updates: StockUpdate[], currentTime: string) => dispatch({ type: "update-stocks", updates, currentTime })) as StockStreamUpdateHandler,
        onStatusChange: ((status: string) => dispatch({ type: "set-status", status })) as StockStreamStatusHandler,
    });

    const toggleStockSelection = async (id: number) => {
        const isCurrentlyExpanded = state.expandedStockIds.includes(id);
        dispatch({ type: "toggle-stock", stockId: id });

        if (!isCurrentlyExpanded) {
            try {
                const rawHistory = await api.getStockHistory(id, "5M");
                const formattedHistory = rawHistory.map((p: any) => ({ timestamp: p.timestamp || p.time, price: p.price }));
                dispatch({ type: "set-history", stockId: id, history: formattedHistory });
            } catch (error) {
                console.error("Failed to fetch history");
            }
        }
    };

    return (
        <div className="dashboard-container">
            <div className="market-header">
                <div className="market-title-group">
                    <h1>Markets</h1>
                    <p>Track simulated market prices in real time. Click a stock to view its chart.</p>
                </div>
                <div className="status-badge">{state.marketStatus.replace("🟢 ", "").replace("🔴 ", "")}</div>
            </div>

            <div className="stock-grid">
                <div className="stock-row stock-header">
                    <span>Asset</span><span>Price</span><span>Change</span><span>Movement</span>
                </div>

                {Object.values(state.stocks).length === 0 && <div className="loading-text">Waiting for market data...</div>}

                {Object.values(state.stocks).map((stock) => {
                    const isUp = stock.previousPrice !== null && stock.price > stock.previousPrice;
                    const priceClass = isUp ? "price-up" : (stock.previousPrice !== null && stock.price < stock.previousPrice) ? "price-down" : "price-neutral";
                    const change = stock.previousPrice !== null ? stock.price - stock.previousPrice : 0;
                    const changePercent = stock.previousPrice !== null ? (change / stock.previousPrice) * 100 : 0;
                    const isExpanded = state.expandedStockIds.includes(stock.id);

                    return (
                        <div key={stock.id} className="stock-item-container">
                            <div className={`stock-row ${isExpanded ? 'selected' : ''}`} onClick={() => toggleStockSelection(stock.id)}>
                                <div className="stock-symbol">
                                    <div className="stock-icon">{stock.ticker.slice(0, 2)}</div>
                                    <div><div className="stock-name">{stock.ticker}</div></div>
                                </div>
                                <div className="stock-price">${stock.price.toFixed(2)}</div>
                                <div className={priceClass}>{stock.previousPrice === null ? "—" : `${change >= 0 ? "+" : ""}$${change.toFixed(2)}`}</div>
                                <div className={priceClass}>{stock.previousPrice === null ? "—" : `${changePercent >= 0 ? "+" : ""}${changePercent.toFixed(2)}%`}</div>
                                <div className={priceClass}>{isUp ? "▲" : stock.previousPrice !== null && stock.price < stock.previousPrice ? "▼" : "—"}</div>
                            </div>

                            {isExpanded && <ExpandedStockView stock={stock} />}
                        </div>
                    );
                })}
            </div>
        </div>
    );
}

function ExpandedStockView({ stock }: { stock: StockData }) {
    const { quantity, setQuantity, tradeLoading, handleTrade, user, ownedQuantity } = useTradeOrder(stock.id, stock.ticker);

    return (
        <div className="stock-expanded-view">
            <div className="stock-chart-container">
                <StockChart data={stock.history || []} />
            </div>
            <TradePanel
                stockId={stock.id}
                ticker={stock.ticker}
                price={stock.price}
                user={user}
                quantity={quantity}
                setQuantity={setQuantity}
                onTrade={handleTrade}
                tradeLoading={tradeLoading}
                ownedQuantity={ownedQuantity}
                showInfoLink={true}
            />
        </div>
    );
}

