import React, { useState, useRef } from 'react';
import { motion, useMotionValue, useTransform, AnimatePresence } from 'framer-motion';
import PassportCover from './PassportCover';
import PassportInterior from './PassportInterior';

export default function DigitalPassport() {
    const [isOpen, setIsOpen] = useState(false);
    const ref = useRef(null);
    const mouseX = useMotionValue(0);
    const mouseY = useMotionValue(0);

    const handleMouseMove = (e) => {
        if (!ref.current || isOpen) return;
        const rect = ref.current.getBoundingClientRect();
        mouseX.set(e.clientX - rect.left);
        mouseY.set(e.clientY - rect.top);
    };

    const rotateX = useTransform(mouseY, [0, 640], [10, -10]);
    const rotateY = useTransform(mouseX, [0, 480], [-10, 10]);

    return (
        <div className="w-full max-w-lg h-[640px] [perspective:2000px]" ref={ref} onMouseMove={handleMouseMove}>
            <motion.div
                className="relative w-full h-full"
                style={{ transformStyle: 'preserve-3d', rotateX: isOpen ? 0 : rotateX, rotateY: isOpen ? 0 : rotateY }}
                transition={{ duration: 1.2, ease: [0.76, 0, 0.24, 1] }}
            >
                <AnimatePresence>
                    {!isOpen ? (
                        <motion.div
                            key="cover"
                            className="absolute w-full h-full"
                            exit={{ opacity: 0, scale: 0.95, transition: { duration: 0.3 } }}
                        >
                            <PassportCover onOpen={() => setIsOpen(true)} mouseX={mouseX} mouseY={mouseY} />
                        </motion.div>
                    ) : (
                        <motion.div
                            key="interior"
                            className="absolute w-full h-full"
                            initial={{ opacity: 0, scale: 0.95 }}
                            animate={{ opacity: 1, scale: 1, transition: { duration: 0.3, delay: 0.2 } }}
                        >
                            <PassportInterior isOpen={isOpen} onSignOut={() => setIsOpen(false)}/>
                        </motion.div>
                    )}
                </AnimatePresence>
            </motion.div>
        </div>
    );
}