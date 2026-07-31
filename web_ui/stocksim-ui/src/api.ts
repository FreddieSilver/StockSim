import type {
    UserRegisterInput,
    UserLoginInput,
    TokenOutput,
    UserAuth,
    PricePoint, TradeOrderInput, StockDetail, Holding, Order
} from "./types";
//import { getErrorDescription } from "./errorDescriptions";

const API_BASE_URL = "";

class ApiError extends Error {
    public status: number;
    constructor(status: number, message: string) {
        super(message);
        this.status = status;
    }
}

export function getAuthHeaders(): Record<string, string> {
    const headers: Record<string, string> = {
        "Content-Type": "application/json",
        "Accept": "application/json"
    };

    // fetch the token from localStorage
    const token = localStorage.getItem("stocksim_token");
    if (token) {
        headers["Authorization"] = `Bearer ${token}`;
    }

    return headers;
}

async function fetchApi<T>(path: string, options: RequestInit = {}): Promise<T> {
    const response = await fetch(`${API_BASE_URL}${path}`, {
        ...options,
        headers: {
            ...getAuthHeaders(),
            ...options.headers,
        },
    });

    if (!response.ok) {
        const error = await response.json().catch(() => ({ error: "Unknown error" }));
        // {"error": "..."} for failures
        throw new ApiError(response.status, error.error || response.statusText);
    }

    if (response.status === 204 || response.status === 201) {
        const text = await response.text();
        return text ? JSON.parse(text) : (undefined as T);
    }

    return response.json();
}

export const api = {
    async createUser(input: UserRegisterInput): Promise<TokenOutput> {
        return fetchApi<TokenOutput>("/users", {
            method: "POST",
            body: JSON.stringify(input),
        });
    },

    async login(input: UserLoginInput): Promise<TokenOutput> {
        const res = await fetchApi<TokenOutput>("/users/login", {
            method: "POST",
            body: JSON.stringify(input),
        });
        // save the token when we login
        localStorage.setItem("stocksim_token", res.token);
        return res;
    },

    async getMe(): Promise<UserAuth> {
        return fetchApi<UserAuth>("/me");
    },

    async logout(): Promise<void> {
        await fetchApi<void>("/users/logout", {
            method: "POST",
        }).catch(() => {}); // ignore errors on logout

        // remove the token when we logout
        localStorage.removeItem("stocksim_token");
    },

    async getStockHistory(stockId: number): Promise<PricePoint[]> {
        return fetchApi<PricePoint[]>(`/stocks/${stockId}/history`, {
            method: "GET",
        });
    },

    async deposit(amount: number): Promise<void> {
        await fetchApi<void>("/me/deposit", {
            method: "POST",
            body: JSON.stringify({ amount }),
        });
    },

    async placeOrder(input: TradeOrderInput): Promise<void> {
        await fetchApi<void>("/trade-orders/place", {
            method: "POST",
            body: JSON.stringify(input),
        });
    },

    async getStock(stockId: number): Promise<StockDetail> {
        return fetchApi<StockDetail>(`/stocks/${stockId}`, {
            method: "GET",
        });
    },

    async getMyHoldings(): Promise<Holding[]> {
        return fetchApi<Holding[]>("/me/holdings", { method: "GET" });
    },

    async getMyOrders(): Promise<Order[]> {
        return fetchApi<Order[]>("/me/orders", { method: "GET" });
    }
};

export { ApiError };