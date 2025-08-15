import React from 'react';

const Filters = () => {
    return (
        <div className="bg-white/80 dark:bg-slate-950/50 p-6 rounded-xl shadow-lg backdrop-blur-md">
            <h3 className="text-xl font-bold mb-4">Filters</h3>

            <div className="mb-6">
                <h4 className="font-semibold mb-2">Class</h4>
                <div className="space-y-2">
                    <label className="flex items-center"><input type="checkbox" className="h-4 w-4 rounded border-gray-300 text-[#ebab5e] focus:ring-[#ebab5e]/50" /> <span className="ml-2">Economy</span></label>
                    <label className="flex items-center"><input type="checkbox" className="h-4 w-4 rounded border-gray-300 text-[#ebab5e] focus:ring-[#ebab5e]/50" /> <span className="ml-2">Business</span></label>
                    <label className="flex items-center"><input type="checkbox" className="h-4 w-4 rounded border-gray-300 text-[#ebab5e] focus:ring-[#ebab5e]/50" /> <span className="ml-2">First Class</span></label>
                </div>
            </div>

            <div className="mb-6">
                <h4 className="font-semibold mb-2">Price</h4>
                <input type="range" min="0" max="1000" className="w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer dark:bg-gray-700" />
            </div>
        </div>
    );
};

export default Filters;