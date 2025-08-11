import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { Search, MapPin, ArrowLeftRight, Plane, Bus, Train } from 'lucide-react';
import TripTypeToggle from './TripTypeToggle';
import CustomDateInput from './CustomDateInput';

interface SearchWidgetProps {
    activeTab: 'FLIGHT' | 'BUS' | 'TRAIN';
    setActiveTab: (tab: 'FLIGHT' | 'BUS' | 'TRAIN') => void;
}

const SearchWidget: React.FC<SearchWidgetProps> = ({ activeTab, setActiveTab }) => {
    const [tripType, setTripType] = useState<'one-way' | 'round-trip'>('one-way');

    const tabs = [
        { id: 'FLIGHT', name: 'Flights', icon: <Plane className="h-5 w-5"/> },
        { id: 'BUS', name: 'Buses', icon: <Bus className="h-5 w-5"/> },
        { id: 'TRAIN', name: 'Trains', icon: <Train className="h-5 w-5"/> },
    ];

    return (
        <div className="container mx-auto px-4 sm:px-6 lg:px-8">
            <div className="bg-black/20 backdrop-blur-xl rounded-2xl shadow-2xl p-4 border border-white/20">
                {/* Top Row: Tabs and Trip Type */}
                <div className="flex flex-col sm:flex-row justify-between items-center mb-4">
                    <div className="flex bg-black/20 p-1 rounded-full border border-white/10">
                        {tabs.map((tab) => (
                            <button
                                key={tab.id}
                                onClick={() => setActiveTab(tab.id as 'FLIGHT' | 'BUS' | 'TRAIN')}
                                className={`relative flex items-center space-x-2 px-4 py-2 font-semibold text-sm rounded-full transition-colors duration-300 ${activeTab === tab.id ? 'text-orange-900' : 'text-white/80 hover:text-white'}`}
                            >
                                {activeTab === tab.id && <motion.div layoutId="activeTab" className="absolute inset-0 bg-white dark:bg-gray-300 rounded-full" transition={{type: 'spring', stiffness: 300, damping: 30}} />}
                                <span className="relative z-10">{tab.icon}</span>
                                <span className="relative z-10">{tab.name}</span>
                            </button>
                        ))}
                    </div>
                    <div className="mt-4 sm:mt-0">
                        <TripTypeToggle tripType={tripType} setTripType={setTripType} />
                    </div>
                </div>

                {/* Search Form Inputs */}
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-12 gap-4 items-center">
                    {/* From */}
                    <div className="relative lg:col-span-3">
                        <MapPin className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-white/50" />
                        <input type="text" placeholder="From" className="w-full bg-white/10 backdrop-blur-md text-white placeholder:text-white/50 border-2 border-transparent focus:border-orange-400 focus:ring-0 rounded-xl py-3 pl-12 pr-4"/>
                    </div>

                    <motion.div whileTap={{ scale: 0.9 }} className="hidden lg:flex justify-center items-center lg:col-span-1">
                        <button className="p-2 bg-white/10 rounded-full border border-white/20 hover:bg-white/20 transition">
                            <ArrowLeftRight className="h-5 w-5 text-white/80" />
                        </button>
                    </motion.div>

                    {/* To */}
                    <div className="relative lg:col-span-3">
                        <MapPin className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-white/50" />
                        <input type="text" placeholder="To" className="w-full bg-white/10 backdrop-blur-md text-white placeholder:text-white/50 border-2 border-transparent focus:border-orange-400 focus:ring-0 rounded-xl py-3 pl-12 pr-4"/>
                    </div>

                    {/* Dates */}
                    <div className="relative lg:col-span-4 grid grid-cols-2 gap-2">
                         <CustomDateInput id="departure-date" placeholder="Departure" />
                         <div className={`transition-opacity duration-300 ${tripType === 'one-way' ? 'opacity-50' : 'opacity-100'}`}>
                            <CustomDateInput id="return-date" placeholder="Return" disabled={tripType === 'one-way'} />
                         </div>
                    </div>

                    {/* Search Button */}
                    <div className="lg:col-span-1">
                         <motion.button
                             whileHover={{ scale: 1.05 }}
                             whileTap={{ scale: 0.95 }}
                             className="w-full h-[52px] flex items-center justify-center bg-orange-500 text-white font-bold rounded-xl hover:bg-orange-600 transition-colors shadow-lg hover:shadow-orange-400/50"
                           >
                             <Search size={24}/>
                         </motion.button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default SearchWidget;