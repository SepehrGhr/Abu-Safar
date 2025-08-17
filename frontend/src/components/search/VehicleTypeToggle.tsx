import React from 'react';
import { motion } from 'framer-motion';
import { Plane, Bus, Train } from 'lucide-react';

const tabs = [
    { id: 'FLIGHT', name: 'Flights', icon: <Plane className="h-5 w-5" /> },
    { id: 'BUS', name: 'Buses', icon: <Bus className="h-5 w-5" /> },
    { id: 'TRAIN', name: 'Trains', icon: <Train className="h-5 w-5" /> }
];

const VehicleTypeToggle = ({ activeTab, setActiveTab }) => {
    return (
        <div className="flex bg-black/20 p-1 rounded-full border border-white/10">
            {tabs.map((tab) => (
                <button
                    key={tab.id}
                    onClick={() => setActiveTab(tab.id)}
                    className={`relative flex items-center space-x-2 px-4 py-2 font-semibold text-sm rounded-full transition-colors duration-300 ${
                        activeTab === tab.id ? 'text-brand-dark' : 'text-white/80 hover:text-white'
                    }`}
                >
                    {/* This is the animated sliding element */}
                    {activeTab === tab.id && (
                        <motion.div
                            layoutId="activeTab"
                            className="absolute inset-0 bg-white dark:bg-slate-300 rounded-full"
                            transition={{ type: 'spring', stiffness: 300, damping: 30 }}
                        />
                    )}
                    <div className="relative z-10">{tab.icon}</div>
                    <span className="relative z-10">{tab.name}</span>
                </button>
            ))}
        </div>
    );
};

export default VehicleTypeToggle;
