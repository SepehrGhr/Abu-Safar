import React from 'react';
import { motion, useMotionTemplate } from 'framer-motion';
import CarpetLogo from '../icons/Carpet';

// A simple SVG component for the decorative corners
const CornerFlourish = ({ className }) => (
    <svg className={`absolute w-8 h-8 text-passport-gold/50 ${className}`} viewBox="0 0 100 100" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M100 100H0V0H5V95H100V100Z" fill="currentColor"/>
        <path d="M10 10H15V15H10V10Z M20 10H25V15H20V10Z M10 20H15V25H10V20Z" fill="currentColor"/>
    </svg>
);

// New: An abstract line-art pattern for the background
const BackgroundPattern = () => (
    <svg className="absolute w-full h-full text-passport-gold/10 pointer-events-none" preserveAspectRatio="xMidYMid slice" viewBox="0 0 300 200">
        <path d="M-13.28,88.42 C29.43,111.45 74.32,112.43 103.96,93.52 C122.38,81.43 134.46,62.83 141.42,43.21 C149.33,20.89 162.13,0.36 184.82,-9.31 C205.9,-18.31 231.3,-16.94 251.27,-4.96 C273.4,8.42 291.6,31.83 299.8,56.51 C305.61,74.22 303.34,93.47 294.61,109.97 C282.88,131.74 263.3,148.23 241.13,155.23 C208.5,165.58 172.03,158.62 143.95,140.35 C118.6,123.85 98.4,98.24 92.4,69.54 C88.39,49.88 90.1,29.13 101.95,12.3 C111.16,-0.53 125.75,-9.05 140.09,-12.34" stroke="currentColor" strokeWidth="0.5" fill="none" />
        <path d="M308.28,95.42 C275.57,72.39 230.68,71.41 201.04,90.32 C182.62,102.41 170.54,121.01 163.58,140.63 C155.67,162.95 142.87,183.48 120.18,193.15 C99.1,202.15 73.7,200.78 53.73,188.8 C31.6,175.42 13.4,152.01 5.2,127.33 C-0.61,109.62 1.66,90.37 10.39,73.87 C22.12,52.1 41.7,35.59 63.87,28.59 C96.5,18.24 132.97,25.2 161.05,43.49 C186.4,60.00 206.6,85.60 212.6,114.3 C216.61,133.96 214.9,154.71 203.05,171.46 C193.84,184.37 179.25,192.89 164.91,196.18" stroke="currentColor" strokeWidth="0.5" fill="none" />
    </svg>
)

export default function PassportCover({ onOpen, mouseX, mouseY }) {
    return (
        <div className="w-full h-full bg-passport-cover-rich rounded-2xl shadow-2xl p-8 flex flex-col items-center justify-center text-white relative overflow-hidden">
            {/* Leather texture overlay */}
            <div className="absolute inset-0 opacity-[0.03]" style={{ backgroundImage: 'url(https://www.transparenttextures.com/patterns/leather.png)' }}></div>
            
            <div className="absolute inset-0 rounded-2xl border-2 border-passport-gold/30 pointer-events-none"></div>
            <div className="absolute inset-2 rounded-xl border border-passport-gold/20 pointer-events-none"></div>

            {/* Background Pattern */}
            <BackgroundPattern />

            <CornerFlourish className="top-4 left-4" />
            <CornerFlourish className="top-4 right-4 transform rotate-90" />
            <CornerFlourish className="bottom-4 left-4 transform -rotate-90" />
            <CornerFlourish className="bottom-4 right-4 transform rotate-180" />

            <motion.div className="pointer-events-none absolute -inset-px rounded-xl opacity-0 transition duration-300 group-hover:opacity-100" style={{ background: useMotionTemplate`radial-gradient(400px circle at ${mouseX}px ${mouseY}px, rgba(255, 255, 255, 0.1), transparent 80%)` }} />
            
            <div className="absolute inset-0 bg-gradient-to-br from-transparent via-transparent to-black/50"></div>
            
            <div className="text-center z-10 flex flex-col items-center justify-center">
                <div className="text-passport-gold" style={{ filter: 'drop-shadow(0 2px 3px rgba(0,0,0,0.4))' }}>
                    <CarpetLogo className="h-20 w-20" />
                </div>
                <h1 className="text-4xl font-bold mt-4 text-transparent bg-clip-text bg-gradient-to-b from-passport-gold to-[#a88a2e]" style={{ textShadow: '0 2px 4px rgba(0,0,0,0.5)', fontFamily: "'Merriweather', serif" }}>
                    AbuSafar
                </h1>
                <p className="text-lg text-passport-gold/80" style={{ textShadow: '0 1px 2px rgba(0,0,0,0.3)' }}>
                    Digital Passport
                </p>
            </div>

            <motion.button 
                onClick={onOpen} 
                className="absolute bottom-12 bg-black/20 text-passport-gold font-bold py-3 px-8 rounded-xl border border-passport-gold/50 z-10" 
                whileHover={{ scale: 1.05, backgroundColor: 'rgba(0,0,0,0.3)' }} 
                whileTap={{ scale: 0.95 }}
            >
                Open Passport to Begin
            </motion.button>
        </div>
    );
}