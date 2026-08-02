
import './styles/App.css';
import {createContext, type ReactNode, useContext, useState} from "react";

type NotificationType = 'success' | 'error' | 'info';

interface Notification {
    id: number;
    message: string;
    type: NotificationType;
}

interface NotificationContextType {
    notify: (message: string, type?: NotificationType) => void;
}

const NotificationContext = createContext<NotificationContextType | undefined>(undefined);

export function NotificationProvider({ children }: { children: ReactNode }) {
    const [notifications, setNotifications] = useState<Notification[]>([]);

    const notify = (message: string, type: NotificationType = 'info') => {
        const id = Date.now();
        setNotifications(prev => [...prev, { id, message, type }]);

        // auto-remove after 4 seconds
        setTimeout(() => {
            setNotifications(prev => prev.filter(n => n.id !== id));
        }, 4000);
    };

    return (
        <NotificationContext.Provider value={{ notify }}>
            {children}
            <div className="toast-container">
                {notifications.map(n => (
                    <div key={n.id} className={`toast toast-${n.type}`}>
                        {n.message}
                    </div>
                ))}
            </div>
        </NotificationContext.Provider>
    );
}

export const useNotification = () => {
    const context = useContext(NotificationContext);
    if (!context) throw new Error("useNotification must be used within NotificationProvider");
    return context;
};