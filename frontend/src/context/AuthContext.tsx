import React, { createContext, useState, useContext, useEffect } from 'react';
import { jwtDecode } from 'jwt-decode';
import { getUserDetails } from '../services/api/apiService';
import type { UserInfoDTO } from '../services/api/apiService';

interface AuthContextType {
    isAuthenticated: boolean;
    user: UserInfoDTO | null;
    token: string | null;
    isLoading: boolean;
    login: (token: string, initialUserData: UserInfoDTO) => Promise<void>;
    logout: () => void;
    updateUser: (userData: UserInfoDTO) => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState<UserInfoDTO | null>(null);
    const [token, setToken] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        const loadUserFromToken = async () => {
            const storedToken = localStorage.getItem('accessToken');
            if (storedToken) {
                try {
                    const decodedToken: { exp: number } = jwtDecode(storedToken);
                    if (decodedToken.exp * 1000 < Date.now()) {
                        logout(); // Token is expired
                    } else {
                        setToken(storedToken);
                        const userDetails = await getUserDetails();
                        setUser(userDetails);
                    }
                } catch (error) {
                    console.error("Failed to load user from token", error);
                    logout();
                }
            }
            setIsLoading(false);
        };
        loadUserFromToken();
    }, []);

    const login = async (newToken: string, initialUserData: UserInfoDTO) => {
        localStorage.setItem('accessToken', newToken);
        setToken(newToken);
        
        try {
            const fullUserDetails = await getUserDetails();
            setUser(fullUserDetails);
            localStorage.setItem('user', JSON.stringify(fullUserDetails));
        } catch (error) {
            console.error("Failed to fetch full user details after login", error);
            setUser(initialUserData);
            localStorage.setItem('user', JSON.stringify(initialUserData));
        }
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