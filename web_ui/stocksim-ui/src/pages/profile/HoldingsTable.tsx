import type { Holding } from "../../types";

export type SortDirection = "asc" | "desc";
export type HoldingsSortKey = "asset" | "shares" | "price" | "value" | "return";

interface HoldingsTableProps {
    holdings: Holding[];
    sort: { key: HoldingsSortKey; direction: SortDirection };
    onSort: (key: HoldingsSortKey) => void;
    getAvgBuyPrice: (stockId: number) => number;
}

export function HoldingsTable({ holdings, sort, onSort, getAvgBuyPrice }: HoldingsTableProps) {
    return (
        <div className="stock-grid" style={{ marginBottom: "40px" }}>
            <div className="stock-row stock-header" style={{ gridTemplateColumns: "1.5fr 1fr 1fr 1fr 1fr", cursor: "pointer" }}>
                <span onClick={() => onSort("asset")}>Asset {sort.key === "asset" ? (sort.direction === "asc" ? "▲" : "▼") : ""}</span>
                <span onClick={() => onSort("shares")}>Shares {sort.key === "shares" ? (sort.direction === "asc" ? "▲" : "▼") : ""}</span>
                <span onClick={() => onSort("price")}>Current Price {sort.key === "price" ? (sort.direction === "asc" ? "▲" : "▼") : ""}</span>
                <span onClick={() => onSort("value")}>Total Value {sort.key === "value" ? (sort.direction === "asc" ? "▲" : "▼") : ""}</span>
                <span onClick={() => onSort("return")}>Total Return {sort.key === "return" ? (sort.direction === "asc" ? "▲" : "▼") : ""}</span>
            </div>

            {holdings.length === 0 ? (
                <div className="loading-text" style={{ padding: "30px" }}>You don't own any stocks yet.</div>
            ) : (
                holdings.map((holding) => {
                    const avgPrice = getAvgBuyPrice(holding.stock.id);
                    const totalReturn = (holding.stock.price - avgPrice) * holding.quantity;
                    const returnPercent = avgPrice > 0 ? ((holding.stock.price - avgPrice) / avgPrice) * 100 : 0;
                    const isPositive = totalReturn >= 0;

                    return (
                        <div className="stock-row" key={holding.stock.id} style={{ gridTemplateColumns: "1.5fr 1fr 1fr 1fr 1fr" }}>
                            <div style={{ fontWeight: "bold" }}>{holding.stock.company.ticker}</div>
                            <div>{holding.quantity}</div>
                            <div>${holding.stock.price.toFixed(2)}</div>
                            <div style={{ fontWeight: "bold" }}>${(holding.quantity * holding.stock.price).toFixed(2)}</div>
                            <div className={isPositive ? "price-up" : "price-down"}>
                                {isPositive ? "+" : ""}${totalReturn.toFixed(2)} ({isPositive ? "+" : ""}{returnPercent.toFixed(2)}%)
                            </div>
                        </div>
                    );
                })
            )}
        </div>
    );
}

