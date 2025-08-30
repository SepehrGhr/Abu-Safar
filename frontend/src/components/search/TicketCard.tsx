import { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Plane, Train, Bus, ChevronDown } from 'lucide-react';
import ActionButton from '../common/ActionButton';
import type { Ticket, TicketDetails } from '/src/services/api/types';
import { selectTicket } from '/src/services/api/tickets';
import TicketDetailsView from './TicketDetailsView';

const formatTime = (timestamp: string): string => {
    const date = new Date(timestamp);
    return date.toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit' });
};

const calculateDuration = (start: string, end: string): string => {
    const startDate = new Date(start);
    const endDate = new Date(end);
    const diffMs = endDate.getTime() - startDate.getTime();
    const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
    const diffMinutes = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60));
    return `${diffHours}h ${diffMinutes}m`;
};

const getVehicleIcon = (vehicleType: string) => {
    const type = vehicleType?.toUpperCase();
    switch (type) {
        case 'FLIGHT':
            return <Plane className="w-8 h-8 text-slate-700 dark:text-slate-300" />;
        case 'TRAIN':
            return <Train className="w-8 h-8 text-slate-700 dark:text-slate-300" />;
        case 'BUS':
            return <Bus className="w-8 h-8 text-slate-700 dark:text-slate-300" />;
        default:
            return null;
    }
};

interface TicketCardProps {
  ticket: Ticket;
  // --- NEW: onSelect prop to trigger the animation in the parent ---
  onSelect: () => void;
}

const TicketCard: React.FC<TicketCardProps> = ({ ticket, onSelect }) => {
    const [isExpanded, setIsExpanded] = useState(false);
    const [details, setDetails] = useState<TicketDetails | null>(null);
    const [isLoadingDetails, setIsLoadingDetails] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const handleViewDetails = async () => {
        if (isExpanded) {
            setIsExpanded(false);
            return;
        }

        setIsExpanded(true);

        if (!details) {
            setIsLoadingDetails(true);
            setError(null);
            try {
                const result = await selectTicket({
                    tripId: ticket.tripId,
                    ageCategory: ticket.age,
                });
                setDetails(result);
            } catch (err: any) {
                setError(err.message || 'Failed to load details.');
            } finally {
                setIsLoadingDetails(false);
            }
        }
    };

    return (
        <motion.div
            // --- NEW: This layoutId is crucial for the animation ---
            layoutId={`ticket-${ticket.tripId}`}
            initial={{ opacity: 0, y: 50 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20, transition: { duration: 0.3 } }}
            className="bg-white/80 dark:bg-slate-950/50 backdrop-blur-md rounded-xl shadow-lg overflow-hidden transition-shadow hover:shadow-2xl"
        >
            <div className="p-6 flex flex-col sm:flex-row items-center justify-between space-y-4 sm:space-y-0">
                <div className="flex items-center space-x-4 min-w-[200px]">
                    <div>{getVehicleIcon(ticket.tripVehicle)}</div>
                    <div>
                        <p className="font-bold text-slate-900 dark:text-white">{ticket.vehicleCompany}</p>
                        <p className="text-sm text-gray-600 dark:text-gray-400 capitalize">{ticket.tripVehicle.toLowerCase()}</p>
                    </div>
                </div>
                <div className="flex items-center space-x-8 text-center">
                    <div>
                        <p className="text-xl font-bold text-slate-900 dark:text-white">{formatTime(ticket.departureTimestamp)}</p>
                        <p className="text-sm font-semibold text-slate-700 dark:text-slate-300">{ticket.originCity}</p>
                    </div>
                    <div className="text-sm text-gray-500 dark:text-gray-400">
                        <p>{calculateDuration(ticket.departureTimestamp, ticket.arrivalTimestamp)}</p>
                        <div className="w-24 h-px bg-gray-300 dark:bg-slate-600 my-1"></div>
                    </div>
                    <div>
                        <p className="text-xl font-bold text-slate-900 dark:text-white">{formatTime(ticket.arrivalTimestamp)}</p>
                        <p className="text-sm font-semibold text-slate-700 dark:text-slate-300">{ticket.destinationCity}</p>
                    </div>
                </div>
                <div className="flex flex-col items-center text-center min-w-[150px]">
                    <p className="text-2xl font-bold text-[#a57c44] dark:text-[#ebab5e]">${ticket.price.toFixed(2)}</p>
                    {/* --- MODIFIED: onClick now calls the onSelect prop from the parent --- */}
                    <ActionButton onClick={onSelect} className="w-auto h-auto px-6 py-2 mt-2 rounded-lg font-aladin">
                        Select
                    </ActionButton>
                </div>
            </div>

            <div className="flex justify-center p-1 bg-slate-100/50 dark:bg-slate-900/50">
                <button onClick={handleViewDetails} className="flex items-center text-sm font-semibold text-slate-600 dark:text-slate-300 hover:text-slate-900 dark:hover:text-white transition-all duration-300">
                    <span>{isExpanded ? 'Hide' : 'View'} Details</span>
                    <motion.div animate={{ rotate: isExpanded ? 180 : 0 }} transition={{ duration: 0.3 }}>
                        <ChevronDown size={20} className="ml-1" />
                    </motion.div>
                </button>
            </div>

            <AnimatePresence>
                {isExpanded && (
                    <motion.div
                        initial={{ height: 0, opacity: 0 }}
                        animate={{ height: 'auto', opacity: 1 }}
                        exit={{ height: 0, opacity: 0 }}
                        transition={{ duration: 0.3, ease: 'easeInOut' }}
                        style={{ overflow: 'hidden' }}
                    >
                        {isLoadingDetails && <div className="text-center p-4 text-slate-600 dark:text-slate-300">Loading details...</div>}
                        {error && <div className="text-center p-4 text-red-500">{error}</div>}
                        {details && <TicketDetailsView details={details} />}
                    </motion.div>
                )}
            </AnimatePresence>
        </motion.div>
    );
};

export default TicketCard;
