import "../../styles/App.css";

type ToastType = "success" | "error" | "info";

interface ToastProps {
    message: string;
    type: ToastType;
}

export function Toast({ message, type }: ToastProps) {
    return <div className={`toast toast-${type}`}>{message}</div>;
}

