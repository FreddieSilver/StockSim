import React from "react";
import { Navigate } from "react-router";
import { useAuth } from "../AuthContext";

export function ProtectedRoute({ children }: { children: React.ReactNode }) {
    const { user, isLoading } = useAuth();

    if (isLoading) {
        return (
            <div className="page-loading">
                <div className="loading-spinner" />
                <span>Loading your account...</span>
            </div>
        );
    }

    if (!user) {
        return <Navigate to="/login" replace />;
    }

    return <>{children}</>;
}