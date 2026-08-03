import type { Order } from "../../types";

export type SortDirection = "asc" | "desc";
export type OrdersSortKey = "time" | "type" | "asset" | "shares" | "price" | "status";

interface OrdersTableProps {
    orders: Order[];
    sort: { key: OrdersSortKey; direction: SortDirection };
    onSort: (key: OrdersSortKey) => void;
}

export function OrdersTable({ orders, sort, onSort }: OrdersTableProps) {
    return (
        <div className="stock-grid">
            <div className="stock-row stock-header" style={{ gridTemplateColumns: "1fr 1fr 1fr 1.5fr 1fr", cursor: "pointer" }}>
                <span onClick={() => onSort("time")}>Order Time {sort.key === "time" ? (sort.direction === "asc" ? "▲" : "▼") : ""}</span>
                <span onClick={() => onSort("type")}>Type {sort.key === "type" ? (sort.direction === "asc" ? "▲" : "▼") : ""}</span>
                <span onClick={() => onSort("asset")}>Asset {sort.key === "asset" ? (sort.direction === "asc" ? "▲" : "▼") : ""}</span>
                <span onClick={() => onSort("shares")}>Shares {sort.key === "shares" ? (sort.direction === "asc" ? "▲" : "▼") : ""}</span>
                <span onClick={() => onSort("price")}>Price (At Order) {sort.key === "price" ? (sort.direction === "asc" ? "▲" : "▼") : ""}</span>
            </div>

            {orders.length === 0 ? (
                <div className="loading-text" style={{ padding: "30px" }}>No trade history.</div>
            ) : (
                orders.map((order) => (
                    <div className="stock-row" key={order.id} style={{ gridTemplateColumns: "1fr 1fr 1fr 1.5fr 1fr" }}>
                        <div style={{ color: "var(--text-muted)" }}>
                            {order.timestamp || "N/A"}
                        </div>
                        <div className={order.type === "BUY" ? "price-up" : "price-down"}>{order.type}</div>
                        <div style={{ fontWeight: "bold" }}>{order.stock.company.ticker}</div>
                        <div>{order.quantity}</div>
                        <div>${order.priceValueAtOrder.toFixed(2)}</div>
                    </div>
                ))
            )}
        </div>
    );
}

