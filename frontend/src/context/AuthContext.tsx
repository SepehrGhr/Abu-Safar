import React, { createContext, useState, useContext, useEffect } from 'react';
import type { UserInfoDTO } from '../services/api/apiService';

interface AuthContextType {
    isAuthenticated: boolean;
    user: UserInfoDTO | null;
    token: string | null;
    isLoading: boolean;
    login: (token: string, userData: UserInfoDTO) => void;
    logout: () => void;
    updateUser: (userData: UserInfoDTO) => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState<UserInfoDTO | null>(null);
    const [token, setToken] = useState<string | null>(localStorage.getItem('accessToken'));
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        const loadUserFromToken = async () => {
            if (token) {
                try {
                    const storedUser = localStorage.getItem('user');
                    if (storedUser) {
                        setUser(JSON.parse(storedUser));
                    }
                } catch (error) {
                    console.error("Failed to load user from token", error);
                    logout();
                }
            }
            setIsLoading(false);
        };
        loadUserFromToken();
    }, [token]);

    const login = (newToken: string, userData: UserInfoDTO) => {
        localStorage.setItem('accessToken', newToken);
        localStorage.setItem('user', JSON.stringify(userData));
        setToken(newToken);
        setUser(userData);
    };

    const logout = () => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('user');
        setToken(null);
        setUser(null);
    };
    
    const updateUser = (userData: UserInfoDTO) => {
        setUser(userData);
        localStorage.setItem('user', JSON.stringify(userData));
    };

    const value = {
        isAuthenticated: !!token,
        user,
        token,
        isLoading,
        login,
        logout,
        updateUser
    };

    return (
        <AuthContext.Provider value={value}>
            {!isLoading && children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};