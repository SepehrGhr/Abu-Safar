import React from 'react';
import type { Ticket } from '/src/services/api/types';

interface ReservationTicketCardProps {
  ticket: Ticket;
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

  const stopsText = ticket.stops === 0 ? 'Non-stop' : `${ticket.stops} stop${ticket.stops > 1 ? 's' : ''}`;

  return (
    <div className="bg-white/80 dark:bg-slate-950/50 backdrop-blur-md rounded-xl shadow-lg overflow-hidden">
      <div className="bg-slate-100/50 dark:bg-slate-900/50 px-6 py-2 flex justify-between items-center text-sm font-semibold text-slate-700 dark:text-slate-300">
        <span>{index === 0 ? 'Departure' : 'Return'}</span>
        <span>{formatDate(ticket.departureTimestamp)}</span>
      </div>
      <div className="p-6 md:flex justify-between items-center">
        <div className="flex-grow">
          <div className="flex justify-between items-start">
            <div>
              <p className="font-bold text-lg text-slate-900 dark:text-white">{ticket.company}</p>
              <p className="text-sm text-gray-600 dark:text-gray-400">{ticket.class} Class</p>
            </div>
            <div className="text-3xl">{ticket.logo || '✈️'}</div>
          </div>
          <div className="flex items-center justify-between mt-4">
            <div className="text-center">
              <p className="text-2xl font-bold text-slate-900 dark:text-white">{ticket.from}</p>
              <p className="text-sm text-gray-600 dark:text-gray-400">{ticket.fromAirport}</p>
              <p className="text-lg font-semibold text-slate-800 dark:text-slate-200 mt-1">{formatTime(ticket.departureTimestamp)}</p>
            </div>
            <div className="text-center text-sm text-gray-500 dark:text-gray-400 px-2">
              <p>{calculateDuration(ticket.departureTimestamp, ticket.arrivalTimestamp)}</p>
              <div className="w-16 md:w-24 h-px bg-gray-300 dark:bg-slate-600 my-1 mx-auto"></div>
              <p>{stopsText}</p>
            </div>
            <div className="text-center">
              <p className="text-2xl font-bold text-slate-900 dark:text-white">{ticket.to}</p>
              <p className="text-sm text-gray-600 dark:text-gray-400">{ticket.toAirport}</p>
              <p className="text-lg font-semibold text-slate-800 dark:text-slate-200 mt-1">{formatTime(ticket.arrivalTimestamp)}</p>
            </div>
          </div>
        </div>
        <div className="w-full md:w-px h-px md:h-24 bg-slate-200 dark:bg-slate-700 my-4 md:my-0 md:mx-6"></div>
        <div className="text-center flex-shrink-0 md:w-24">
          <p className="text-sm text-gray-600 dark:text-gray-400">Price</p>
          <p className="text-2xl font-bold text-[#a57c44] dark:text-[#ebab5e]">${ticket.price.toFixed(2)}</p>
        </div>
      </div>
      {ticket.services && (
        <div className="bg-slate-100/50 dark:bg-slate-900/50 px-6 py-3">
          <p className="text-sm font-semibold text-slate-700 dark:text-slate-300">Services: <span className="font-normal text-gray-600 dark:text-gray-400">{ticket.services.join(', ')}</span></p>
        </div>
      )}
    </div>
  );
};