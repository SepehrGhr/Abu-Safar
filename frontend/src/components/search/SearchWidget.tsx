import React from 'react';
import { motion } from 'framer-motion';
import { Search, MapPin, Plane, Bus, Train, ArrowLeftRight } from 'lucide-react';

const SearchWidget = ({ activeTab, setActiveTab, onSearch }) => {
    const tabs = [
        { id: 'FLIGHT', name: 'Flights', icon: <Plane className="h-5 w-5"/> },
        { id: 'BUS', name: 'Buses', icon: <Bus className="h-5 w-5"/> },
        { id: 'TRAIN', name: 'Trains', icon: <Train className="h-5 w-5"/> }
    ];

    return (
        <div className="container mx-auto px-4 sm:px-6 lg:px-8">
            <div className="bg-black/20 backdrop-blur-xl rounded-2xl shadow-2xl p-4 border border-white/20">
                <div className="flex justify-center mb-4">
                    <div className="flex bg-black/20 p-1 rounded-full border border-white/10">
                        {tabs.map((tab) => (
                            <button
                                key={tab.id}
                                onClick={() => setActiveTab(tab.id)}
                                className={`relative flex items-center space-x-2 px-4 py-2 font-semibold text-sm rounded-full transition-colors duration-300 ${activeTab === tab.id ? 'text-[#a57c44]' : 'text-white/80 hover:text-white'}`}
                            >
                                {activeTab === tab.id && <motion.div layoutId="activeTab" className="absolute inset-0 bg-white dark:bg-slate-300 rounded-full" transition={{type: 'spring', stiffness: 300, damping: 30}}/>}
                                <motion.div className="relative z-10">{tab.icon}</motion.div>
                                <span className="relative z-10">{tab.name}</span>
                            </button>
                        ))}
                    </div>
                </div>
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-12 gap-4 items-center">
                    <div className="relative lg:col-span-3"><MapPin className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-white/50" /><input type="text" placeholder="From" className="w-full bg-white/10 backdrop-blur-md text-white placeholder:text-white/50 border-2 border-transparent focus:border-[#ebab5e] focus:ring-0 rounded-xl py-3 pl-12 pr-4"/></div>
                    <motion.div whileTap={{ scale: 0.9 }} className="hidden lg:flex justify-center items-center lg:col-span-1"><button className="p-2 bg-white/10 rounded-full border border-white/20 hover:bg-white/20 transition"><ArrowLeftRight className="h-5 w-5 text-white/80" /></button></motion.div>
                    <div className="relative lg:col-span-3"><MapPin className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-white/50" /><input type="text" placeholder="To" className="w-full bg-white/10 backdrop-blur-md text-white placeholder:text-white/50 border-2 border-transparent focus:border-[#ebab5e] focus:ring-0 rounded-xl py-3 pl-12 pr-4"/></div>
                    <div className="relative lg:col-span-4"><input type="date" className="w-full bg-white/10 backdrop-blur-md text-white placeholder:text-white/50 border-2 border-transparent focus:border-[#ebab5e] focus:ring-0 rounded-xl py-3 px-4"/></div>
                    <div className="lg:col-span-1"><motion.button onClick={onSearch} whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }} className="w-full h-[52px] flex items-center justify-center bg-[#ebab5e] text-white font-bold rounded-xl hover:bg-[#d49e54] transition-colors shadow-lg hover:shadow-[#ebab5e]/50 group"><Search size={24}/></motion.button></div>
                </div>
            </div>
        </div>
    );
};

export default SearchWidget;