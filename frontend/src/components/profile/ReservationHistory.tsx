import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { getReservationHistory } from '../../services/api/apiService';
import type { ReserveRecordItemDTO } from '../../services/api/apiService';
import ReservationCard from './ReservationCard';
import CancellationModal from './CancellationModal';

const statusTabs = ['UPCOMING_TRIP', 'PAST_TRIP', 'CANCELLED', 'PENDING_PAYMENT'];

export default function ReservationHistory() {
    const [reservations, setReservations] = useState<ReserveRecordItemDTO[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [activeTab, setActiveTab] = useState('UPCOMING_TRIP');
    
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [selectedReservationId, setSelectedReservationId] = useState<number | null>(null);

    const fetchHistory = async (status: string) => {
        setIsLoading(true);
        setError(null);
        try {
            const response = await getReservationHistory(status);
            setReservations(response.data);
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to load reservation history.');
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        fetchHistory(activeTab);
    }, [activeTab]);

    const handleOpenCancelModal = (reservationId: number) => {
        setSelectedReservationId(reservationId);
        setIsModalOpen(true);
    };

    const handleCloseModal = () => {
        setIsModalOpen(false);
        setSelectedReservationId(null);
    };
    
    const handleCancellationSuccess = () => {
        fetchHistory(activeTab); 
    };

    const renderContent = () => {
        if (isLoading) return <div className="text-center p-8 text-stone-500 dark:text-stone-400">Loading reservations...</div>;
        if (error) return <div className="text-center p-8 text-red-500">{error}</div>;
        if (reservations.length === 0) return <div className="text-center p-8 text-stone-500 dark:text-stone-400">No reservations found for this category.</div>;
        
        return (
            <motion.div layout className="space-y-4">
                <AnimatePresence>
                    {reservations.map(record => (
                        <ReservationCard key={record.reservationId} record={record} onCancelClick={handleOpenCancelModal} />
                    ))}
                </AnimatePresence>
            </motion.div>
        );
    };

    return (
        <>
            <div className="flex p-2 bg-stone-100 dark:bg-slate-900/50 space-x-2">
                {statusTabs.map(tab => (
                    <button
                        key={tab}
                        onClick={() => setActiveTab(tab)}
                        className={`relative flex-1 px-3 py-2 text-sm font-semibold rounded-lg transition-colors ${
                            activeTab === tab ? 'text-stone-900' : 'text-stone-500 hover:text-stone-800 dark:text-stone-400 dark:hover:text-white'
                        }`}
                    >
                        {activeTab === tab && <motion.div layoutId="historyTab" className="absolute inset-0 bg-white dark:bg-slate-700 rounded-lg shadow-sm" />}
                        <span className="relative z-10 capitalize">{tab.replace('_', ' ').toLowerCase()}</span>
                    </button>
                ))}
            </div>
            <div className="p-4">
                {renderContent()}
            </div>

            {selectedReservationId && (
                <CancellationModal
                    isOpen={isModalOpen}
                    onClose={handleCloseModal}
                    reservationId={selectedReservationId}
                    onCancellationSuccess={handleCancellationSuccess}
                />
            )}
        </>
    );
}