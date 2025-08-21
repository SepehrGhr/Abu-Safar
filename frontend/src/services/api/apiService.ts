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

export interface TicketResultItemDTO {
    tripId: number;
    age: 'ADULT' | 'CHILD' | 'BABY';
    originCity: string;
    destinationCity: string;
    departureTimestamp: string;
    arrivalTimestamp: string;
    tripVehicle: 'TRAIN' | 'BUS' | 'FLIGHT';
    price: number;
    vehicleCompany: string;
}

export interface ReserveRecordItemDTO {
    status: 'UPCOMING_TRIP' | 'PAST_TRIP' | 'CANCELLED' | 'PENDING_PAYMENT';
    reservationId: number;
    paymentId: number;
    paymentTimestamp: string;
    seatNumbers: number[];
    isRoundTrip: boolean;
    ticketsInformation: TicketResultItemDTO[];
}

export interface PaymentRecordDTO {
    paymentId: number;
    reservationId: number;
    paymentStatus: 'SUCCESSFUL' | 'UNSUCCESSFUL' | 'PENDING';
    paymentType: 'CARD' | 'WALLET' | 'CRYPTO';
    paymentTimestamp: string;
    price: number;
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

// --- Wallet Functions ---
export const chargeWallet = async (amount: number) => {
    const response = await apiClient.post('/api/wallet/charge', { amount });
    return response.data;
};


export const getReservationHistory = async (status: string) => {
    const response = await apiClient.get('/api/bookings/history', {
        params: { status }
    });
    return response.data;
};

// --- Booking History & Cancellation Functions ---
export const calculateCancellationPenalty = async (reservationId: number) => {
    const response = await apiClient.post('/api/booking/cancel/calculate', { reservationId });
    return response.data;
};

export const cancelReservation = async (reservationId: number) => {
    const response = await apiClient.post('/api/booking/cancel/confirm', { reservationId });
    return response.data;
};

// --- Payment History Functions ---

export const getPaymentHistory = async () => {
    const response = await apiClient.get('/api/payments');
    return response.data;
};