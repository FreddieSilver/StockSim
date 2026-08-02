import "./styles/App.css";
import { createRoot } from "react-dom/client";
import { createBrowserRouter, RouterProvider } from "react-router";
import { AuthProvider } from "./AuthContext";
import {Layout} from "./components/Layout.tsx";
import TitleScreen from "./components/TitleScreen.tsx";
import {Login} from "./components/Login.tsx";
import {Register} from "./components/Register.tsx";
import {UserProfile} from "./components/UserProfile.tsx";
import {ProtectedRoute} from "./components/ProtectedRoute.tsx";
import {Market} from "./components/Market.tsx";
import {StockScreen} from "./components/StockScreen.tsx";
import {NotificationProvider} from "./NotificationContext.tsx";

const router = createBrowserRouter([
    {
        path: "/",
        element: <Layout />,
        children: [
            { path: "", element: <TitleScreen /> },
            { path: "login", element: <Login /> },
            { path: "register", element: <Register /> },
            { path: "market", element: <Market /> },
            { path: "stock/:id", element: <StockScreen /> },
            {
                path: "me",
                element: (
                    <ProtectedRoute>
                        <UserProfile />
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