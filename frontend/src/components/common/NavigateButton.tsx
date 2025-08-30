import React from 'react';
import { motion } from 'framer-motion';

const NavigateButton = ({ onClick, children, className = '' }) => {
    return (
        <motion.button
            onClick={onClick}
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            className={`flex items-center space-x-1 text-sm font-semibold px-4 py-2 rounded-lg bg-white/80 dark:bg-slate-900/50 hover:shadow-lg transition-all text-gray-600 dark:text-gray-300 hover:text-yellow-500 dark:hover:text-yellow-400 ${className}`}
        >
            {children}
        </motion.button>
    );
};

export default NavigateButton;