import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { useNavigate } from 'react-router-dom';

import SearchWidget from '../search/SearchWidget';

import flightBackground from '../../assets/images/flight.avif';
import busBackground from '../../assets/images/bus.avif';
import trainBackground from '../../assets/images/train.avif';

const HeroSection = () => {
    const navigate = useNavigate();

    const [activeTab, setActiveTab] = useState('FLIGHT');

    const backgrounds = {
        FLIGHT: flightBackground,
        BUS: busBackground,
        TRAIN: trainBackground,
    };

    const handleSearch = () => {
        const mockQuery = { from: 'AGR', to: 'DXB', departureDate: '2025-10-10' };
        navigate('/results', { state: { query: mockQuery } });
    };

    return (
        <section className="relative h-screen min-h-[800px] flex items-center justify-center">
            <AnimatePresence>
                <motion.div
                    key={activeTab}
                    className="absolute inset-0"
                    initial={{ opacity: 0, scale: 1.1 }}
                    animate={{ opacity: 1, scale: 1.05 }}
                    exit={{ opacity: 0 }}
                    transition={{ duration: 1.5, ease: [0.4, 0, 0.2, 1] }}
                >
                    <img
                        src={backgrounds[activeTab]}
                        className="w-full h-full object-cover"
                        alt={`${activeTab} travel background`}
                    />
                    <div className="absolute inset-0 bg-black/40"></div>
                </motion.div>
            </AnimatePresence>
            <motion.div
                initial={{ opacity: 0, y: 50 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.6, delay: 0.2, ease: "easeOut" }}
                className="relative z-10 w-full"
            >
                <SearchWidget
                    onSearch={handleSearch}
                    activeTab={activeTab}
                    setActiveTab={setActiveTab}
                />
            </motion.div>
        </section>
    );
};

export default HeroSection;