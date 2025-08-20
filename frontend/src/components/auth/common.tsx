import React from 'react';
import { motion, useMotionTemplate, useMotionValue } from 'framer-motion';

export const InputField = ({ icon, ...props }) => (
    <div className="relative">
        <div className="absolute inset-y-0 left-0 flex items-center pl-3.5 pointer-events-none text-stone-400 dark:text-stone-500">
            {React.cloneElement(icon, { size: 20 })}
        </div>
        <input 
            className="input-field"
            {...props} 
        />
    </div>
);

export const ShinyButton = ({ children, className = '', ...props }) => {
    const mouseX = useMotionValue(0);
    const mouseY = useMotionValue(0);

    function handleMouseMove({ currentTarget, clientX, clientY }) {
        const { left, top } = currentTarget.getBoundingClientRect();
        mouseX.set(clientX - left);
        mouseY.set(clientY - top);
    }
    return (
        <motion.button
            onMouseMove={handleMouseMove} whileTap={{ scale: 0.95 }}
            className={`shiny-button group ${className}`}
            {...props}
        >
            <motion.div className="shiny-button-spotlight" style={{ background: useMotionTemplate`radial-gradient(200px circle at ${mouseX}px ${mouseY}px, rgba(235, 171, 94, 0.2), transparent 80%)` }}/>
            <div className="relative z-10 flex items-center justify-center space-x-2 font-bold text-sm">
                {children}
            </div>
        </motion.button>
    );
};

export const Footer = ({ text, actionText, onClick }) => (
    <div className="text-center text-xs">
        <p className="text-stone-500 dark:text-stone-400">
            {text}{' '}
            <button onClick={onClick} className="font-bold text-brand-actionButtonDark hover:underline">
                {actionText}
            </button>
        </p>
    </div>
);

export const Spinner = () => (
    <motion.div 
        animate={{ rotate: 360 }} 
        transition={{ duration: 1, repeat: Infinity, ease: "linear" }} 
        className="w-5 h-5 border-2 border-t-transparent border-brand-actionButtonDark rounded-full"
    ></motion.div>
);

// New Password Strength Meter Component
export const PasswordStrengthMeter = ({ strength }) => {
    const strengthColors = [
        'bg-stone-300 dark:bg-stone-700', // Strength 0
        'bg-red-500',                      // Strength 1
        'bg-orange-500',                   // Strength 2
        'bg-yellow-500',                   // Strength 3
        'bg-green-500'                     // Strength 4
    ];

    return (
        <div className="w-full h-1 bg-stone-200 dark:bg-stone-800 rounded-full mt-1.5">
            <motion.div 
                className={`h-1 rounded-full ${strengthColors[strength]}`}
                initial={{ width: 0 }}
                animate={{ width: `${(strength / 4) * 100}%` }}
                transition={{ duration: 0.5, ease: 'easeOut' }}
            />
        </div>
    );
};