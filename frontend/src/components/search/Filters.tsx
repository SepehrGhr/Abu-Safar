import React from 'react';

const Filters = () => {
    const companies = ['Magic Carpet Air', 'Sultan Express', 'Genie Flights'];
    const classes = ['Economy', 'Business', 'First Class'];
    const ageCategories = ['Adult', 'Child', 'Infant'];

    return (
        <div className="bg-white/80 dark:bg-slate-950/50 p-6 rounded-xl shadow-lg backdrop-blur-md">
            <h3 className="text-xl font-bold mb-6 text-slate-900 dark:text-white">Filters</h3>

            {/* Class Filter */}
            <div className="mb-6">
                <h4 className="font-semibold mb-3 text-slate-800 dark:text-slate-200">Class</h4>
                <div className="space-y-2">
                    {classes.map((className) => (
                        <label key={className} className="flex items-center text-slate-600 dark:text-slate-300">
                            <input type="checkbox" className="h-4 w-4 rounded border-gray-300 text-brand-primary focus:ring-brand-primary/50" />
                            <span className="ml-2">{className}</span>
                        </label>
                    ))}
                </div>
            </div>

            {/* Company Filter */}
            <div className="mb-6">
                <h4 className="font-semibold mb-3 text-slate-800 dark:text-slate-200">Company</h4>
                <div className="space-y-2">
                    {companies.map((companyName) => (
                         <label key={companyName} className="flex items-center text-slate-600 dark:text-slate-300">
                            <input type="checkbox" className="h-4 w-4 rounded border-gray-300 text-brand-primary focus:ring-brand-primary/50" />
                            <span className="ml-2">{companyName}</span>
                        </label>
                    ))}
                </div>
            </div>

            {/* Price Filter with Age Dropdown */}
            <div className="mb-6">
                <h4 className="font-semibold mb-3 text-slate-800 dark:text-slate-200">Price</h4>
                <div className="space-y-3">
                    <div className="flex items-center space-x-2">
                        <input
                            type="number"
                            placeholder="Min"
                            className="w-full bg-gray-100 dark:bg-slate-800 border-gray-300 dark:border-slate-700 rounded-md p-2 focus:ring-brand-primary/50 focus:border-brand-primary"
                        />
                        <span className="text-gray-500">-</span>
                        <input
                            type="number"
                            placeholder="Max"
                            className="w-full bg-gray-100 dark:bg-slate-800 border-gray-300 dark:border-slate-700 rounded-md p-2 focus:ring-brand-primary/50 focus:border-brand-primary"
                        />
                    </div>
                    <select className="w-full bg-gray-100 dark:bg-slate-800 border-gray-300 dark:border-slate-700 rounded-md p-2 focus:ring-brand-primary/50 focus:border-brand-primary">
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