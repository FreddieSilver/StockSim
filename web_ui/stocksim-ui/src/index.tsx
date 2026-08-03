import "./styles/App.css";
import { createRoot } from "react-dom/client";
import { createBrowserRouter, RouterProvider } from "react-router";
import { AuthProvider } from "./contexts/AuthContext";
import { Layout } from "./components/Layout";
import TitlePage from "./pages/TitlePage.tsx";
import { Login } from "./pages/auth/Login";
import { Register } from "./pages/auth/Register";
import { UserPage } from "./pages/profile/UserPage.tsx";
import { ProtectedRoute } from "./pages/auth/ProtectedRoute";
import { MarketPage } from "./pages/market/MarketPage";
import { StockPage } from "./pages/stock/StockPage";
import { NotificationProvider } from "./contexts/NotificationContext";

const router = createBrowserRouter([
    {
        path: "/",
        element: <Layout />,
        children: [
            { path: "", element: <TitlePage /> },
            { path: "login", element: <Login /> },
            { path: "register", element: <Register /> },
            { path: "market", element: <MarketPage /> },
            { path: "stock/:id", element: <StockPage /> },
            {
                path: "me",
                element: (
                    <ProtectedRoute>
                        <UserPage />
                    </ProtectedRoute>
                ),
            },
        ],
    },
]);

const container =
    document.getElementById("root") ??
    (() => {
        const el = document.createElement("div");
        el.id = "root";
        document.body.appendChild(el);
        return el;
    })();

createRoot(container).render(
    <AuthProvider>
        <NotificationProvider>
            <RouterProvider router={router} />
        </NotificationProvider>
    </AuthProvider>
);