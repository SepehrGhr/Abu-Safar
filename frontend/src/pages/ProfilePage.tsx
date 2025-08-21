import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import ProfileIdCard from '../components/profile/ProfileIdCard';
import WalletWidget from '../components/profile/WalletWidget';
import ChargeWalletModal from '../components/profile/ChargeWalletModal';
import ReservationHistory from '../components/profile/ReservationHistory'; 
import background from '../assets/images/night.jpg';

export default function ProfilePage() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const [isModalOpen, setIsModalOpen] = useState(false);

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
                        <button onClick={handleLogout} className="text-sm font-semibold text-stone-500 dark:text-stone-400 hover:underline">Logout</button>
                    </div>
                    <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                        <div className="lg:col-span-2">
                            <ProfileIdCard user={user} />
                            {/* Add the reservation history below the main card */}
                            <ReservationHistory />
                        </div>
                        <div className="lg:col-span-1">
                            <WalletWidget balance={user.walletBalance} onAddFunds={() => setIsModalOpen(true)} />
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