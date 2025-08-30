import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import journeyImg1 from '../../assets/images/day.jpg';
import journeyImg2 from '../../assets/images/train.avif';
import journeyImg3 from '../../assets/images/night.jpg';

const journeys = [
    { id: 1, title: 'Journey Through Ancient Sands', image: journeyImg1 },
    { id: 2, title: 'Cross the Continental Divide', image: journeyImg2 },
    { id: 3, title: 'Explore Under the Stars', image: journeyImg3 },
];

const variants = {
    enter: (direction: number) => ({
        x: direction > 0 ? 1000 : -1000,
        opacity: 0
    }),
    center: {
        zIndex: 1,
        x: 0,
        opacity: 1
    },
    exit: (direction: number) => ({
        zIndex: 0,
        x: direction < 0 ? 1000 : -1000,
        opacity: 0
    })
};

export default function FeaturedJourneys() {
    const [[page, direction], setPage] = useState([0, 0]);

    const paginate = (newDirection: number) => {
        setPage([(page + newDirection + journeys.length) % journeys.length, newDirection]);
    };

    return (
        <div className="relative w-full h-[600px] overflow-hidden">
            <AnimatePresence initial={false} custom={direction}>
                <motion.img
                    key={page}
                    src={journeys[page].image}
                    custom={direction}
                    variants={variants}
                    initial="enter"
                    animate="center"
                    exit="exit"
                    transition={{
                        x: { type: "spring", stiffness: 300, damping: 30 },
                        opacity: { duration: 0.2 }
                    }}
                    className="absolute w-full h-full object-cover"
                />
            </AnimatePresence>
            <div className="absolute inset-0 bg-black/50"></div>
            
            {/* --- FIX: Added z-10 to ensure this content is on top of the image --- */}
            <div className="absolute inset-0 flex flex-col items-center justify-center text-white text-center p-4 z-10">
                <motion.h2 
                    key={page + '_title'}
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0, transition: { delay: 0.3, duration: 0.5 } }}
                    className="text-5xl font-aladin mb-4"
                >
                    {journeys[page].title}
                </motion.h2>
                <motion.button
                     key={page + '_button'}
                     initial={{ opacity: 0, y: 20 }}
                     animate={{ opacity: 1, y: 0, transition: { delay: 0.5, duration: 0.5 } }}
                     className="px-6 py-2 font-bold text-white rounded-lg transition-colors bg-brand-actionButtonDark hover:bg-brand-actionButtonDark/80"
                >
                    Explore Now
                </motion.button>
            </div>
            
            {/* --- FIX: Added z-10 to the navigation buttons --- */}
            <div className="absolute top-1/2 left-4 transform -translate-y-1/2 z-10">
                <button onClick={() => paginate(-1)} className="p-2 bg-white/20 rounded-full hover:bg-white/40 transition-colors">
                    <ChevronLeft />
                </button>
            </div>
            <div className="absolute top-1/2 right-4 transform -translate-y-1/2 z-10">
                <button onClick={() => paginate(1)} className="p-2 bg-white/20 rounded-full hover:bg-white/40 transition-colors">
                    <ChevronRight />
                </button>
            </div>
        </div>
    );
}