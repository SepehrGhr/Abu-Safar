import React, { createContext, useState, useContext, useEffect } from 'react';
import { jwtDecode } from 'jwt-decode';
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
        const loadUserFromToken = () => {
            const storedToken = localStorage.getItem('accessToken');
            if (storedToken) {
                try {
                    const decodedToken: { exp: number } = jwtDecode(storedToken);
                    // Check if token is expired
                    if (decodedToken.exp * 1000 < Date.now()) {
                        logout(); // Token is expired, log out
                    } else {
                        const storedUser = localStorage.getItem('user');
                        if (storedUser) {
                            setUser(JSON.parse(storedUser));
                            setToken(storedToken);
                        }
                    }
                } catch (error) {
                    console.error("Invalid token found", error);
                    logout(); // Token is malformed, log out
                }
            }
            setIsLoading(false);
        };
        loadUserFromToken();
    }, []);

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