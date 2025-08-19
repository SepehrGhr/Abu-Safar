import React, { useState, useRef } from 'react';
import { AnimatePresence, motion } from 'framer-motion';
import { Mail, KeyRound, User, Users, MapPin, Phone, CheckCircle, ChevronLeft, ChevronRight } from 'lucide-react';
import Page from './Page';
import { PageInfo } from './PageInfo';
import { InputField, ShinyButton, Footer, Spinner, PasswordStrengthMeter } from './common';

export default function PassportInterior({ isOpen, onSignOut }) {
    const [currentPage, setCurrentPage] = useState(0);
    const [authStep, setAuthStep] = useState('input');
    const [isLoading, setIsLoading] = useState(false);

    const resetState = () => {
        setTimeout(() => {
            setAuthStep('input');
            setCurrentPage(0);
            onSignOut();
        }, 2500);
    };

    const handleLoginSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        await new Promise(resolve => setTimeout(resolve, 1500));
        setIsLoading(false);
        setAuthStep('otp');
    };

    const handleOtpSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        await new Promise(resolve => setTimeout(resolve, 1500));
        setIsLoading(false);
        setAuthStep('success');
        resetState();
    };

    const handleSignupSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        await new Promise(resolve => setTimeout(resolve, 2000));
        setIsLoading(false);
        setAuthStep('success');
        resetState();
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
                    {authStep === 'success' && <SuccessPage message={currentPage === 0 ? "Login Successful" : "Account Created"} />}
                </AnimatePresence>

                <Page isVisible={currentPage === 0 && authStep !== 'success'}>
                    {authStep === 'input' && <LoginForm onSubmit={handleLoginSubmit} isLoading={isLoading} onSignupClick={() => setCurrentPage(1)} />}
                    {authStep === 'otp' && <OtpForm onSubmit={handleOtpSubmit} isLoading={isLoading} />}
                </Page>
                <Page isVisible={currentPage === 1 && authStep !== 'success'} isFlipping={currentPage > 1}>
                    <SignupStep1Form onNext={() => setCurrentPage(2)} onBack={() => setCurrentPage(0)} />
                </Page>
                <Page isVisible={currentPage === 2 && authStep !== 'success'}>
                    <SignupStep2Form onSubmit={handleSignupSubmit} isLoading={isLoading} onBack={() => setCurrentPage(1)} />
                </Page>
            </div>
        </div>
    );
}


// --- Form Components ---

const LoginForm = ({ onSubmit, isLoading, onSignupClick }) => (
    <div className="h-full flex flex-col justify-between"> <div/>
        <form onSubmit={onSubmit} className="space-y-5">
            <h3 className="form-header">Login</h3>
            <InputField icon={<Mail />} type="text" placeholder="E-mail or Phone" required />
            <ShinyButton type="submit" disabled={isLoading}>{isLoading ? <Spinner /> : 'Request Access Stamp'}</ShinyButton>
        </form>
        <Footer text="Don't have an account?" actionText="Sign Up" onClick={onSignupClick} />
    </div>
);

const OtpForm = ({ onSubmit, isLoading }) => {
    const inputsRef = useRef([]);
    const handleInputChange = (e, index) => { if (e.target.value && index < 5) inputsRef.current[index + 1].focus(); };
    const handleKeyDown = (e, index) => { if (e.key === 'Backspace' && !e.target.value && index > 0) inputsRef.current[index - 1].focus(); };

    return (
        <form onSubmit={onSubmit} className="h-full w-full flex flex-col items-center justify-center">
            <motion.div
                className="otp-container w-full"
                initial={{ scale: 0.5, opacity: 0 }}
                animate={{ scale: 1, opacity: 1 }}
            >
                <h3 className="form-header tracking-widest">ENTRY STAMP</h3>
                <p className="text-xs text-stone-500 dark:text-stone-400 mb-2">A 6-digit code has been sent to your device.</p>
                <p className="text-xs text-stone-500 dark:text-stone-400 mb-4">Enter it below.</p>
                <div className="flex justify-center space-x-1">
                    {[...Array(6)].map((_, i) => (
                        <input key={i} ref={el => inputsRef.current[i] = el} type="text" maxLength="1" onChange={e => handleInputChange(e, i)} onKeyDown={e => handleKeyDown(e, i)} className="otp-input" required />
                    ))}
                </div>
            </motion.div>
            <div className="mt-5 w-full">
                <ShinyButton type="submit" disabled={isLoading}>{isLoading ? <Spinner /> : 'Verify Code'}</ShinyButton>
            </div>
        </form>
    );
};

const SignupStep1Form = ({ onNext, onBack }) => (
    <div className="h-full flex flex-col justify-between"> <div/>
        <div className="space-y-4">
            <h3 className="form-header">Passenger Details</h3>
            <InputField icon={<User />} type="text" placeholder="FIRST NAME" required />
            <InputField icon={<Users />} type="text" placeholder="LAST NAME" required />
            <InputField icon={<MapPin />} type="text" placeholder="CITY" required />
        </div>
        <div className="flex items-center space-x-4">
            <button type="button" onClick={onBack} className="form-back-button"><ChevronLeft size={14} /><span>Login</span></button>
            <ShinyButton onClick={onNext}>Next <ChevronRight size={16} /></ShinyButton>
        </div>
    </div>
);

const SignupStep2Form = ({ onSubmit, isLoading, onBack }) => {
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
                <InputField icon={<Mail />} type="email" placeholder="CONTACT E-MAIL" required />
                <div>
                    <InputField
                        icon={<KeyRound />}
                        type="password"
                        placeholder="PASSWORD"
                        required
                        onChange={(e) => setPassword(e.target.value)}
                    />
                    <PasswordStrengthMeter strength={getPasswordStrength()} />
                </div>
                <InputField icon={<Phone />} type="tel" placeholder="PHONE (OPTIONAL)" />
                <div className="pt-2 flex items-center space-x-4">
                    <button type="button" onClick={onBack} className="form-back-button"><ChevronLeft size={14} /><span>Back</span></button>
                    <ShinyButton type="submit" disabled={isLoading}>{isLoading ? <Spinner /> : 'Create Account'}</ShinyButton>
                </div>
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