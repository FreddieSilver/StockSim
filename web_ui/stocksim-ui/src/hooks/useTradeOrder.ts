import { useEffect, useState } from "react";
import { api } from "../api/api.ts";
import { useAuth } from "../contexts/AuthContext";
import { useNotification } from "../contexts/NotificationContext";

export function useTradeOrder(stockId: number, ticker: string) {
    const { user, refreshUser } = useAuth();
    const { notify } = useNotification();

    const [quantity, setQuantity] = useState<number | string>("");
    const [tradeLoading, setTradeLoading] = useState<boolean>(false);
    const [ownedQuantity, setOwnedQuantity] = useState<number>(0);

    const fetchOwnedQuantity = async () => {
        if (!user) return;

        try {
            const holdings = await api.getMyHoldings();
            const holding = holdings.find((h) => h.stock.id === stockId);
            setOwnedQuantity(holding ? holding.quantity : 0);
        } catch (error) {
            console.error("Failed to fetch holdings", error);
        }
    };

    useEffect(() => {
        fetchOwnedQuantity();
    }, [user, stockId]);

    const handleTrade = async (type: "BUY" | "SELL") => {
        if (!user) return;

        const numQuantity = Number(quantity);
        if (numQuantity <= 0) return;

        setTradeLoading(true);
        try {
            await api.placeOrder({ stockId, type, quantity: numQuantity });
            notify(`Successfully placed ${type.toLowerCase()} order for ${numQuantity} shares of ${ticker}!`, "success");
            await refreshUser();
            await fetchOwnedQuantity();
            setQuantity("");
        } catch (error: any) {
            notify(error.message || "Trade failed", "error");
        } finally {
            setTradeLoading(false);
        }
    };

    return { quantity, setQuantity, tradeLoading, handleTrade, user, ownedQuantity };
}

