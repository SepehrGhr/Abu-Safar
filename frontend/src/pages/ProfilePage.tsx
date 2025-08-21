import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { LogOut, History, CreditCard } from 'lucide-react';
import ProfileIdCard from '../components/profile/ProfileIdCard';
import WalletWidget from '../components/profile/WalletWidget';
import ChargeWalletModal from '../components/profile/ChargeWalletModal';
import ReservationHistory from '../components/profile/ReservationHistory';
import PaymentHistory from '../components/profile/PaymentHistory';
import background from '../assets/images/night.jpg';

type ProfileView = 'RESERVATIONS' | 'PAYMENTS';

export default function ProfilePage() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [activeView, setActiveView] = useState<ProfileView>('RESERVATIONS');

    const handleLogout = () => {
        logout();
        navigate('/');
    };

    if (!user) {
        return <div>Loading profile...</div>;
    }

    return (
        <>
            <motion.div 
                className="min-h-screen pt-24 pb-16 relative font-kameron"
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                exit={{ opacity: 0 }}
            >
                <div className="absolute inset-0 bg-cover bg-center opacity-20 dark:opacity-30 -z-10" style={{ backgroundImage: `url(${background})` }}></div>
                <div className="absolute inset-0 bg-brand-sandLight dark:bg-brand-night -z-20"></div>

                <div className="container mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex justify-end mb-4">
                        <motion.button 
                            onClick={handleLogout} 
                            className="flex items-center space-x-2 text-sm font-semibold px-4 py-2 rounded-lg bg-white/60 dark:bg-slate-800/50 text-stone-600 dark:text-stone-300 hover:bg-red-500/10 hover:text-red-500 dark:hover:bg-red-500/10 dark:hover:text-red-500 transition-all duration-300"
                            whileHover={{ scale: 1.05 }}
                            whileTap={{ scale: 0.95 }}
                        >
                            <LogOut size={16} />
                            <span>Logout</span>
                        </motion.button>
                    </div>

                    {/* Top Section */}
                    <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                        <div className="lg:col-span-2">
                            <ProfileIdCard user={user} />
                        </div>
                        <div className="lg:col-span-1">
                            <WalletWidget balance={user.walletBalance} onAddFunds={() => setIsModalOpen(true)} />
                        </div>
                    </div>

                    {/* Bottom Section with Tabs */}
                    <div className="bg-white/80 dark:bg-slate-900/50 backdrop-blur-lg rounded-2xl shadow-xl mt-8">
                        <div className="flex p-2 bg-stone-100 dark:bg-slate-900/50 space-x-2 border-b border-stone-200 dark:border-slate-800">
                            <TabButton icon={<History size={16}/>} label="Reservations" isActive={activeView === 'RESERVATIONS'} onClick={() => setActiveView('RESERVATIONS')} />
                            <TabButton icon={<CreditCard size={16}/>} label="Payments" isActive={activeView === 'PAYMENTS'} onClick={() => setActiveView('PAYMENTS')} />
                        </div>
                        <div>
                            {activeView === 'RESERVATIONS' && <ReservationHistory />}
                            {activeView === 'PAYMENTS' && <PaymentHistory />}
                        </div>
                    </div>
                </div>
            </motion.div>
            <ChargeWalletModal 
                isOpen={isModalOpen} 
                onClose={() => setIsModalOpen(false)} 
                currentBalance={user.walletBalance}
            />
        </>
    );
}

const TabButton = ({ icon, label, isActive, onClick }) => (
    <button
        onClick={onClick}
        className={`relative flex-1 px-3 py-2 text-sm font-semibold rounded-lg transition-colors flex items-center justify-center space-x-2 ${
            isActive ? 'text-stone-900 dark:text-white' : 'text-stone-500 hover:text-stone-800 dark:text-stone-400 dark:hover:text-white'
        }`}
    >
        {isActive && <motion.div layoutId="profileTab" className="absolute inset-0 bg-white dark:bg-slate-700 rounded-lg shadow-sm" />}
        <span className="relative z-10">{icon}</span>
        <span className="relative z-10">{label}</span>
    </button>
);