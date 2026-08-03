import { useEffect, useRef } from "react";
import type { StockUpdate } from "../types";

interface UseStockStreamOptions {
	onUpdates: (updates: StockUpdate[], currentTime: string) => void;
	onStatusChange?: (status: string) => void;
}

export function useStockStream({ onUpdates, onStatusChange }: UseStockStreamOptions) {
	const handlersRef = useRef({ onUpdates, onStatusChange });

	useEffect(() => {
		handlersRef.current = { onUpdates, onStatusChange };
	}, [onUpdates, onStatusChange]);

	useEffect(() => {
		const eventSource = new EventSource("/stocks/stream");

		eventSource.onopen = () => {
			handlersRef.current.onStatusChange?.("🟢 Live Market Open");
		};

		eventSource.addEventListener("PRICE-UPDATE", (event) => {
			const updates: StockUpdate[] = JSON.parse((event as MessageEvent).data);
			const currentTime = new Date().toLocaleTimeString("en-US", {
				hour12: false,
				hour: "2-digit",
				minute: "2-digit",
				second: "2-digit",
			});

			handlersRef.current.onUpdates(updates, currentTime);
		});

		eventSource.onerror = () => {
			handlersRef.current.onStatusChange?.("🔴 Connection Lost. Reconnecting...");
		};

		return () => eventSource.close();
	}, []);
}

