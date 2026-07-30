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



