import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import SearchWidget from '../search/SearchWidget';

const HeroSection = () => {
    const [activeTab, setActiveTab] = useState<'FLIGHT' | 'BUS' | 'TRAIN'>('FLIGHT');

    const backgrounds = {
        FLIGHT: 'https://images.unsplash.com/photo-1506012787146-f92b2d7d6d96?q=80&w=2669&auto=format&fit=crop',
        BUS: 'https://images.unsplash.com/photo-1706367340442-80a7aa36493e?q=80&w=2670&auto=format&fit=crop',
        TRAIN: 'https://images.unsplash.com/photo-1505832018823-50331d70d237?q=80&w=2708&auto=format&fit=crop'
    };

    return (
        <section className="relative h-screen min-h-[800px] flex items-center justify-center">
            {/* Background Image with animation */}
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

            {/* Search Widget Container */}
            <motion.div
                initial={{ opacity: 0, y: 50 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.6, delay: 0.2, ease: "easeOut" }}
                className="relative z-10 w-full"
            >
                <SearchWidget activeTab={activeTab} setActiveTab={setActiveTab} />
            </motion.div>
        </section>
    );
};

export default HeroSection;