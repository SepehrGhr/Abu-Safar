import React, { useState, useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import { ChevronUp, ChevronDown } from 'lucide-react';
import { searchTickets } from '../services/api/tickets';
import { createReservation } from '../services/api/reservations';
import { useAuth } from '@/context/AuthContext';
import type { Ticket, TicketSearchRequest, ReserveConfirmation } from '../services/api/types';

import PersistentSearchBar from '../components/search/PersistentSearchBar';
import Filters from '../components/search/Filters';
import TicketCard from '../components/search/TicketCard';
import NavigateButton from '../components/common/NavigateButton';
import ShinyButton from '../components/common/ShinyButton';
import SelectedTicket from '../components/search/SelectedTicket';

import backgroundDarkImage from '../assets/images/night.jpg';

const TicketSearchResultPage = () => {
     const [searchParams] = useSearchParams();
     const navigate = useNavigate();
     const { isAuthenticated } = useAuth();

     const [tickets, setTickets] = useState<Ticket[]>([]);
     const [isLoading, setIsLoading] = useState(true);
     const [error, setError] = useState<string | null>(null);
     const [selectedTickets, setSelectedTickets] = useState<Ticket[]>([]);
     const [isCreatingReservation, setIsCreatingReservation] = useState(false);

     // --- Derived State from URL ---
     const tripType = searchParams.get('tripType')?.toUpperCase();
     const isRoundTrip = tripType === 'ROUNDTRIP';
     const ageCategory = searchParams.get('age')?.toUpperCase() || 'ADULT';

     // --- Data Fetching Effect ---
     useEffect(() => {
         const fetchTickets = async () => {
             const vehicleType = searchParams.get('vehicle')?.toUpperCase();
             const fromId = searchParams.get('fromId');
             const toId = searchParams.get('toId');
             const departureDate = searchParams.get('departureDate');
             const companies = searchParams.get('companies');
             const minPrice = searchParams.get('minPrice');
             const maxPrice = searchParams.get('maxPrice');
             const busClass = searchParams.get('busClass');
             const flightClass = searchParams.get('flightClass');
             const trainStars = searchParams.get('trainStars');

             if (!vehicleType || !fromId || !toId || !departureDate || !tripType) {
                 setError("Missing required search parameters in the URL.");
                 setIsLoading(false);
                 return;
             }
             setIsLoading(true);
             setError(null);
             try {
                 const request: TicketSearchRequest = {
                     originId: parseInt(fromId, 10),
                     destinationId: parseInt(toId, 10),
                     departureDate,
                     tripVehicle: vehicleType as TicketSearchRequest['tripVehicle'],
                     ageCategory: ageCategory as TicketSearchRequest['ageCategory'],
                 };
                 if (companies) request.vehicleCompany = companies;
                 if (minPrice) request.minPrice = parseFloat(minPrice);
                 if (maxPrice) request.maxPrice = parseFloat(maxPrice);
                 if (busClass && vehicleType === 'BUS') request.busClass = busClass.split(',');
                 if (flightClass && vehicleType === 'FLIGHT') request.flightClass = flightClass.split(',');
                 if (trainStars && vehicleType === 'TRAIN') request.trainStars = parseInt(trainStars, 10);
                 const results = await searchTickets(request);
                 setTickets(results);
             } catch (err: any) {
                 setError(err.message || "An unexpected error occurred.");
             } finally {
                 setIsLoading(false);
             }
         };
         fetchTickets();
     }, [searchParams, ageCategory, tripType]);


     const handleSelectTicket = (ticket: Ticket) => {
         const maxSelection = isRoundTrip ? 2 : 1;
         if (selectedTickets.length < maxSelection) {

             const ticketWithAge = { ...ticket, ageCategory };
             setSelectedTickets(prev => [...prev, ticketWithAge]);
         }
     };

     const handleDeselectTicket = (ticketId: string) => {
         setSelectedTickets(prev => prev.filter(t => t.tripId !== ticketId));
     };

     const handleProceedToReservation = async () => {

         if (!isAuthenticated) {
             setError("You must be logged in to make a reservation.");

             setTimeout(() => setError(null), 5000);
             return;
         }

         if (isCreatingReservation) return;

         setIsCreatingReservation(true);
         setError(null);
         try {
             const reservationDetails: ReserveConfirmation = await createReservation(selectedTickets, isRoundTrip);


             navigate('/reservation', { state: { reservationDetails } });
         } catch (err: any) {
             setError(err.message || "Could not create your reservation. Please try again.");
         } finally {
             setIsCreatingReservation(false);
         }
     };


     const canProceed = isRoundTrip ? selectedTickets.length === 2 : selectedTickets.length === 1;
     const topBarTitle = isRoundTrip && selectedTickets.length === 1
         ? "Find your return ticket"
         : "Find your ticket";
     const unselectedTickets = tickets.filter(t => !selectedTickets.find(st => st.tripId === t.tripId));

     const renderTicketList = () => {
         if (isLoading) return <div className="text-center p-10 text-white">Loading tickets...</div>;
         if (error && !isCreatingReservation) return null; // Error is now shown at the top
         if (tickets.length === 0 && !isLoading) return <div className="text-center p-10 text-white bg-black/20 rounded-lg">No tickets found.</div>;

         return (
             <motion.div className="space-y-4" layout>
                 <AnimatePresence>
                     {unselectedTickets.map((ticket) => (
                         <TicketCard
                             key={ticket.tripId}
                             ticket={ticket}
                             onSelect={() => handleSelectTicket(ticket)}
                             isDisabled={selectedTickets.length >= (isRoundTrip ? 2 : 1)}
                         />
                     ))}
                 </AnimatePresence>
             </motion.div>
         );
     };

     return (
         <div className="relative min-h-screen font-sans">
             <div className="absolute inset-0 bg-brand-lightBackGround -z-10 block dark:hidden"></div>
             <div
                 className="absolute inset-0 bg-cover bg-[center_top_5%] opacity-30 -z-10 hidden dark:block"
                 style={{ backgroundImage: `url(${backgroundDarkImage})` }}
             ></div>

             <div className="relative z-10 pt-24 pb-16">
                 <div className="container mx-auto px-4 sm:px-6 lg:px-8">
                     <div className="h-24 flex items-center justify-between mb-4">
                         <h2 className="text-5xl font-bold font-aladin text-white">{topBarTitle}</h2>
                         <div className="flex items-center">
                             <AnimatePresence>
                                 {selectedTickets.map((ticket, index) => (
                                     <SelectedTicket
                                         key={ticket.tripId}
                                         ticket={ticket}
                                         onDeselect={() => handleDeselectTicket(ticket.tripId)}
                                         isOverlapped={index > 0}
                                     />
                                 ))}
                             </AnimatePresence>
                             <AnimatePresence>
                                 {canProceed && (
                                     <motion.div
                                         initial={{ scale: 0 }}
                                         animate={{ scale: 1 }}
                                         exit={{ scale: 0 }}
                                         className="ml-4"
                                     >
                                         <ShinyButton
                                             onClick={handleProceedToReservation}
                                             disabled={isCreatingReservation}
                                             className="text-4xl px-6 py-3 text-lg bg-green-600 hover:shadow-green-500/30 font-aladin"
                                         >
                                             {isCreatingReservation ? 'Creating...' : 'Proceed to Reservation'}
                                         </ShinyButton>
                                     </motion.div>
                                 )}
                             </AnimatePresence>
                         </div>
                     </div>

                     {/* --- NEW: Centralized Error Display --- */}
                     <AnimatePresence>
                         {error && (
                              <motion.div
                                 initial={{ opacity: 0, y: -20 }}
                                 animate={{ opacity: 1, y: 0 }}
                                 exit={{ opacity: 0, y: -20 }}
                                 className="flex items-center justify-center text-center p-4 mb-4 text-red-300 bg-red-900/50 rounded-lg border border-red-700"
                             >
                                 <AlertCircle className="mr-3" />
                                 {error}
                             </motion.div>
                         )}
                     </AnimatePresence>

                     <PersistentSearchBar />
                     <div className="grid grid-cols-1 lg:grid-cols-4 gap-8 mt-8">
                         <Filters vehicleType={searchParams.get('vehicle')?.toUpperCase() || 'FLIGHT'} />
                         <div className="lg:col-span-3">
                             <div className="flex justify-center mb-4">
                                 <NavigateButton onClick={() => alert('Feature coming soon...')}>
                                     <ChevronUp size={16} />
                                     <span>Earlier</span>
                                 </NavigateButton>
                             </div>
                             {renderTicketList()}
                             <div className="flex justify-center mt-4">
                                 <NavigateButton onClick={() => alert('Feature coming soon...')}>
                                     <ChevronDown size={16} />
                                     <span>Later</span>
                                 </NavigateButton>
                             </div>
                         </div>
                     </div>
                 </div>
             </div>
         </div>
     );
 };

 export default TicketSearchResultPage;
