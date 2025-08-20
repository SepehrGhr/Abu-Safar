import React from 'react';
import { motion } from 'framer-motion';

// --- Animated SVG Components ---

const AnimatedEye = ({ isOpen }) => (
    <motion.svg className="w-24 h-24" viewBox="0 0 100 100">
        <motion.path
            d="M10,50 C25,25 75,25 90,50 C75,75 25,75 10,50 Z"
            fill="none" stroke="currentColor" className="text-stone-700 dark:text-stone-400" strokeWidth="2"
            initial={{ pathLength: 0, opacity: 0 }}
            animate={{ pathLength: isOpen ? 1 : 0, opacity: isOpen ? 1 : 0 }}
            transition={{ duration: 1.5, delay: 0.5, ease: "easeInOut" }}
        />
        <motion.circle
            cx="50" cy="50" r="15"
            fill="none" stroke="currentColor" className="text-stone-700 dark:text-stone-400" strokeWidth="2"
            initial={{ pathLength: 0, opacity: 0 }}
            animate={{ pathLength: isOpen ? 1 : 0, opacity: isOpen ? 1 : 0 }}
            transition={{ duration: 1, delay: 1, ease: "easeInOut" }}
        />
    </motion.svg>
);


const AnimatedProfile = ({ isOpen }) => (
     <motion.svg className="w-24 h-24" viewBox="0 0 100 100">
        <motion.path
            d="M50,20 A15,15 0 1,1 50,50 A15,15 0 1,1 50,20 M25,90 A25,25 0 0,1 75,90 Z"
            fill="none" stroke="currentColor" className="text-stone-700 dark:text-stone-400" strokeWidth="2"
            initial={{ pathLength: 0 }}
            animate={{ pathLength: isOpen ? 1 : 0 }}
            transition={{ duration: 1.5, delay: 0.5, ease: "easeInOut" }}
        />
    </motion.svg>
);

const AnimatedShield = ({ isOpen }) => (
    <motion.svg className="w-24 h-24" viewBox="0 0 100 100">
        <motion.path
            d="M50,10 L90,30 L90,60 C90,80 50,95 50,95 C50,95 10,80 10,60 L10,30 Z"
            fill="none" stroke="currentColor" className="text-stone-700 dark:text-stone-400" strokeWidth="2"
            initial={{ pathLength: 0 }}
            animate={{ pathLength: isOpen ? 1 : 0 }}
            transition={{ duration: 1.5, delay: 0.5, ease: "easeInOut" }}
        />
        <motion.path
            d="M35,55 L45,65 L65,45"
            fill="none" stroke="currentColor" className="text-stone-700 dark:text-stone-400" strokeWidth="3"
            initial={{ pathLength: 0, opacity: 0 }}
            animate={{ pathLength: isOpen ? 1 : 0, opacity: isOpen ? 1 : 0 }}
            transition={{ pathLength: { delay: 1.5, duration: 0.5}, opacity: { delay: 1.5, duration: 0.1 } }}
        />
    </motion.svg>
);


export const PageInfo = ({ title, text, isOpen }) => {
    return (
        <div className="flex flex-col h-full items-center justify-center text-center p-4">

            <div className="flex items-center justify-center h-24 mb-4">
                {title === "Identity Verification" && <AnimatedEye isOpen={isOpen} />}
                {title === "Passenger Details" && <AnimatedProfile isOpen={isOpen} />}
                {title === "Account Security" && <AnimatedShield isOpen={isOpen} />}
            </div>

            <h2 className="page-info-title" style={{ fontFamily: "'Merriweather', serif" }}>
                {title}
            </h2>

            <p className="page-info-text mt-2">
                {text}
            </p>
        </div>
    );
};