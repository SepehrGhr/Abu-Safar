import apiClient from './apiClient';

// --- Type Definitions ---

export interface UserInfoDTO {
    id: number;
    firstName: string;
    lastName: string;
    city: string;
    userType: string;
    signUpDate: string;
    walletBalance: number;
    birthdayDate?: string;
    profilePictureUrl?: string;
}

export interface SignUpData {
    firstName: string;
    lastName: string;
    city: string;
    password: string;
    email?: string;
    phoneNumber?: string;
}

export interface UserUpdateData {
    firstName?: string;
    lastName?: string;
    city?: string;
    email?: string;
    phoneNumber?: string;
    birthdayDate?: string;
}

// --- Auth Functions ---
export const requestLoginOtp = async (contactInfo: string) => {
    const response = await apiClient.post('/api/auth/login/otp/request', { contactInfo });
    return response.data;
};

export const verifyLoginOtp = async (contactInfo: string, otp: string) => {
    const response = await apiClient.post('/api/auth/login/otp/verify', { contactInfo, otp });
    return response.data;
};

export const signUpUser = async (userData: SignUpData) => {
    const response = await apiClient.post('/api/auth/signup', userData);
    return response.data;
};

// --- Profile Functions ---
export const updateUserInfo = async (updateData: UserUpdateData) => {
    const response = await apiClient.put('/api/profile/update', updateData);
    return response.data;
};