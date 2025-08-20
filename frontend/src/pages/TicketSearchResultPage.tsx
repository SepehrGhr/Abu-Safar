import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { motion } from 'framer-motion';
import { ChevronUp, ChevronDown } from 'lucide-react';

import { searchTickets } from '/src/services/api/tickets';
import type { Ticket, TicketSearchRequest } from '/src/services/api/types';

import PersistentSearchBar from '/src/components/search/PersistentSearchBar';
import Filters from '/src/components/search/Filters';
import TicketCard from '/src/components/search/TicketCard';
import NavigateButton from '/src/components/common/NavigateButton';

import backgroundDarkImage from '/src/assets/images/night.jpg';

const TicketSearchResultPage = () => {
    const [searchParams] = useSearchParams();
    const [tickets, setTickets] = useState<Ticket[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    // This query object is used for display purposes in the PersistentSearchBar and Filters.
    const query = {
        from: searchParams.get('from') || 'N/A',
        to: searchParams.get('to') || 'N/A',
        vehicleType: searchParams.get('vehicle')?.toUpperCase() || 'FLIGHT',
        departureDate: searchParams.get('departureDate') || '',
    };

    useEffect(() => {
        const fetchTickets = async () => {
            // --- Primary Search Parameters ---
            const vehicleType = searchParams.get('vehicle')?.toUpperCase();
            const fromId = searchParams.get('fromId');
            const toId = searchParams.get('toId');
            const departureDate = searchParams.get('departureDate');
            const tripType = searchParams.get('tripType')?.toUpperCase();

            // --- Filter Parameters from Sidebar and URL ---
            const age = searchParams.get('age')?.toUpperCase() || 'ADULT';
            const companies = searchParams.get('companies'); // e.g., "Mahan,IranAir"
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
                // --- Construct the API request object with all parameters ---
                const request: TicketSearchRequest = {
                    originId: parseInt(fromId, 10),
                    destinationId: parseInt(toId, 10),
                    departureDate,
                    tripVehicle: vehicleType as TicketSearchRequest['tripVehicle'],
                    ageCategory: age as TicketSearchRequest['ageCategory'],
                };

                // --- Conditionally add filter parameters if they exist in the URL ---
                if (companies) {
                    request.vehicleCompany = companies;
                }
                if (minPrice) {
                    request.minPrice = parseFloat(minPrice);
                }
                if (maxPrice) {
                    request.maxPrice = parseFloat(maxPrice);
                }
                if (busClass && vehicleType === 'BUS') {
                                    request.busClass = busClass.split(',');
                                }
                if (flightClass && vehicleType === 'FLIGHT') {
                                    request.flightClass = flightClass.split(',');
                }
                if (trainStars && vehicleType === 'TRAIN') {
                    request.trainStars = parseInt(trainStars, 10);
                }


                const results = await searchTickets(request);
                setTickets(results);
            } catch (err: any) {
                setError(err.message || "An unexpected error occurred.");
            } finally {
                setIsLoading(false);
            }
        };

        fetchTickets();
    }, [searchParams]); // The dependency on searchParams is crucial. It re-runs the search on ANY URL change.

    const listVariants = {
        hidden: { opacity: 0 },
        visible: { opacity: 1, transition: { staggerChildren: 0.1 } }
    };

    const renderContent = () => {
        if (isLoading) {
            return <div className="text-center p-10 text-white">Loading tickets...</div>;
        }

        if (error) {
            return <div className="text-center p-10 text-red-400 bg-black/20 rounded-lg">Error: {error}</div>;
        }

        if (tickets.length === 0) {
            return <div className="text-center p-10 text-white bg-black/20 rounded-lg">No tickets found for your search criteria.</div>;
        }

        return (
            <motion.div className="space-y-4" variants={listVariants} initial="hidden" animate="visible">
                {/* Assuming TicketCard can handle the Ticket type */}
                {tickets.map((ticket, index) => (
                    // It's better to find a unique ID from the ticket object itself if possible
                    <TicketCard key={ticket.tripId ? `${ticket.tripId}-${index}` : index} ticket={ticket} />
                ))}
            </motion.div>
        );
    };

    return (
        <div className="relative min-h-screen font-sans">
            {/* Backgrounds */}
            <div className="absolute inset-0 bg-brand-lightBackGround -z-10 block dark:hidden"></div>
            <div
                className="absolute inset-0 bg-cover bg-[center_top_5%] opacity-30 -z-10 hidden dark:block"
                style={{ backgroundImage: `url(${backgroundDarkImage})` }}
            ></div>

            <div className="relative z-10 pt-24 pb-16">
                <div className="container mx-auto px-4 sm:px-6 lg:px-8">
                    {/* PersistentSearchBar no longer needs the query prop as it's self-contained and URL-driven */}
                    <PersistentSearchBar />
                    <div className="grid grid-cols-1 lg:grid-cols-4 gap-8 mt-8">
                        {/* The vehicleType from the URL is passed here for dynamic filtering */}
                        <Filters vehicleType={query.vehicleType} />
                        <div className="lg:col-span-3">
                            <div className="flex justify-center mb-4">
                                <NavigateButton onClick={() => alert('Feature coming soon: Loading earlier results...')}>
                                    <ChevronUp size={16} />
                                    <span>Earlier</span>
                                </NavigateButton>
                            </div>

                            {renderContent()}

                            <div className="flex justify-center mt-4">
                                <NavigateButton onClick={() => alert('Feature coming soon: Loading later results...')}>
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