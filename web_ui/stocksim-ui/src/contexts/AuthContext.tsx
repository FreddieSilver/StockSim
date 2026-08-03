import React, {createContext, useContext, useState, useCallback, useEffect, useMemo} from "react";
import type { UserAuth } from "../types";
import { api } from "../api/api.ts";


interface AuthContextType {
    user: UserAuth | null;
    login: () => Promise<void>;
    logout: () => Promise<void>;
    isLoading: boolean;
    refreshUser: () => Promise<void>; // Added
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: Readonly<{ children: React.ReactNode }>) {
    const [user, setUser] = useState<UserAuth | null>(null);
    const [isLoading, setIsLoading] = useState<boolean>(true);

    // Function to check if the cookie is valid by asking the server
    const checkSession = useCallback(async () => {
        try {
            setIsLoading(true);
            const userData = await api.getMe();
            setUser(userData);
        } catch (error) {
            console.log(error || "No active session found");
            setUser(null);
        } finally {
            setIsLoading(false);
        }
    }, []);

    // Check session on mount (refreshing the page)
    useEffect(() => {
        checkSession();
    }, [checkSession]);

    // LOGIN:
    // The Login component calls API -> API sets Cookie -> We call this to update UI
    const login = async () => {
        await checkSession();
    };

    // LOGOUT:
    // We must tell server to delete cookie, then clear local state
    const logout = async () => {
        try {
            await api.logout();
        } catch (e) {
            console.error("Logout failed", e);
        } finally {
            setUser(null);
        }
    };

    const contextValue = useMemo(() => ({
        user,
        login,
        logout,
        isLoading,
        refreshUser: checkSession
    }), [user, login, logout, isLoading, checkSession]);

    return (
        <AuthContext.Provider value={contextValue}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within AuthProvider");
  }
  return context;
}

