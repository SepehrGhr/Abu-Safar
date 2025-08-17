import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { Search, MapPin, Plane, Bus, Train, ArrowLeftRight } from 'lucide-react';
import TripTypeToggle from './TripTypeToggle';
import VehicleTypeToggle from './VehicleTypeToggle';
import ActionButton from '../common/ActionButton';

const SearchWidget = ({ activeTab, setActiveTab, onSearch }) => {
    const [tripType, setTripType] = useState('one-way');

    return (
        <div className="container mx-auto px-4 sm:px-6 lg:px-8">
            <div className="bg-black/20 backdrop-blur-xl rounded-2xl shadow-2xl p-4 border border-white/20">
                <div className="flex flex-col sm:flex-row justify-between items-center mb-4">
                    <VehicleTypeToggle activeTab={activeTab} setActiveTab={setActiveTab} />
                    <div className="mt-4 sm:mt-0">
                        <TripTypeToggle tripType={tripType} setTripType={setTripType} />
                    </div>
                </div>
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-12 gap-4 items-center">
                    <div className="relative lg:col-span-3">
                        <MapPin className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-white/50" />
                        <input type="text" placeholder="From" className="w-full bg-white/10 backdrop-blur-md text-white placeholder:text-white/50 border-2 border-transparent focus:border-yellow-400 focus:ring-0 rounded-xl py-3 pl-12 pr-4"/>
                    </div>
                    <motion.div whileTap={{ scale: 0.9 }} className="hidden lg:flex justify-center items-center lg:col-span-1">
                        <button className="p-2 bg-white/10 rounded-full border border-white/20 hover:bg-white/20 transition">
                            <ArrowLeftRight className="h-5 w-5 text-white/80" />
                        </button>
                    </motion.div>
                    <div className="relative lg:col-span-3">
                        <MapPin className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-white/50" />
                        <input type="text" placeholder="To" className="w-full bg-white/10 backdrop-blur-md text-white placeholder:text-white/50 border-2 border-transparent focus:border-yellow-400 focus:ring-0 rounded-xl py-3 pl-12 pr-4"/>
                    </div>
                    <div className="relative lg:col-span-4 grid grid-cols-2 gap-2">
                        <input type="date" className="w-full bg-white/10 backdrop-blur-md text-white placeholder:text-white/50 border-2 border-transparent focus:border-yellow-400 focus:ring-0 rounded-xl py-3 px-4"/>
                        <div className={`transition-opacity duration-300 ${tripType === 'one-way' ? 'opacity-50' : 'opacity-100'}`}>
                           <input
                                type="date"
                                placeholder="Return"
                                disabled={tripType === 'one-way'}
                                className="w-full bg-white/10 backdrop-blur-md text-white placeholder:text-white/50 border-2 border-transparent focus:border-yellow-400 focus:ring-0 rounded-xl py-3 px-4 disabled:cursor-not-allowed"
                            />
                        </div>
                    </div>
                    <div className="lg:col-span-1">
                        <ActionButton onClick={onSearch}>
                            <Search size={24}/>
                        </ActionButton>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default SearchWidget;