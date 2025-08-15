import React, { useState } from 'react';
import { MapPin, ChevronLeft, ChevronRight } from 'lucide-react';

const PersistentSearchBar = ({ query }) => {
    const [date, setDate] = useState(new Date(query.departureDate || Date.now()));

    const changeDate = (days) => {
        setDate(prevDate => {
            const newDate = new Date(prevDate);
            newDate.setDate(newDate.getDate() + days);
            return newDate;
        });
    };

    return (
        <div className="bg-white/80 dark:bg-slate-950/50 p-4 rounded-xl shadow-lg backdrop-blur-md mb-8">
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 items-center">
                <div className="relative"><MapPin className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-400" /><input type="text" defaultValue={query.from} className="w-full bg-gray-100 dark:bg-slate-800 border-gray-300 dark:border-slate-700 rounded-md p-2 pl-10 focus:ring-[#ebab5e]/50 focus:border-[#ebab5e]" /></div>
                <div className="relative"><MapPin className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-400" /><input type="text" defaultValue={query.to} className="w-full bg-gray-100 dark:bg-slate-800 border-gray-300 dark:border-slate-700 rounded-md p-2 pl-10 focus:ring-[#ebab5e]/50 focus:border-[#ebab5e]" /></div>
                <div className="flex items-center justify-center space-x-2">
                    <button onClick={() => changeDate(-1)} className="p-2 rounded-md hover:bg-gray-200 dark:hover:bg-slate-700"><ChevronLeft size={20} /></button>
                    <div className="text-center">
                        <span className="font-semibold whitespace-nowrap">{date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })}</span>
                        <p className="text-xs text-gray-500 dark:text-gray-400">{date.toLocaleDateString('en-US', { weekday: 'long' })}</p>
                    </div>
                    <button onClick={() => changeDate(1)} className="p-2 rounded-md hover:bg-gray-200 dark:hover:bg-slate-700"><ChevronRight size={20} /></button>
                </div>
            </div>
        </div>
    );
};

export default PersistentSearchBar;