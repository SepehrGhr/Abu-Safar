import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { X, AlertTriangle } from 'lucide-react';
import { ShinyButton, Spinner } from '../auth/common';
import { calculateCancellationPenalty, cancelReservation } from '../../services/api/apiService';
import { useAuth } from '../../context/AuthContext';

interface CancellationModalProps {
    isOpen: boolean;
    onClose: () => void;
    reservationId: number;
    onCancellationSuccess: () => void;
}

const CancellationModal: React.FC<CancellationModalProps> = ({ isOpen, onClose, reservationId, onCancellationSuccess }) => {
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState('');
    const [penaltyDetails, setPenaltyDetails] = useState(null);
    const { updateUser } = useAuth();

    useEffect(() => {
        if (isOpen) {
            const fetchPenalty = async () => {
                setIsLoading(true);
                setError('');
                try {
                    const response = await calculateCancellationPenalty(reservationId);
                    setPenaltyDetails(response.data);
                } catch (err) {
                    setError(err.response?.data?.message || 'Could not calculate penalty.');
                } finally {
                    setIsLoading(false);
                }
            };
            fetchPenalty();
        }
    }, [isOpen, reservationId]);

    const handleConfirmCancellation = async () => {
        setIsLoading(true);
        setError('');
        try {
            const response = await cancelReservation(reservationId);
            updateUser(response.data.newWalletBalance); // Update wallet in context
            onCancellationSuccess();
            onClose();
        } catch (err) {
            setError(err.response?.data?.message || 'Cancellation failed.');
        } finally {
            setIsLoading(false);
        }
    };
    
    return (
        <AnimatePresence>
            {isOpen && (
                <motion.div
                    initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}
                    className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-50 p-4"
                    onClick={onClose}
                >
                    <motion.div
                        initial={{ scale: 0.9, opacity: 0 }} animate={{ scale: 1, opacity: 1 }} exit={{ scale: 0.9, opacity: 0 }}
                        className="bg-white dark:bg-slate-900 rounded-2xl shadow-xl w-full max-w-md p-8 relative"
                        onClick={(e) => e.stopPropagation()}
                    >
                        <button onClick={onClose} className="absolute top-4 right-4 text-stone-500 hover:text-stone-800 dark:hover:text-stone-200">
                            <X size={24} />
                        </button>

                        <div className="flex items-center text-red-500 mb-4">
                            <AlertTriangle size={28} className="mr-3" />
                            <h2 className="text-2xl font-bold text-stone-800 dark:text-white">Confirm Cancellation</h2>
                        </div>
                        
                        {isLoading && <div className="text-center p-8"><Spinner /></div>}
                        {error && <p className="text-red-500 text-center">{error}</p>}
                        
                        {!isLoading && penaltyDetails && (
                            <div>
                                <p className="text-sm text-stone-600 dark:text-stone-300 mb-4">
                                    {penaltyDetails.message} Are you sure you want to cancel this reservation? This action is irreversible.
                                </p>
                                <div className="space-y-2 text-sm bg-stone-100 dark:bg-slate-800 p-4 rounded-lg">
                                    <div className="flex justify-between"><span>Original Price:</span> <span className="font-semibold">${penaltyDetails.originalPrice.toFixed(2)}</span></div>
                                    <div className="flex justify-between text-red-500"><span>Penalty Fee:</span> <span className="font-semibold">-${penaltyDetails.penaltyAmount.toFixed(2)}</span></div>
                                    <hr className="border-stone-200 dark:border-slate-700"/>
                                    <div className="flex justify-between font-bold text-lg text-green-500"><span>Total Refund:</span> <span>${penaltyDetails.refundAmount.toFixed(2)}</span></div>
                                </div>
                                <div className="mt-6 flex space-x-4">
                                    <button onClick={onClose} className="flex-1 py-2 text-sm font-semibold rounded-lg bg-stone-200 dark:bg-slate-700 hover:bg-stone-300 dark:hover:bg-slate-600 transition-colors">
                                        Keep Reservation
                                    </button>
                                    <ShinyButton onClick={handleConfirmCancellation} className="flex-1 bg-red-500/10 text-red-500 hover:bg-red-500/20">
                                        Confirm & Cancel
                                    </ShinyButton>
                                </div>
                            </div>
                        )}
                    </motion.div>
                </motion.div>
            )}
        </AnimatePresence>
    );
};

export default CancellationModal;