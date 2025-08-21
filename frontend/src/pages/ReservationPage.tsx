import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { Clock } from 'lucide-react';

import ActionButton from '../components/common/ActionButton';
import { ReservationTicketCard } from '../components/reservation/ReservationTicketCard';
import type { ReserveConfirmation } from '../services/api/types';

export const ReservationPage: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();

  // The state is initialized directly from the location state.
  const [reservationDetails] = useState<ReserveConfirmation | null>(location.state?.reservationDetails);

  useEffect(() => {
    // This effect now correctly checks for the absence of reservationDetails.
    if (!reservationDetails) {
      console.log("No reservation details found, redirecting.");
      navigate('/ticket-search-result');
    }
  }, [reservationDetails, navigate]);

  // If reservationDetails are not available, we show a loading message and the useEffect will handle the redirect.
  if (!reservationDetails) {
    return <div className="pt-32 text-center">Loading Reservation...</div>;
  }

  const { reservationId, tickets, price } = reservationDetails;
  const totalPrice = price;

  return (
    <motion.div
      key="reservation"
      initial={{ opacity: 0, y: 50 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5 }}
      className="pt-24 pb-16"
    >
      <div className="container mx-auto px-4 sm:px-6 lg:px-8">
        <div className="max-w-4xl mx-auto">
          <h2 className="text-4xl font-bold text-center mb-8 font-kameron text-slate-900 dark:text-white">
            Confirm Your Reservation
          </h2>
          <p className="text-center text-gray-500 -mt-4 mb-8">Reservation ID: {reservationId}</p>

          <div className="space-y-6">
            {tickets.map((ticket, index) => (
              <ReservationTicketCard key={ticket.tripId} ticket={ticket} index={index} />
            ))}
          </div>

          <div className="mt-8 bg-white/80 dark:bg-slate-950/50 backdrop-blur-md rounded-xl shadow-lg p-6">
            <h3 className="text-xl font-bold mb-4 text-slate-900 dark:text-white">Finalize your reservation</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="space-y-4">
                  <div>
                      <label className="text-sm font-semibold text-gray-600 dark:text-gray-400">Passenger</label>
                      <p className="text-lg font-semibold text-slate-800 dark:text-slate-200">Aladdin</p>
                  </div>
              </div>
              <div className="space-y-4">
                  <div>
                      <label className="text-sm font-semibold text-gray-600 dark:text-gray-400">Total Price</label>
                      <p className="text-2xl font-bold text-[#a57c44] dark:text-[#ebab5e]">${totalPrice.toFixed(2)}</p>
                  </div>
              </div>
            </div>
          </div>

          <div className="mt-8 text-center p-4 bg-yellow-100/80 dark:bg-yellow-900/50 rounded-lg text-yellow-800 dark:text-yellow-200">
            <p className="font-semibold flex items-center justify-center">
              <Clock size={18} className="mr-2" />
              Your reservation expires soon. Please complete the payment.
            </p>
          </div>

          <div className="mt-8 flex flex-col sm:flex-row justify-center items-center space-y-4 sm:space-y-0 sm:space-x-6">
            <button
              onClick={() => navigate(-1)}
              className="font-semibold text-gray-600 dark:text-gray-300 hover:text-[#ebab5e] dark:hover:text-[#ebab5e] transition-colors"
            >
              Back to Search
            </button>
            <ActionButton className="w-full sm:w-auto text-lg">
                Pay Now
            </ActionButton>
          </div>
        </div>
      </div>
    </motion.div>
  );
};