import axios from 'axios';

// The SignUpRequestDTO interface to match your backend DTO
export interface SignUpData {
    firstName: string;
    lastName: string;
    city: string;
    password: string;
    email?: string;
    phoneNumber?: string;
}

// Create an axios instance with the base URL
const apiClient = axios.create({
    baseURL: 'http://localhost:8888',
    headers: {
        'Content-Type': 'application/json',
    }
});

// --- API Functions ---

/**
 * Requests an OTP for a given email or phone number.
 * @param contactInfo The user's email or phone.
 * @returns The success message from the backend.
 */
export const requestLoginOtp = async (contactInfo: string) => {
    const response = await apiClient.post('/api/auth/login/otp/request', { contactInfo });
    return response.data;
};

/**
 * Verifies the OTP and returns user data and a token upon success.
 * @param contactInfo The user's email or phone.
 * @param otp The 6-digit code.
 * @returns The login response data (token, user info).
 */
export const verifyLoginOtp = async (contactInfo: string, otp: string) => {
    const response = await apiClient.post('/api/auth/login/otp/verify', { contactInfo, otp });
    return response.data;
};

/**
 * Registers a new user.
 * @param userData The user's signup information.
 * @returns The newly created user's information.
 */
export const signUpUser = async (userData: SignUpData) => {
    const response = await apiClient.post('/api/auth/signup', userData);
    return response.data;
};