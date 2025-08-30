import React, { useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { MapPin, ChevronLeft, ChevronRight } from 'lucide-react';
import LocationSearchBar from './LocationSearchBar';
import type { Location } from '../../services/api/types';

// Helper function to format dates as YYYY-MM-DD
const formatDateForURL = (date: Date): string => {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
};

const PersistentSearchBar = () => {
    const [searchParams, setSearchParams] = useSearchParams();

    // State for the controlled LocationSearchBar components
    const [originQuery, setOriginQuery] = useState(searchParams.get('from') || '');
    const [destinationQuery, setDestinationQuery] = useState(searchParams.get('to') || '');

    // Get date from URL. Fallback to today if not present.
    // The 'T00:00:00Z' is important to ensure the date is parsed in UTC to avoid timezone issues.
    const currentDateStr = searchParams.get('departureDate') || formatDateForURL(new Date());
    const currentDate = new Date(currentDateStr + 'T00:00:00Z');

    const handleLocationSelect = (type: 'from' | 'to', location: Location) => {
        const newSearchParams = new URLSearchParams(searchParams);
        if (type === 'from') {
            setOriginQuery(location.city);
            newSearchParams.set('from', location.city);
            newSearchParams.set('fromId', String(location.locationId));
        } else {
            setDestinationQuery(location.city);
            newSearchParams.set('to', location.city);
            newSearchParams.set('toId', String(location.locationId));
        }
        setSearchParams(newSearchParams);
    };

    const changeDate = (days: number) => {
        const newDate = new Date(currentDate);
        newDate.setDate(newDate.getDate() + days);

        const newSearchParams = new URLSearchParams(searchParams);
        newSearchParams.set('departureDate', formatDateForURL(newDate));
        setSearchParams(newSearchParams);
    };

    const sharedInputStyle = "w-full bg-gray-100 dark:bg-slate-800 border-gray-300 dark:border-slate-700 rounded-md p-2 pl-10 focus:ring-[#ebab5e]/50 focus:border-[#ebab5e]";

    return (
        <div className="bg-white/80 dark:bg-slate-950/50 p-4 rounded-xl shadow-lg backdrop-blur-md mb-8">
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 items-center">
                {/* Origin Location Input */}
                <div className="relative">
                    <MapPin className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-400 z-10" />
                    <LocationSearchBar
                        query={originQuery}
                        onQueryChange={setOriginQuery}
                        onLocationSelect={(loc) => handleLocationSelect('from', loc)}
                        placeholder="From"
                        className={sharedInputStyle}
                    />
                </div>

                {/* Destination Location Input */}
                <div className="relative">
                    <MapPin className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-400 z-10" />
                    <LocationSearchBar
                        query={destinationQuery}
                        onQueryChange={setDestinationQuery}
                        onLocationSelect={(loc) => handleLocationSelect('to', loc)}
                        placeholder="To"
                        className={sharedInputStyle}
                    />
                </div>

                {/* Date Changer UI */}
                <div className="flex items-center justify-center space-x-2 text-slate-900 dark:text-white">
                    <button onClick={() => changeDate(-1)} className="p-2 rounded-md hover:bg-gray-200 dark:hover:bg-slate-700 transition-colors">
                        <ChevronLeft size={20} />
                    </button>
                    <div className="text-center">
                        <span className="font-semibold whitespace-nowrap">
                            {currentDate.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })}
                        </span>
                        <p className="text-xs text-gray-500 dark:text-gray-400">
                            {currentDate.toLocaleDateString('en-US', { weekday: 'long' })}
                        </p>
                    </div>
                    <button onClick={() => changeDate(1)} className="p-2 rounded-md hover:bg-gray-200 dark:hover:bg-slate-700 transition-colors">
                        <ChevronRight size={20} />
                    </button>
                </div>
            </div>
        </div>
    );
};

export default PersistentSearchBar;