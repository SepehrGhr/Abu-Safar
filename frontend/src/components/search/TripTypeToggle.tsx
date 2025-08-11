import React from 'react';
import { motion } from 'framer-motion';

interface TripTypeToggleProps {
    tripType: 'one-way' | 'round-trip';
    setTripType: (type: 'one-way' | 'round-trip') => void;
}

const TripTypeToggle: React.FC<TripTypeToggleProps> = ({ tripType, setTripType }) => {
    return (
        <div className="flex items-center space-x-4 bg-black/20 p-1 rounded-full border border-white/10 text-white/80">
            <button onClick={() => setTripType('one-way')} className="relative px-4 py-1.5 rounded-full">
                {tripType === 'one-way' && (
                    <motion.div
                        layoutId="tripTypeBg"
                        className="absolute inset-0 bg-white dark:bg-gray-300 rounded-full"
                        transition={{type: 'spring', stiffness: 300, damping: 30}}
                    />
                )}
                <span className={`relative z-10 font-semibold text-sm transition ${tripType === 'one-way' ? 'text-orange-900' : 'text-white'}`}>
                    One-way
                </span>
            </button>
            <button onClick={() => setTripType('round-trip')} className="relative px-4 py-1.5 rounded-full">
                {tripType === 'round-trip' && (
                    <motion.div
                        layoutId="tripTypeBg"
                        className="absolute inset-0 bg-white dark:bg-gray-300 rounded-full"
                        transition={{type: 'spring', stiffness: 300, damping: 30}}
                    />
                )}
                <span className={`relative z-10 font-semibold text-sm transition ${tripType === 'round-trip' ? 'text-orange-900' : 'text-white'}`}>
                    Round-trip
                </span>
            </button>
        </div>
    );
};

export default TripTypeToggle;