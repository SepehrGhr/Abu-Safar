import React from 'react';
import { motion } from 'framer-motion';
import { CreditCard, CheckCircle, AlertCircle, Clock } from 'lucide-react';
import type { PaymentRecordDTO } from '../../services/api/apiService';

// Updated to handle all payment statuses
const paymentStatusInfo = {
    SUCCESSFUL: { icon: <CheckCircle size={16} />, color: 'text-green-500', label: 'Successful' },
    UNSUCCESSFUL: { icon: <AlertCircle size={16} />, color: 'text-red-500', label: 'Unsuccessful' },
    PENDING: { icon: <Clock size={16} />, color: 'text-orange-500', label: 'Pending' }
};

export default function PaymentCard({ record }: { record: PaymentRecordDTO }) {
    const { paymentId, reservationId, paymentStatus, paymentType, paymentTimestamp, price } = record;
    // Fallback for any unknown statuses
    const currentStatus = paymentStatusInfo[paymentStatus.toUpperCase()] || { icon: <CreditCard size={16} />, color: 'text-stone-500', label: paymentStatus };

    return (
        <motion.div
            layout
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
            className="border border-stone-200 dark:border-slate-800 rounded-xl p-4 flex justify-between items-center"
        >
            <div className="flex-grow">
                <div className="flex items-center space-x-3">
                    <span className={`p-1.5 rounded-full ${currentStatus.color.replace('text-', 'bg-')}/10`}>
                        {React.cloneElement(currentStatus.icon, { className: currentStatus.color })}
                    </span>
                    <h4 className="font-bold text-stone-800 dark:text-white">Payment #{paymentId}</h4>
                    <span className={`text-xs font-semibold px-2 py-0.5 rounded-full ${currentStatus.color.replace('text-', 'bg-')}/10 ${currentStatus.color}`}>
                        {currentStatus.label}
                    </span>
                </div>
                <div className="text-sm text-stone-500 dark:text-stone-400 pl-9 mt-1">
                    <p>
                        {new Date(paymentTimestamp).toLocaleString('en-US', { year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' })}
                    </p>
                    <p>Reservation ID: {reservationId} &bull; Type: Wallet</p>
                </div>
            </div>
            <div className="flex-shrink-0 ml-4">
                <p className="text-xl font-bold text-stone-800 dark:text-white">${price.toFixed(2)}</p>
            </div>
        </motion.div>
    );
}