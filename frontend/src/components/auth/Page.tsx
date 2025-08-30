import React from 'react';
import { motion } from 'framer-motion';

export default function Page({ children, isVisible, isFlipping = false }) {
    return (
        <motion.div
            className="absolute w-full h-full px-6 py-8 bg-transparent flex flex-col justify-center"
            style={{ 
                transformOrigin: 'left', 
                transformStyle: 'preserve-3d', 
                pointerEvents: isVisible ? 'auto' : 'none' 
            }}
            initial={{ rotateY: 0, zIndex: 0 }}
            animate={{
                rotateY: isFlipping ? -180 : 0,
                zIndex: isVisible ? 10 : (isFlipping ? 5 : 0),
                opacity: isVisible || isFlipping ? 1 : 0
            }}
            transition={{ duration: 0.8, ease: 'easeInOut' }}
        >
            <div className={isFlipping ? '[backface-visibility:hidden]' : ''}>
                {children}
            </div>
        </motion.div>
    );
}