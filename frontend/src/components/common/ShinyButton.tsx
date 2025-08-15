import React from 'react';
import { motion, useMotionTemplate, useMotionValue } from 'framer-motion';

// Define the props type for our component
interface ShinyButtonProps {
    children: React.ReactNode;
    className?: string;
}

const ShinyButton: React.FC<ShinyButtonProps> = ({ children, className = '' }) => {
    const mouseX = useMotionValue(0);
    const mouseY = useMotionValue(0);

    function handleMouseMove({ currentTarget, clientX, clientY }: React.MouseEvent<HTMLButtonElement>) {
        const { left, top } = currentTarget.getBoundingClientRect();
        mouseX.set(clientX - left);
        mouseY.set(clientY - top);
    }

    return (
        <motion.button
            onMouseMove={handleMouseMove}
            whileTap={{ scale: 0.95 }}
            className={`group relative w-full rounded-xl border border-orange-500/20 bg-orange-50/10 px-4 py-2 text-orange-600 shadow-lg transition-shadow duration-300 hover:shadow-orange-400/20 dark:bg-orange-900/10 dark:text-orange-400 dark:shadow-orange-900/20 dark:hover:shadow-orange-900/40 ${className}`}
        >
            <motion.div
                className="pointer-events-none absolute -inset-px rounded-xl opacity-0 transition duration-300 group-hover:opacity-100"
                style={{
                    background: useMotionTemplate`
                        radial-gradient(
                            200px circle at ${mouseX}px ${mouseY}px,
                            rgba(235, 171, 94, 0.2),
                            transparent 80%
                        )
                    `,
                }}
            />
            <div className="relative z-10 flex items-center justify-center space-x-2">
                {children}
            </div>
        </motion.button>
    );
};

export default ShinyButton;