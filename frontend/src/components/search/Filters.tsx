import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import VehicleClassFilter from './VehicleClassFilter';
import CompanyFilter from './CompanyFilter';

interface FiltersProps {
  vehicleType: string;
}

const Filters: React.FC<FiltersProps> = ({ vehicleType }) => {
    const [searchParams, setSearchParams] = useSearchParams();
    const ageCategories = ['ADULT', 'CHILD', 'BABY'];

    // State for price inputs
    const [minPrice, setMinPrice] = useState(searchParams.get('minPrice') || '');
    const [maxPrice, setMaxPrice] = useState(searchParams.get('maxPrice') || '');

    // State for age select
    const selectedAge = searchParams.get('age') || 'ADULT';

    const handleAgeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        const newSearchParams = new URLSearchParams(searchParams);
        newSearchParams.set('age', e.target.value);
        setSearchParams(newSearchParams);
    };

    // Debounce effect for price changes
    useEffect(() => {
        const handler = setTimeout(() => {
            const newSearchParams = new URLSearchParams(searchParams);
            if (minPrice) {
                newSearchParams.set('minPrice', minPrice);
            } else {
                newSearchParams.delete('minPrice');
            }
            if (maxPrice) {
                newSearchParams.set('maxPrice', maxPrice);
            } else {
                newSearchParams.delete('maxPrice');
            }
            // Use functional update to avoid stale state issues if needed,
            // or just set directly as we are reading from searchParams which is stable.
            setSearchParams(newSearchParams);
        }, 700); // 700ms delay

        // This is the cleanup function that runs before the next effect or on unmount
        return () => {
            clearTimeout(handler);
        };
    }, [minPrice, maxPrice, searchParams, setSearchParams]); // Dependency array

    // The main JSX for the component must be returned here, at the top level.
    return (
        <div className="bg-white/80 dark:bg-slate-950/50 p-6 rounded-xl shadow-lg backdrop-blur-md">
            <h3 className="text-xl font-bold mb-6 text-slate-900 dark:text-white">Filters</h3>

            <VehicleClassFilter vehicleType={vehicleType} />
            <CompanyFilter vehicleType={vehicleType} />

            <div className="mb-6">
                <h4 className="font-semibold mb-3 text-slate-800 dark:text-slate-200">Price</h4>
                <div className="space-y-3">
                    <div className="flex items-center space-x-2">
                        <input
                            type="number"
                            placeholder="Min"
                            value={minPrice}
                            onChange={(e) => setMinPrice(e.target.value)}
                            className="w-full bg-gray-100 dark:bg-slate-800 border-gray-300 dark:border-slate-700 rounded-md p-2 focus:ring-yellow-500/50 focus:border-yellow-500"
                        />
                        <span className="text-gray-500">-</span>
                        <input
                            type="number"
                            placeholder="Max"
                            value={maxPrice}
                            onChange={(e) => setMaxPrice(e.target.value)}
                            className="w-full bg-gray-100 dark:bg-slate-800 border-gray-300 dark:border-slate-700 rounded-md p-2 focus:ring-yellow-500/50 focus:border-yellow-500"
                        />
                    </div>
                </div>
            </div>

            <div className="mb-6">
                 <h4 className="font-semibold mb-3 text-slate-800 dark:text-slate-200">Passenger Age</h4>
                 <select
                    value={selectedAge}
                    onChange={handleAgeChange}
                    className="w-full bg-gray-100 dark:bg-slate-800 border-gray-300 dark:border-slate-700 rounded-md p-2 focus:ring-yellow-500/50 focus:border-yellow-500"
                 >
                    {ageCategories.map((age) => (
                        <option key={age} value={age}>{age}</option>
                    ))}
                </select>
            </div>
        </div>
    );
};

export default Filters;