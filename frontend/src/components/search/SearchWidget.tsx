import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { Search, MapPin, ArrowLeftRight } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

import TripTypeToggle from './TripTypeToggle';
import VehicleTypeToggle from './VehicleTypeToggle';
import ActionButton from '../common/ActionButton';
import LocationSearchBar from './LocationSearchBar';
import type { Location } from '../../services/api/types';

const SearchWidget = ({ activeTab, setActiveTab }) => {
    const [tripType, setTripType] = useState<'one-way' | 'round-trip'>('one-way');

    // State for the selected origin and destination (null until a valid choice is made)
    const [origin, setOrigin] = useState<Location | null>(null);
    const [destination, setDestination] = useState<Location | null>(null);

    // State for the text being typed into the search bars
    const [originQuery, setOriginQuery] = useState('');
    const [destinationQuery, setDestinationQuery] = useState('');

    const [departureDate, setDepartureDate] = useState('');
    const [returnDate, setReturnDate] = useState('');
    const [ageCategory, setAgeCategory] = useState<'ADULT' | 'CHILD' | 'BABY'>('ADULT');

    const navigate = useNavigate();

    const handleSearch = () => {
        // Validate that a location has been selected from the dropdown, not just typed
        if (!origin || !destination) {
            alert('Please select a valid origin and destination from the list.');
            return;
        }
        if (!departureDate) {
            alert('Please select a departure date.');
            return;
        }
        if (tripType === 'round-trip' && !returnDate) {
            alert('Please select a return date for a round trip.');
            return;
        }

        const queryParams = new URLSearchParams({
            vehicle: activeTab,
            from: origin.city,
            to: destination.city,
            fromId: String(origin.locationId),
            toId: String(destination.locationId),
            departureDate: departureDate,
            tripType: tripType,
            age: ageCategory,
        });

        if (tripType === 'round-trip' && returnDate) {
            queryParams.append('returnDate', returnDate);
        }

        navigate(`/ticket-search-result?${queryParams.toString()}`);
    };

    const handleSwapLocations = () => {
        // Swap both the selected location objects and the text in the inputs
        const tempOrigin = origin;
        const tempOriginQuery = originQuery;

        setOrigin(destination);
        setOriginQuery(destinationQuery);

        setDestination(tempOrigin);
        setDestinationQuery(tempOriginQuery);
    };

    const sharedInputStyle = "w-full bg-white/10 backdrop-blur-md text-white placeholder:text-white/50 border-2 border-transparent focus:border-yellow-400 focus:ring-0 rounded-xl py-3 pl-12 pr-4";

    return (
        <div className="container mx-auto px-4 sm:px-6 lg:px-8">
            <div className="bg-black/20 backdrop-blur-xl rounded-2xl shadow-2xl p-4 border border-white/20">
                <div className="flex flex-col sm:flex-row justify-between items-center mb-4">
                    <VehicleTypeToggle activeTab={activeTab} setActiveTab={setActiveTab} />
                    <div className="mt-4 sm:mt-0">
                        <TripTypeToggle tripType={tripType} setTripType={setTripType} />
                    </div>
                </div>
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-12 gap-4 items-center">
                    {/* FROM Input */}
                    <div className="relative lg:col-span-3">
                        <MapPin className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-white/50 z-20" />
                        <LocationSearchBar
                            value={origin}
                            query={originQuery}
                            onQueryChange={(query) => {
                                setOriginQuery(query);
                                // If user types, the previous selection is no longer valid
                                if (origin) setOrigin(null);
                            }}
                            onLocationSelect={(location) => {
                                setOrigin(location);
                                // Sync the input text with the selected city name
                                setOriginQuery(location.city);
                            }}
                            placeholder="From"
                            className={sharedInputStyle}
                        />
                    </div>

                    {/* SWAP Button */}
                    <motion.div whileTap={{ scale: 0.9 }} className="hidden lg:flex justify-center items-center lg:col-span-1">
                        <button onClick={handleSwapLocations} className="p-2 bg-white/10 rounded-full border border-white/20 hover:bg-white/20 transition">
                            <ArrowLeftRight className="h-5 w-5 text-white/80" />
                        </button>
                    </motion.div>

                    {/* TO Input */}
                    <div className="relative lg:col-span-3">
                        <MapPin className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-white/50 z-20" />
                        <LocationSearchBar
                            value={destination}
                            query={destinationQuery}
                            onQueryChange={(query) => {
                                setDestinationQuery(query);
                                if (destination) setDestination(null);
                            }}
                            onLocationSelect={(location) => {
                                setDestination(location);
                                setDestinationQuery(location.city);
                            }}
                            placeholder="To"
                            className={sharedInputStyle}
                        />
                    </div>

                    {/* DATE Inputs */}
                    <div className="relative lg:col-span-4 grid grid-cols-2 gap-2">
                        <input
                            type="date"
                            value={departureDate}
                            onChange={(e) => setDepartureDate(e.target.value)}
                            className="w-full bg-white/10 backdrop-blur-md text-white placeholder:text-white/50 border-2 border-transparent focus:border-yellow-400 focus:ring-0 rounded-xl py-3 px-4"
                        />
                        <div className={`transition-opacity duration-300 ${tripType === 'one-way' ? 'opacity-50' : 'opacity-100'}`}>
                           <input
                                type="date"
                                value={returnDate}
                                onChange={(e) => setReturnDate(e.target.value)}
                                placeholder="Return"
                                disabled={tripType === 'one-way'}
                                className="w-full bg-white/10 backdrop-blur-md text-white placeholder:text-white/50 border-2 border-transparent focus:border-yellow-400 focus:ring-0 rounded-xl py-3 px-4 disabled:cursor-not-allowed"
                            />
                        </div>
                    </div>

                    {/* SEARCH Button */}
                    <div className="lg:col-span-1">
                        <ActionButton onClick={handleSearch}>
                            <Search size={24}/>
                        </ActionButton>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default SearchWidget;
