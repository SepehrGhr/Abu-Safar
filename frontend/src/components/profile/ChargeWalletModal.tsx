import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { X, CreditCard } from 'lucide-react';
import { ShinyButton, Spinner } from '../auth/common';
import { chargeWallet } from '../../services/api/apiService';
import { useAuth } from '../../context/AuthContext';

interface ChargeWalletModalProps {
    isOpen: boolean;
    onClose: () => void;
    currentBalance: number;
}

const ChargeWalletModal: React.FC<ChargeWalletModalProps> = ({ isOpen, onClose, currentBalance }) => {
    const [amount, setAmount] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const { updateUser } = useAuth();

    const handleCharge = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsLoading(true);
        setError('');
        setSuccess('');

        try {
            const chargeAmount = parseFloat(amount);
            if (chargeAmount > 0) {
                const response = await chargeWallet(chargeAmount);
                // Update the global user state with the new data from the API
                updateUser(response.data); 
                setSuccess(`Successfully added $${chargeAmount.toFixed(2)} to your wallet.`);
                
                setTimeout(() => {
                    onClose();
                    // Reset modal state for next time
                    setSuccess('');
                    setAmount('');
                }, 2000);
            } else {
                setError('Please enter a valid amount.');
            }
        } catch (err) {
            setError(err.response?.data?.message || 'An error occurred.');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <AnimatePresence>
            {isOpen && (
                <motion.div
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    exit={{ opacity: 0 }}
                    className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-50 p-4"
                    onClick={onClose}
                >
                    <motion.div
                        initial={{ scale: 0.9, opacity: 0 }}
                        animate={{ scale: 1, opacity: 1 }}
                        exit={{ scale: 0.9, opacity: 0 }}
                        transition={{ type: 'spring', damping: 20, stiffness: 300 }}
                        className="bg-white dark:bg-slate-900 rounded-2xl shadow-xl w-full max-w-md p-8 relative"
                        onClick={(e) => e.stopPropagation()} // Prevent closing when clicking inside
                    >
                        <button onClick={onClose} className="absolute top-4 right-4 text-stone-500 hover:text-stone-800 dark:hover:text-stone-200">
                            <X size={24} />
                        </button>

                        <div className="flex items-center text-passport-gold mb-4">
                            <CreditCard size={28} className="mr-3" />
                            <h2 className="text-2xl font-bold text-stone-800 dark:text-white">Add Funds to Wallet</h2>
                        </div>
                        
                        <p className="text-sm text-stone-500 dark:text-stone-400 mb-6">Your current balance is ${currentBalance.toFixed(2)}.</p>

                        <form onSubmit={handleCharge}>
                            <label htmlFor="amount" className="block text-sm font-medium text-stone-600 dark:text-stone-300 mb-2">Amount to Add ($)</label>
                            <input
                                id="amount"
                                type="number"
                                value={amount}
                                onChange={(e) => setAmount(e.target.value)}
                                placeholder="e.g., 50.00"
                                className="input-field py-2 text-lg"
                                step="0.01"
                                min="1"
                                required
                            />

                            <div className="mt-6">
                                <ShinyButton className="w-full" disabled={isLoading}>
                                    {isLoading ? <Spinner /> : 'Confirm Payment'}
                                </ShinyButton>
                            </div>
                            
                            {error && <p className="text-xs text-red-500 text-center mt-2">{error}</p>}
                            {success && <p className="text-xs text-green-500 text-center mt-2">{success}</p>}
                        </form>
                    </motion.div>
                </motion.div>
            )}
        </AnimatePresence>
    );
};

export default ChargeWalletModal;