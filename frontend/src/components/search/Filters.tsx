import React from 'react';
import VehicleClassFilter from './VehicleClassFilter';

const Filters = ({ vehicleType }) => {
    const companies = ['Magic Carpet Air', 'Sultan Express', 'Genie Flights'];
    const ageCategories = ['Adult', 'Child', 'Infant'];

    return (
        <div className="bg-white/80 dark:bg-slate-950/50 p-6 rounded-xl shadow-lg backdrop-blur-md">
            <h3 className="text-xl font-bold mb-6 text-slate-900 dark:text-white">Filters</h3>

            <VehicleClassFilter vehicleType={vehicleType} />

            <div className="mb-6">
                <h4 className="font-semibold mb-3 text-slate-800 dark:text-slate-200">Company</h4>
                <div className="space-y-2">
                    {companies.map((companyName) => (
                         <label key={companyName} className="flex items-center text-slate-600 dark:text-slate-300">
                            <input type="checkbox" className="h-4 w-4 rounded border-gray-300 text-yellow-500 focus:ring-yellow-500/50" />
                            <span className="ml-2">{companyName}</span>
                        </label>
                    ))}
                </div>
            </div>

            <div className="mb-6">
                <h4 className="font-semibold mb-3 text-slate-800 dark:text-slate-200">Price</h4>
                <div className="space-y-3">
                    <div className="flex items-center space-x-2">
                        <input
                            type="number"
                            placeholder="Min"
                            className="w-full bg-gray-100 dark:bg-slate-800 border-gray-300 dark:border-slate-700 rounded-md p-2 focus:ring-yellow-500/50 focus:border-yellow-500"
                        />
                        <span className="text-gray-500">-</span>
                        <input
                            type="number"
                            placeholder="Max"
                            className="w-full bg-gray-100 dark:bg-slate-800 border-gray-300 dark:border-slate-700 rounded-md p-2 focus:ring-yellow-500/50 focus:border-yellow-500"
                        />
                    </div>
                    <select className="w-full bg-gray-100 dark:bg-slate-800 border-gray-300 dark:border-slate-700 rounded-md p-2 focus:ring-yellow-500/50 focus:border-yellow-500">
                        {ageCategories.map((age) => (
                            <option key={age}>{age}</option>
                        ))}
                    </select>
                </div>
            </div>
        </div>
    );
};

export default Filters;