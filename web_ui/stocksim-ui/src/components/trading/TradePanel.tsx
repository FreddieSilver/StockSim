import { useState } from "react";
import { Link } from "react-router";
import type { UserAuth } from "../../types";

interface TradePanelProps {
    stockId: number;
    ticker: string;
    price: number | string;
    user: UserAuth | null;
    quantity: number | string;
    setQuantity: (q: number | string) => void;
    onTrade: (type: "BUY" | "SELL") => void;
    tradeLoading: boolean;
    ownedQuantity?: number;
    showInfoLink?: boolean;
}

export function TradePanel({ stockId, ticker, price, user, quantity, setQuantity, onTrade, tradeLoading, ownedQuantity = 0, showInfoLink }: TradePanelProps) {
    const [action, setAction] = useState<"BUY" | "SELL">("BUY");
    const numericPrice = Number(price);
    const numQuantity = Number(quantity);

    // Fractional Math: Max they can afford vs Max they own
    const maxBuy = user && numericPrice > 0 ? (user.balance / numericPrice) : 0;
    const maxSliderValue = action === "BUY" ? maxBuy : ownedQuantity;
    const estimatedCost = numQuantity * numericPrice;

    // Validation
    const isOverBalance = action === "BUY" && estimatedCost > (user?.balance || 0);
    const isOverOwned = action === "SELL" && numQuantity > ownedQuantity;
    const isInvalid = numQuantity <= 0 || isOverBalance || isOverOwned;

    return (
        <div className="stock-trade-panel" style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>

            {/* Tabs for Buy / Sell */}
            <div className="trade-tabs">
                <button className={`trade-tab ${action === "BUY" ? "active-buy" : ""}`} onClick={() => { setAction("BUY"); setQuantity(""); }}>Buy</button>
                <button className={`trade-tab ${action === "SELL" ? "active-sell" : ""}`} onClick={() => { setAction("SELL"); setQuantity(""); }}>Sell</button>
            </div>

            <div style={{ marginBottom: "15px", marginTop: "15px", color: "var(--text-secondary)", display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span>Current Price:</span>
                <strong style={{ color: "var(--text)", fontSize: "18px" }}>${numericPrice.toFixed(2)}</strong>
            </div>

            {user ? (
                <div style={{ display: "flex", flexDirection: "column", gap: "10px", flex: 1 }}>

                    {/* Contextual Header (Balance vs Owned) */}
                    <div style={{ display: "flex", justifyContent: "space-between", paddingBottom: "10px", borderBottom: "1px solid var(--border)" }}>
                        <span style={{ fontSize: "12px", color: "var(--text-muted)" }}>
                            {action === "BUY" ? "Available Cash" : "Shares Owned"}
                        </span>
                        <span style={{ fontSize: "13px", fontWeight: "bold", color: "var(--text)" }}>
                            {action === "BUY" ? `$${user.balance.toFixed(2)}` : `${ownedQuantity.toFixed(4)} Shares`}
                        </span>
                    </div>

                    {/* Number Input (Fractional) */}
                    <label style={{ fontSize: "12px", color: "var(--text-muted)", marginTop: "5px" }}>Shares to {action.toLowerCase()}</label>
                    <input
                        type="number"
                        min="0"
                        step="0.01" // Allows decimals
                        value={quantity}
                        onChange={(e) => setQuantity(e.target.value)}
                        className="login-form-input"
                        placeholder="0.00"
                    />

                    {/* --- THE CUSTOM PRETTY SLIDER --- */}
                    <div style={{ marginTop: "15px", marginBottom: "15px" }}>
                        <input
                            type="range"
                            min="0"
                            max={maxSliderValue || 0.01}
                            step="0.01"
                            value={numQuantity}
                            onChange={(e) => setQuantity(e.target.value)}
                            className={`custom-slider ${action === "BUY" ? "buy-slider" : "sell-slider"}`}
                            disabled={maxSliderValue <= 0}
                        />
                        <div style={{ display: "flex", justifyContent: "space-between", fontSize: "11px", color: "var(--text-muted)", marginTop: "8px" }}>
                            <span>0</span>
                            <span>{maxSliderValue.toFixed(4)} Max</span>
                        </div>
                    </div>

                    {/* Estimated Cost / Value */}
                    <div style={{ display: "flex", justifyContent: "space-between", fontSize: "14px", marginTop: "auto", paddingBottom: "15px" }}>
                        <span style={{ color: "var(--text-muted)" }}>Estimated {action === "BUY" ? "Cost" : "Credit"}:</span>
                        <strong style={{ color: isOverBalance ? "var(--red)" : "var(--text)" }}>
                            ${estimatedCost.toFixed(2)}
                        </strong>
                    </div>

                    {/* Execute Trade Button */}
                    <button
                        className="btn-primary"
                        style={{
                            background: action === "BUY" ? 'var(--green)' : 'var(--red)',
                            width: '100%',
                            height: '44px',
                            opacity: isInvalid ? 0.5 : 1
                        }}
                        onClick={() => onTrade(action)}
                        disabled={tradeLoading || isInvalid}
                    >
                        {action} {ticker}
                    </button>

                    {showInfoLink && (
                        <div style={{ marginTop: '10px', paddingTop: '15px', borderTop: '1px solid var(--border)' }}>
                            <Link to={`/stock/${stockId}`} className="btn-primary-outline" style={{ width: '100%', textDecoration: 'none', display: 'flex' }}>
                                View Stock Info →
                            </Link>
                        </div>
                    )}
                </div>
            ) : (
                <p style={{ color: "var(--text-muted)", fontSize: "14px", marginTop: "15px" }}>Please log in to trade.</p>
            )}
        </div>
    );
}
