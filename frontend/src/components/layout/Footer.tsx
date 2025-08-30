import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { ArrowUp } from 'lucide-react';
import CarpetLogo from '../icons/Carpet';

const Footer = () => {
    const [isVisible, setIsVisible] = useState(false);

    // Show button when page is scrolled up to a certain amount
    const toggleVisibility = () => {
        if (window.pageYOffset > 300) {
            setIsVisible(true);
        } else {
            setIsVisible(false);
        }
    };

    const scrollToTop = () => {
        window.scrollTo({
            top: 0,
            behavior: 'smooth'
        });
    };

    useEffect(() => {
        window.addEventListener('scroll', toggleVisibility);
        return () => window.removeEventListener('scroll', toggleVisibility);
    }, []);

    return (
        <>
            <footer className="bg-brand-sandLight text-gray-600 dark:bg-slate-950/50 dark:text-gray-400 backdrop-blur-sm">
                <div className="container mx-auto py-8 px-4 sm:px-6 lg:px-8">
                    <div className="flex justify-center items-center">
                        <div className="flex items-center space-x-3">
                            <CarpetLogo className="h-6 w-6 text-slate-700 dark:text-white" />
                            <p className="text-sm text-gray-500 dark:text-gray-400">
                                &copy; {new Date().getFullYear()} AbuSafar Technologies Inc. All rights reserved.
                            </p>
                        </div>
                    </div>
                </div>
            </footer>
            
            {/* Back to Top Button */}
            <AnimatePresence>
                {isVisible && (
                    <motion.button
                        onClick={scrollToTop}
                        className="fixed bottom-8 right-8 bg-passport-gold text-passport-cover-rich p-3 rounded-full shadow-lg z-50"
                        initial={{ opacity: 0, y: 20 }}
                        animate={{ opacity: 1, y: 0 }}
                        exit={{ opacity: 0, y: 20 }}
                        whileHover={{ scale: 1.1 }}
                        whileTap={{ scale: 0.9 }}
                    >
                        <ArrowUp size={20} />
                    </motion.button>
                )}
            </AnimatePresence>
        </>
    );
};

export default Footer;