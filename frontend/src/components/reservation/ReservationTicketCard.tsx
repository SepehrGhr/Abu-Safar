import React from 'react';
import type { ReservationTicket } from '/src/services/api/types';
import { Bus, Train, Plane } from 'lucide-react';
import { ReservationDetailsView } from './ReservationDetailsView';

interface ReservationTicketCardProps {
  ticket: ReservationTicket;
  index: number;
}

export const ReservationTicketCard: React.FC<ReservationTicketCardProps> = ({ ticket, index }) => {
  const formatTime = (timestamp: string) => new Date(timestamp).toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit' });
  const formatDate = (timestamp: string) => new Date(timestamp).toLocaleDateString('en-US', { weekday: 'short', month: 'short', day: 'numeric' });

  const calculateDuration = (start: string, end: string) => {
    const diffMs = new Date(end).getTime() - new Date(start).getTime();
    const diffHours = Math.floor(diffMs / 3600000);
    const diffMinutes = Math.round((diffMs % 3600000) / 60000);
    return `${diffHours}h ${diffMinutes}m`;
  };

  const getVehicleIcon = () => {
    switch(ticket.tripVehicle) {
      case 'BUS': return <Bus className="w-6 h-6 text-slate-500" />;
      case 'TRAIN': return <Train className="w-6 h-6 text-slate-500" />;
      case 'FLIGHT': return <Plane className="w-6 h-6 text-slate-500" />;
      default: return null;
    }
  };

  const hasDetails = ticket.vehicleDetails || (ticket.service && ticket.service.length > 0);

  return (
    <div className="bg-white/80 dark:bg-slate-950/50 backdrop-blur-md rounded-xl shadow-lg overflow-hidden border border-slate-200/50 dark:border-slate-800/50">
      <div className="bg-slate-100/50 dark:bg-slate-900/50 px-6 py-2 flex justify-between items-center text-sm font-semibold text-slate-700 dark:text-slate-300">
        <span>{index === 0 ? 'Departure' : 'Return'}</span>
        <span>{formatDate(ticket.departureTimestamp)}</span>
      </div>
      <div className="p-6">
          <div className="flex justify-between items-start mb-4">
            <div className='flex items-center gap-3'>
                {getVehicleIcon()}
                <div>
                  <p className="font-bold text-lg text-slate-900 dark:text-white">{ticket.companyName}</p>
                </div>
            </div>
            <div className="text-center flex-shrink-0">
              <p className="text-sm text-gray-600 dark:text-gray-400">Price</p>
              <p className="text-2xl font-bold text-[#a57c44] dark:text-[#ebab5e]">${ticket.price.toFixed(2)}</p>
            </div>
          </div>

          <div className="flex items-center justify-between mt-4">
            <div className="text-center">
              <p className="text-2xl font-bold text-slate-900 dark:text-white">{ticket.originCity}</p>
              <p className="text-lg font-semibold text-slate-800 dark:text-slate-200 mt-1">{formatTime(ticket.departureTimestamp)}</p>
            </div>
            <div className="text-center text-sm text-gray-500 dark:text-gray-400 px-2">
              <p>{calculateDuration(ticket.departureTimestamp, ticket.arrivalTimestamp)}</p>
              <div className="w-16 md:w-24 h-px bg-gray-300 dark:bg-slate-600 my-1 mx-auto"></div>
              <p>{ticket.stopCount} stop(s)</p>
            </div>
            <div className="text-center">
              <p className="text-2xl font-bold text-slate-900 dark:text-white">{ticket.destinationCity}</p>
              <p className="text-lg font-semibold text-slate-800 dark:text-slate-200 mt-1">{formatTime(ticket.arrivalTimestamp)}</p>
            </div>
          </div>
      </div>

      {hasDetails && <ReservationDetailsView ticket={ticket} />}
    </div>
  );
};