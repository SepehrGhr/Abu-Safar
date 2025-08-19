import React, { useState, useRef } from 'react';
import { AnimatePresence, motion } from 'framer-motion';
import { Mail, KeyRound, User, Users, MapPin, Phone, CheckCircle, ChevronLeft, ChevronRight, AlertCircle } from 'lucide-react';
import Page from './Page';
import { PageInfo } from './PageInfo';
import { InputField, ShinyButton, Footer, Spinner, PasswordStrengthMeter } from './common';
import { requestLoginOtp, verifyLoginOtp, signUpUser } from '../../services/api/authService';
import type { SignUpData } from '../../services/api/authService';

export default function PassportInterior({ isOpen, onSignOut }) {
    const [currentPage, setCurrentPage] = useState(0);
    const [authStep, setAuthStep] = useState('input');
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');
    const [successMessage, setSuccessMessage] = useState('');

    // State for login
    const [contactInfo, setContactInfo] = useState('');

    // State for signup
    const [signupData, setSignupData] = useState<Partial<SignUpData>>({});

    const resetState = () => {
        setTimeout(() => {
            setAuthStep('input');
            setCurrentPage(0);
            setError('');
            setSuccessMessage('');
            setSignupData({});
            onSignOut();
        }, 2500);
    };

    const handleLoginSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setError('');
        try {
            await requestLoginOtp(contactInfo);
            setAuthStep('otp');
        } catch (err) {
            setError(err.response?.data?.message || 'An unexpected error occurred.');
        } finally {
            setIsLoading(false);
        }
    };

    const handleOtpSubmit = async (otp) => {
        setIsLoading(true);
        setError('');
        try {
            const response = await verifyLoginOtp(contactInfo, otp);
            console.log('Login successful!', response.data);
            setSuccessMessage("Login Successful");
            setAuthStep('success');
            resetState();
        } catch (err) {
            setError(err.response?.data?.message || 'Invalid OTP or an error occurred.');
        } finally {
            setIsLoading(false);
        }
    };

    const handleSignupSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setError('');
        try {
            const response = await signUpUser(signupData as SignUpData);
            console.log('Signup successful!', response.data);
            setSuccessMessage("Account Created");
            setAuthStep('success');
            resetState();
        } catch (err) {
             setError(err.response?.data?.message || 'An error occurred during signup.');
        } finally {
            setIsLoading(false);
        }
    };

    const handlePageChange = (page) => {
        setError(''); // Clear errors when changing pages
        setCurrentPage(page);
    };
    
    const updateSignupData = (newData: Partial<SignUpData>) => {
        setSignupData(prev => ({ ...prev, ...newData }));
    };

    return (
        <div className="w-full h-full bg-passport-page dark:bg-passport-page-dark rounded-r-2xl relative flex z-10">
            {/* Left Page (Static Info) */}
            <div className="w-1/2 h-full p-6 border-r-2 border-dashed border-stone-300 dark:border-stone-700 flex flex-col justify-center">
                <AnimatePresence mode="wait">
                    <motion.div key={currentPage} initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}>
                        <PageInfo
                            isOpen={isOpen}
                            title={currentPage === 0 ? "Identity Verification" : (currentPage === 1 ? "Passenger Details" : "Account Security")}
                            text={currentPage === 0 ? "Welcome back. Please enter your credentials to access your account." : (currentPage === 1 ? "Let's get you set up. Your personal details are required to create your passport." : "Your security is our priority. Choose a secure email and password.")}
                        />
                    </motion.div>
                </AnimatePresence>
            </div>

            {/* Right Pages (Dynamic Forms) */}
            <div className="w-1/2 h-full relative" style={{ perspective: '1500px' }}>
                <AnimatePresence>
                    {authStep === 'success' && <SuccessPage message={successMessage} />}
                </AnimatePresence>

                <Page isVisible={currentPage === 0 && authStep !== 'success'}>
                    <AnimatePresence mode="wait">
                        {authStep === 'input' && (
                            <motion.div key="login-input" initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} className="h-full">
                                <LoginForm onSubmit={handleLoginSubmit} isLoading={isLoading} setContactInfo={setContactInfo} onSignupClick={() => handlePageChange(1)} error={error} />
                            </motion.div>
                        )}
                        {authStep === 'otp' && (
                             <motion.div key="login-otp" initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} className="h-full">
                                <OtpForm onSubmit={handleOtpSubmit} isLoading={isLoading} error={error} />
                            </motion.div>
                        )}
                    </AnimatePresence>
                </Page>
                <Page isVisible={currentPage === 1 && authStep !== 'success'} isFlipping={currentPage > 1}>
                    <SignupStep1Form onNext={() => handlePageChange(2)} onBack={() => handlePageChange(0)} updateData={updateSignupData} />
                </Page>
                <Page isVisible={currentPage === 2 && authStep !== 'success'}>
                    <SignupStep2Form onSubmit={handleSignupSubmit} isLoading={isLoading} onBack={() => handlePageChange(1)} updateData={updateSignupData} error={error}/>
                </Page>
            </div>
        </div>
    );
}


// --- Form Components ---

const LoginForm = ({ onSubmit, isLoading, setContactInfo, onSignupClick, error }) => (
    <div className="h-full flex flex-col justify-between"> <div/>
        <form onSubmit={onSubmit} className="space-y-5">
            <h3 className="form-header">Login</h3>
            <InputField icon={<Mail />} type="text" placeholder="E-mail or Phone" required onChange={(e) => setContactInfo(e.target.value)} />
            <ShinyButton type="submit" disabled={isLoading}>{isLoading ? <Spinner /> : 'Request Access Stamp'}</ShinyButton>
            {error && <ErrorMessage message={error} />}
        </form>
        <Footer text="Don't have an account?" actionText="Sign Up" onClick={onSignupClick} />
    </div>
);

const OtpForm = ({ onSubmit, isLoading, error }) => {
    const inputsRef = useRef([]);
    const [otp, setOtp] = useState(new Array(6).fill(""));

    const handleInputChange = (e, index) => {
        const newOtp = [...otp];
        newOtp[index] = e.target.value;
        setOtp(newOtp);
        if (e.target.value && index < 5) {
            inputsRef.current[index + 1].focus();
        }
    };

    const handleKeyDown = (e, index) => {
        if (e.key === 'Backspace' && !otp[index] && index > 0) {
            inputsRef.current[index - 1].focus();
        }
    };
    
    const handleSubmit = (e) => {
        e.preventDefault();
        onSubmit(otp.join(""));
    };

    return (
        <form onSubmit={handleSubmit} className="h-full w-full flex flex-col items-center justify-center">
            <motion.div
                className="otp-container w-full"
                initial={{ scale: 0.5, opacity: 0 }}
                animate={{ scale: 1, opacity: 1 }}
            >
                <h3 className="form-header tracking-widest">ENTRY STAMP</h3>
                <p className="text-xs text-stone-500 dark:text-stone-400 mb-2">A 6-digit code has been sent to your device.</p>
                <p className="text-xs text-stone-500 dark:text-stone-400 mb-4">Enter it below.</p>
                <div className="flex justify-center space-x-1">
                    {otp.map((data, i) => (
                        <input key={i} ref={el => inputsRef.current[i] = el} type="text" maxLength="1" value={data} onChange={e => handleInputChange(e, i)} onKeyDown={e => handleKeyDown(e, i)} className="otp-input" required />
                    ))}
                </div>
            </motion.div>
            <div className="mt-5 w-full">
                <ShinyButton type="submit" disabled={isLoading}>{isLoading ? <Spinner /> : 'Verify Code'}</ShinyButton>
            </div>
            {error && <ErrorMessage message={error} />}
        </form>
    );
};

const SignupStep1Form = ({ onNext, onBack, updateData }) => (
    <div className="h-full flex flex-col justify-between"> <div/>
        <div className="space-y-4">
            <h3 className="form-header">Passenger Details</h3>
            <InputField icon={<User />} type="text" placeholder="FIRST NAME" required onChange={e => updateData({ firstName: e.target.value })} />
            <InputField icon={<Users />} type="text" placeholder="LAST NAME" required onChange={e => updateData({ lastName: e.target.value })} />
            <InputField icon={<MapPin />} type="text" placeholder="CITY" required onChange={e => updateData({ city: e.target.value })}/>
        </div>
        <div className="flex items-center space-x-4">
            <button type="button" onClick={onBack} className="form-back-button"><ChevronLeft size={14} /><span>Login</span></button>
            <ShinyButton onClick={onNext}>Next <ChevronRight size={16} /></ShinyButton>
        </div>
    </div>
);

const SignupStep2Form = ({ onSubmit, isLoading, onBack, updateData, error }) => {
    const [password, setPassword] = useState('');

    const getPasswordStrength = () => {
        let score = 0;
        if (!password) return 0;
        if (password.length >= 8) score++;
        if (/[A-Z]/.test(password)) score++;
        if (/[0-9]/.test(password)) score++;
        if (/[^A-Za-z0-9]/.test(password)) score++;
        return score;
    };

    return (
        <div className="h-full flex flex-col justify-between"> <div/>
            <form onSubmit={onSubmit} className="space-y-4">
                <h3 className="form-header">Account Security</h3>
                <InputField icon={<Mail />} type="email" placeholder="CONTACT E-MAIL" onChange={e => updateData({ email: e.target.value })} />
                <div>
                    <InputField
                        icon={<KeyRound />}
                        type="password"
                        placeholder="PASSWORD"
                        required
                        onChange={(e) => {
                            setPassword(e.target.value);
                            updateData({ password: e.target.value });
                        }}
                    />
                    <PasswordStrengthMeter strength={getPasswordStrength()} />
                </div>
                <InputField icon={<Phone />} type="tel" placeholder="PHONE (OPTIONAL)" onChange={e => updateData({ phoneNumber: e.target.value })} />
                <div className="pt-2 flex items-center space-x-4">
                    <button type="button" onClick={onBack} className="form-back-button"><ChevronLeft size={14} /><span>Back</span></button>
                    <ShinyButton type="submit" disabled={isLoading}>{isLoading ? <Spinner /> : 'Create Account'}</ShinyButton>
                </div>
                {error && <ErrorMessage message={error} />}
            </form>
            <div/>
        </div>
    );
};

const SuccessPage = ({ message }) => (
    <motion.div className="absolute inset-0 bg-passport-page dark:bg-passport-page-dark z-20 flex flex-col items-center justify-center text-center p-8" initial={{ opacity: 0, scale: 0.8 }} animate={{ opacity: 1, scale: 1 }} exit={{ opacity: 0 }}>
        <motion.div initial={{ scale: 0 }} animate={{ scale: 1 }} transition={{ type: 'spring', delay: 0.2 }}>
            <CheckCircle className="text-green-500" size={80} />
        </motion.div>
        <h2 className="mt-6 text-2xl font-bold text-stone-800 dark:text-stone-100">{message}</h2>
        <p className="text-stone-500 dark:text-stone-400">You're ready for your next journey!</p>
    </motion.div>
);

const ErrorMessage = ({ message }) => (
    <div className="flex items-center justify-center mt-2 text-xs text-red-500">
        <AlertCircle size={14} className="mr-1" />
        <span>{message}</span>
    </div>
);