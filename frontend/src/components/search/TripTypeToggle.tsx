import React from 'react';
import { motion } from 'framer-motion';

const TripTypeToggle = ({ tripType, setTripType }) => {
    return (
        <div className="flex items-center bg-black/20 p-1 rounded-full border border-white/10">
            <button
                onClick={() => setTripType('one-way')}
                className={`relative px-4 py-1.5 text-sm font-semibold transition-colors duration-300 ${
                    tripType === 'one-way' ? 'text-gray-900' : 'text-white/80 hover:text-white'
                }`}
            >
                {tripType === 'one-way' && (
                    <motion.div
                        layoutId="tripTypeBg"
                        className="absolute inset-0 bg-white rounded-full"
                        transition={{ type: 'spring', stiffness: 300, damping: 30 }}
                    />
                )}
                <span className="relative z-10">One-way</span>
            </button>
            <button
                onClick={() => setTripType('round-trip')}
                className={`relative px-4 py-1.5 text-sm font-semibold transition-colors duration-300 ${
                    tripType === 'round-trip' ? 'text-gray-900' : 'text-white/80 hover:text-white'
                }`}
            >
                {tripType === 'round-trip' && (
                    <motion.div
                        layoutId="tripTypeBg"
                        className="absolute inset-0 bg-white rounded-full"
                        transition={{ type: 'spring', stiffness: 300, damping: 30 }}
                    />
                )}
                <span className="relative z-10">Round-trip</span>
            </button>
        </div>
    );
};

export default TripTypeToggle;