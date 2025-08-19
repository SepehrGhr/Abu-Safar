import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Menu, X, User, Sun, Moon } from 'lucide-react';
import ShinyButton from '../common/ShinyButton';
import CarpetLogo from '../icons/Carpet.tsx';
import { Link } from 'react-router-dom';

const Header = ({ theme, setTheme }) => {
    const [isMenuOpen, setIsMenuOpen] = useState(false);
    const navLinks = [{ name: 'Flights', href: '#' }, { name: 'Buses', href: '#' }, { name: 'Trains', href: '#' }];
    const toggleTheme = () => setTheme(theme === 'light' ? 'dark' : 'light');

    return (
        <header className="bg-white/80 dark:bg-slate-950/50 backdrop-blur-md shadow-sm fixed top-0 left-0 right-0 z-50">
            <div className="container mx-auto px-4 sm:px-6 lg:px-8">
                <div className="flex items-center justify-between h-16">
                    <a href="/" className="flex items-center space-x-3 text-slate-950 dark:text-white">
                        <CarpetLogo className="h-8 w-8" />
                        <span className="font-aladin font-bold text-2xl text-gray-800 dark:text-white">AbuSafar</span>
                    </a>
                    <nav className="hidden md:flex flex-grow items-center justify-center">
                        {navLinks.map((link) => (
                            <a key={link.name} href={link.href} className="text-gray-600 dark:text-gray-300 hover:text-[#ebab5e] dark:hover:text-[#ebab5e] transition-colors font-medium px-4">{link.name}</a>
                        ))}
                    </nav>
                    <div className="hidden md:flex items-center space-x-4">
                        <Link to="/auth">
                            <motion.button whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }} className="px-4 py-2 text-sm font-medium text-gray-700 dark:text-gray-200 bg-gray-100/80 dark:bg-slate-800/80 border border-transparent rounded-xl hover:bg-gray-200/80 dark:hover:bg-slate-700/80">Login</motion.button>
                        </Link>
                        <Link to="/auth">
                           <ShinyButton><User size={16} /><span className="text-sm font-semibold">Sign Up</span></ShinyButton>
                        </Link>
                        <motion.button onClick={toggleTheme} whileHover={{ scale: 1.1 }} whileTap={{ scale: 0.9 }} className="p-2 rounded-full bg-gray-100/80 dark:bg-slate-800/80">
                            <AnimatePresence mode="wait" initial={false}>
                                <motion.div key={theme} initial={{ y: -20, opacity: 0, rotate: -90 }} animate={{ y: 0, opacity: 1, rotate: 0 }} exit={{ y: 20, opacity: 0, rotate: 90 }} transition={{ duration: 0.2 }}>
                                    {theme === 'light' ? <Moon size={20} className="text-gray-600"/> : <Sun size={20} className="text-yellow-400"/>}
                                </motion.div>
                            </AnimatePresence>
                        </motion.button>
                    </div>
                    <div className="md:hidden">
                        <button onClick={() => setIsMenuOpen(!isMenuOpen)} className="p-2 rounded-md text-gray-600 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-slate-800">{isMenuOpen ? <X size={24} /> : <Menu size={24} />}</button>
                    </div>
                </div>
            </div>
        </header>
    );
};

export default Header;