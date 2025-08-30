import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Menu, X, User, LogOut, Wallet } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';
import ShinyButton from '../common/ShinyButton';
import CarpetLogo from '../icons/Carpet.tsx';
import { useAuth } from '../../context/AuthContext';

// A component for the circular profile icon
const ProfileIcon = ({ user }) => {
    const initials = `${user?.firstName?.[0] || ''}${user?.lastName?.[0] || ''}`.toUpperCase();
    return (
        <Link to="/profile">
            <div className="w-9 h-9 rounded-full flex items-center justify-center bg-passport-gold/20 text-passport-gold font-bold border-2 border-passport-gold/50">
                {initials}
            </div>
        </Link>
    );
};

// New component to display the wallet balance
const WalletDisplay = ({ balance }) => (
    <div className="flex items-center space-x-2 text-sm font-semibold px-4 py-2 rounded-lg bg-white/10 dark:bg-slate-800/50 text-stone-600 dark:text-stone-300">
        <Wallet size={16} className="text-passport-gold" />
        <span>${balance.toFixed(2)}</span>
    </div>
);


const Header = () => {
    const [isMenuOpen, setIsMenuOpen] = useState(false);
    const { isAuthenticated, user, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/');
    };

    return (
        <header className="bg-white/80 dark:bg-slate-950/50 backdrop-blur-md shadow-sm fixed top-0 left-0 right-0 z-50">
            <div className="container mx-auto px-4 sm:px-6 lg:px-8">
                <div className="flex items-center justify-between h-16">
                    <a href="/" className="flex items-center space-x-3 text-slate-950 dark:text-white">
                        <CarpetLogo className="h-8 w-8" />
                        <span className="font-aladin font-bold text-2xl text-gray-800 dark:text-white">AbuSafar</span>
                    </a>
                    
                    <div className="hidden md:flex items-center space-x-4">
                        <AnimatePresence mode="wait">
                            {isAuthenticated && user ? (
                                <motion.div key="profile" initial={{ opacity: 0, scale: 0.8 }} animate={{ opacity: 1, scale: 1 }} exit={{ opacity: 0, scale: 0.8 }} className="flex items-center space-x-4">
                                    <WalletDisplay balance={user.walletBalance} />
                                    <ProfileIcon user={user} />
                                    <button onClick={handleLogout} title="Logout" className="p-2 text-stone-500 rounded-full hover:bg-red-500/10 hover:text-red-500 transition-colors">
                                        <LogOut size={18} />
                                    </button>
                                </motion.div>
                            ) : (
                                <motion.div key="auth-button" initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}>
                                    <Link to="/auth">
                                       <ShinyButton><User size={16} /><span className="text-sm font-semibold">Sign In</span></ShinyButton>
                                    </Link>
                                </motion.div>
                            )}
                        </AnimatePresence>
                    </div>
                    <div className="md:hidden">
                        <button onClick={() => setIsMenuOpen(!isMenuOpen)} className="p-2 rounded-md text-gray-600 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-slate-800">{isMenuOpen ? <X size={24} /> : <Menu size={24} />}</button>
                    </div>
                </div>
            </div>
        </header>
    );
};

export default Header;