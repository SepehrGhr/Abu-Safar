import React from 'react';

const VehicleClassFilter = ({ vehicleType }) => {
    const filterOptions = {
        FLIGHT: { title: 'Class', options: ['Economy', 'Business', 'First Class'], type: 'checkbox' },
        BUS: { title: 'Class', options: ['VIP', 'Standard', 'Sleeper'], type: 'checkbox' },
        TRAIN: { title: 'Train Stars', options: [1, 2, 3, 4, 5], type: 'multiselect' }
    };

    const currentFilter = filterOptions[vehicleType] || filterOptions['FLIGHT'];

    return (
        <div className="mb-6">
            <h4 className="font-semibold mb-3 text-slate-800 dark:text-slate-200">{currentFilter.title}</h4>

            {currentFilter.type === 'checkbox' && (
                <div className="space-y-2">
                    {currentFilter.options.map((option) => (
                        <label key={option} className="flex items-center text-slate-600 dark:text-slate-300">
                            <input type="checkbox" className="h-4 w-4 rounded border-gray-300 text-yellow-500 focus:ring-yellow-500/50" />
                            <span className="ml-2">{option}</span>
                        </label>
                    ))}
                </div>
            )}

            {currentFilter.type === 'multiselect' && (
                 <select
                    multiple
                    className="w-full bg-gray-100 dark:bg-slate-800 border-gray-300 dark:border-slate-700 rounded-md p-2 focus:ring-yellow-500/50 focus:border-yellow-500"
                    size={5}
                 >
                    {currentFilter.options.map((option) => (
                        <option key={option} value={option} className="p-2 hover:bg-yellow-500/20">
                            {'⭐'.repeat(option)}
                        </option>
                    ))}
                </select>
            )}
        </div>
    );
};

export default VehicleClassFilter;