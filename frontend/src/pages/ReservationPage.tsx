import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import { Clock, AlertCircle, CheckCircle, Wallet, User,Users } from 'lucide-react';

import ActionButton from '../components/common/ActionButton';
import { ReservationTicketCard } from '../components/reservation/ReservationTicketCard';
import { processPayment } from '../services/api/apiService';
import type { ReserveConfirmation, PaymentRecordDTO } from '../services/api/types';
import { useAuth } from '../context/AuthContext';

export const ReservationPage: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { user } = useAuth();

  const [reservationDetails] = useState<ReserveConfirmation | null>(location.state?.reservationDetails);

  const [isPaying, setIsPaying] = useState(false);
  const [paymentError, setPaymentError] = useState<string | null>(null);
  const [paymentSuccessDetails, setPaymentSuccessDetails] = useState<PaymentRecordDTO | null>(null);

  useEffect(() => {
    if (!reservationDetails) {
      console.log("No reservation details found, redirecting.");
      navigate('/ticket-search-result');
    }
  }, [reservationDetails, navigate]);

  const handlePayment = async () => {
    if (!reservationDetails) return;

    setIsPaying(true);
    setPaymentError(null);

    try {
      const response = await processPayment(reservationDetails.reservationId);
      setPaymentSuccessDetails(response.data);

      setTimeout(() => {
        navigate('/profile', { state: { refresh: true } });
      }, 5000);

    } catch (err: any) {
        const serverMessage = err.response?.data?.message || err.message;

        if (serverMessage.includes("Insufficient wallet balance")) {
            setPaymentError("You do not have enough wallet balance for this transaction.");
        } else {
            setPaymentError("An unexpected error occurred during payment. Please try again.");
        }
    } finally {
      setIsPaying(false);
    }
  };

  if (!reservationDetails) {
    return <div className="pt-32 text-center">Loading Reservation...</div>;
  }

  const { reservationId, tickets, price } = reservationDetails;
  const totalPrice = price;

  const passengerAges = tickets.map(t => t.age).join(', ');

  const passengerName = (user?.firstName && user?.lastName)
    ? `${user.firstName} ${user.lastName}`
    : 'Passenger';

  return (
    <>
      <motion.div
        key="reservation"
        initial={{ opacity: 0, y: 50 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="pt-24 pb-16"
      >
        <div className="container mx-auto px-4 sm:px-6 lg:px-8">
          <div className="max-w-4xl mx-auto">
            <h2 className="text-4xl font-bold text-center mb-8 font-aladin text-slate-900 dark:text-white">
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
                        <label className="text-sm font-semibold text-gray-600 dark:text-gray-400 flex items-center"><User size={14} className="mr-2"/>Passenger</label>
                        <p className="text-lg font-semibold text-slate-800 dark:text-slate-200 capitalize">
                          {passengerName.toLowerCase()}
                        </p>
                    </div>
                     {/* --- NEW: Age Category field --- */}
                    <div>
                        <label className="text-sm font-semibold text-gray-600 dark:text-gray-400 flex items-center"><Users size={14} className="mr-2"/>Age Category</label>
                        <p className="text-lg font-semibold text-slate-800 dark:text-slate-200 capitalize">
                            {passengerAges.toLowerCase()}
                        </p>
                    </div>
                </div>
                <div className="space-y-4 text-left md:text-right">
                     <div>
                        <label className="text-sm font-semibold text-gray-600 dark:text-gray-400 flex items-center justify-start md:justify-end"><Wallet size={14} className="mr-2"/>Wallet Balance</label>
                        <p className="text-lg font-semibold text-slate-800 dark:text-slate-200">
                          ${(user?.walletBalance ?? 0).toFixed(2)}
                        </p>
                    </div>
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

            <AnimatePresence>
              {paymentError && (
                  <motion.div
                      initial={{ opacity: 0, y: 10 }}
                      animate={{ opacity: 1, y: 0 }}
                      exit={{ opacity: 0, y: 10 }}
                      className="mt-4 text-center p-3 bg-red-900/50 text-red-300 rounded-lg flex items-center justify-center"
                  >
                      <AlertCircle className="mr-2" /> {paymentError}
                  </motion.div>
              )}
            </AnimatePresence>

            <div className="mt-8 flex flex-col sm:flex-row justify-center items-center space-y-4 sm:space-y-0 sm:space-x-6">
              <button
                onClick={() => navigate(-1)}
                className="font-semibold text-gray-600 dark:text-gray-300 hover:text-[#ebab5e] dark:hover:text-[#ebab5e] transition-colors"
              >
                Back to Search
              </button>
              <ActionButton
                onClick={handlePayment}
                disabled={isPaying || paymentSuccessDetails !== null}
                className="w-full sm:w-auto text-lg"
              >
                  {isPaying ? 'Processing...' : (paymentSuccessDetails ? 'Paid' : 'Pay with Wallet')}
              </ActionButton>
            </div>
          </div>
        </div>
      </motion.div>

      {/* Success Popup */}
      <AnimatePresence>
        {paymentSuccessDetails && (
          <div className="fixed inset-0 bg-black/70 flex items-center justify-center z-50 p-4">
              <motion.div
                  initial={{ scale: 0.7, opacity: 0 }}
                  animate={{ scale: 1, opacity: 1 }}
                  exit={{ scale: 0.7, opacity: 0 }}
                  className="bg-slate-800 p-8 rounded-2xl shadow-2xl shadow-green-500/10 text-white max-w-md w-full text-center border border-slate-700"
              >
                  <CheckCircle className="text-green-400 w-16 h-16 mx-auto mb-4" />
                  <h3 className="text-2xl font-bold text-green-400">Payment Successful!</h3>
                  <p className="mt-2 text-slate-400">Redirecting to your profile shortly...</p>
                  <div className="mt-6 text-left bg-slate-900/50 p-4 rounded-lg space-y-2 border border-slate-700">
                      <p><strong>Payment ID:</strong> {paymentSuccessDetails.paymentId}</p>
                      <p><strong>Amount Paid:</strong> <span className="font-bold text-[#ebab5e]">${paymentSuccessDetails.price.toFixed(2)}</span></p>
                      <p><strong>Status:</strong> <span className="font-semibold text-green-400">{paymentSuccessDetails.paymentStatus}</span></p>
                      <p><strong>Date:</strong> {new Date(paymentSuccessDetails.paymentTimestamp).toLocaleString()}</p>
                  </div>
              </motion.div>
          </div>
        )}
      </AnimatePresence>
    </>
  );
};