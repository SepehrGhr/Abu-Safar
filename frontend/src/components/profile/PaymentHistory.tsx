import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { getPaymentHistory } from '../../services/api/apiService';
import type { PaymentRecordDTO } from '../../services/api/apiService';
import PaymentCard from './PaymentCard';

export default function PaymentHistory() {
    const [payments, setPayments] = useState<PaymentRecordDTO[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchPayments = async () => {
            setIsLoading(true);
            setError(null);
            try {
                const response = await getPaymentHistory();
                setPayments(response.data);
            } catch (err) {
                setError(err.response?.data?.message || 'Failed to load payment history.');
            } finally {
                setIsLoading(false);
            }
        };

        fetchPayments();
    }, []);

    const renderContent = () => {
        if (isLoading) return <div className="text-center p-8 text-stone-500 dark:text-stone-400">Loading payments...</div>;
        if (error) return <div className="text-center p-8 text-red-500">{error}</div>;
        if (payments.length === 0) return <div className="text-center p-8 text-stone-500 dark:text-stone-400">No payment records found.</div>;
        
        return (
            <motion.div layout className="space-y-4">
                <AnimatePresence>
                    {payments.map(record => (
                        <PaymentCard key={record.paymentId} record={record} />
                    ))}
                </AnimatePresence>
            </motion.div>
        );
    };

    return (
        <div className="p-4">
            {renderContent()}
        </div>
    );
}