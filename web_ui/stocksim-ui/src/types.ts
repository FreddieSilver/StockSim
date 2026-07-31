export interface User {
    id: number;
    username: string;
    email: string;
    balance: number;
}

export interface UserAuth{
    id: number;
    username: string;
    email: string;
    balance: number;
}

export interface UserRegisterInput {
    username: string;
    email: string;
    password: string;
}

export interface UserLoginInput {
    email: string;
    password: string;
}

export interface TokenOutput {
    token: string;
}


export interface StockUpdate {
    stock_id: number;
    ticker: string;
    price: number;
}

export interface PricePoint {
    time: string;
    price: number;
}

export interface StockData {
    id: number;
    ticker: string;
    price: number;
    previousPrice: number | null;
    history?: PricePoint[];
}

export interface StockDetail {
    id: number;
    company: CompanyDetail;
    price: number
}

export interface CompanyDetail {
    id: number;
    name: string
    ticker: string
    description: string
    volatility: number;
    drift: number;
}

export interface TradeOrderInput {
    stockId: number;
    type: "BUY" | "SELL";
    quantity: number;
}

export interface Holding {
    user: User;
    stock: StockDetail;
    quantity: number;
}

export interface Order {
    stock: StockDetail;
    type: "BUY" | "SELL";
    quantity: number;
    priceValueAtOrder: number;
    status: string;
}



