import React from 'react';
import { motion } from 'framer-motion';
import { Plane, Bus, Train, Calendar, AlertTriangle, CheckCircle, XCircle, Clock } from 'lucide-react';
import ActionButton from '../common/ActionButton';

const statusInfo = {
    UPCOMING_TRIP: { icon: <Calendar size={16} />, color: 'text-blue-500', label: 'Upcoming' },
    PAST_TRIP: { icon: <CheckCircle size={16} />, color: 'text-green-500', label: 'Completed' },
    CANCELLED: { icon: <XCircle size={16} />, color: 'text-red-500', label: 'Cancelled' },
    PENDING_PAYMENT: { icon: <Clock size={16} />, color: 'text-orange-500', label: 'Pending Payment' }
};

const vehicleIcons = {
    FLIGHT: <Plane size={20} />,
    BUS: <Bus size={20} />,
    TRAIN: <Train size={20} />
};

export default function ReservationCard({ record, onCancelClick }) {
    const { status, reservationId, ticketsInformation } = record;
    const currentStatus = statusInfo[status];
    const primaryTicket = ticketsInformation[0];

    return (
        <motion.div
            layout
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
            className="border border-stone-200 dark:border-slate-800 rounded-xl overflow-hidden"
        >
            <div className="p-4 flex flex-col sm:flex-row justify-between items-start">
                <div className="flex-grow">
                    <div className="flex items-center space-x-3 mb-2">
                        <span className={`p-1.5 rounded-full ${currentStatus.color.replace('text-', 'bg-')}/10`}>
                            {React.cloneElement(currentStatus.icon, { className: currentStatus.color })}
                        </span>
                        <h4 className="font-bold text-stone-800 dark:text-white">{primaryTicket.originCity} to {primaryTicket.destinationCity}</h4>
                        <span className={`text-xs font-semibold px-2 py-0.5 rounded-full ${currentStatus.color.replace('text-', 'bg-')}/10 ${currentStatus.color}`}>
                            {currentStatus.label}
                        </span>
                    </div>
                    <div className="text-sm text-stone-500 dark:text-stone-400 pl-9">
                        <p>{new Date(primaryTicket.departureTimestamp).toLocaleString('en-US', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}</p>
                        <p>{primaryTicket.vehicleCompany} &bull; {vehicleIcons[primaryTicket.tripVehicle]}</p>
                    </div>
                </div>
                <div className="mt-4 sm:mt-0 sm:ml-4 flex-shrink-0">
                    {status === 'UPCOMING_TRIP' && (
                        <ActionButton onClick={() => onCancelClick(reservationId)} className="w-full sm:w-auto text-sm bg-red-500/10 text-red-500 hover:bg-red-500/20">
                            <XCircle size={16} />
                            <span>Cancel Trip</span>
                        </ActionButton>
                    )}
                    {status === 'PENDING_PAYMENT' && (
                        <ActionButton onClick={() => alert('Redirect to payment page...')} className="w-full sm:w-auto text-sm">
                            Pay Now
                        </ActionButton>
                    )}
                </div>
            </div>
        </motion.div>
    );
}