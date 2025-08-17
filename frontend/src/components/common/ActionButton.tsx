import React from 'react';
import { motion } from 'framer-motion';


interface ActionButtonProps {
    children: React.ReactNode;
    onClick?: () => void;
    className?: string;
}

const ActionButton: React.FC<ActionButtonProps> = ({ children, onClick, className = '' }) => {
    return (
        <motion.button
            onClick={onClick}
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            className={`
                px-6 py-2 font-bold text-white rounded-lg transition-colors
                bg-brand-actionButtonLight hover:bg-brand-sandLight
                dark:bg-brand-actionButtonDark dark:hover:bg-brand-actionButtonDark/80
                ${className}
            `}
        >
            {children}
        </motion.button>
    );
};

export default ActionButton;
