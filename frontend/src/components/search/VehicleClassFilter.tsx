import React from 'react';
import { useSearchParams } from 'react-router-dom';

interface VehicleClassFilterProps {
  vehicleType: string;
}

const VehicleClassFilter: React.FC<VehicleClassFilterProps> = ({ vehicleType }) => {
    const [searchParams, setSearchParams] = useSearchParams();

    const filterOptions = {
        FLIGHT: { title: 'Flight Class', param: 'flightClass', options: ['ECONOMY_CLASS', 'BUSINESS_CLASS', 'FIRST_CLASS'], type: 'checkbox' },
        BUS: { title: 'Bus Class', param: 'busClass', options: ['VIP', 'STANDARD', 'SLEEPER'], type: 'checkbox' },
        TRAIN: { title: 'Minimum Train Stars', param: 'trainStars', options: [5, 4, 3, 2, 1], type: 'radio' }
    };

    const currentFilter = filterOptions[vehicleType?.toUpperCase()] || filterOptions.FLIGHT;

    const selectedValues = new Set(searchParams.get(currentFilter.param)?.split(',') || []);

    const handleChange = (optionValue: string) => {
        const newSelectedValues = new Set(selectedValues);
        const newSearchParams = new URLSearchParams(searchParams);

        if (currentFilter.type === 'checkbox') {
            if (newSelectedValues.has(optionValue)) {
                newSelectedValues.delete(optionValue);
            } else {
                newSelectedValues.add(optionValue);
            }
        } else {
            if (newSelectedValues.has(optionValue)) {
                newSelectedValues.delete(optionValue);
            } else {
                newSelectedValues.clear();
                newSelectedValues.add(optionValue);
            }
        }

        // Update the URL parameter
        if (newSelectedValues.size > 0) {
            newSearchParams.set(currentFilter.param, Array.from(newSelectedValues).join(','));
        } else {
            newSearchParams.delete(currentFilter.param); // Clean up URL if nothing is selected
        }

        setSearchParams(newSearchParams);
    };

    return (
        <div className="mb-6">
            <h4 className="font-semibold mb-3 text-slate-800 dark:text-slate-200">{currentFilter.title}</h4>

            {/* Checkbox UI for Flight and Bus */}
            {(currentFilter.type === 'checkbox') && (
                <div className="space-y-2">
                    {currentFilter.options.map((option) => (
                        <label key={option} className="flex items-center text-slate-600 dark:text-slate-300">
                            <input
                                type="checkbox"
                                className="h-4 w-4 rounded border-gray-300 text-yellow-500 focus:ring-yellow-500/50"
                                checked={selectedValues.has(String(option))}
                                onChange={() => handleChange(String(option))}
                            />
                            <span className="ml-2">{String(option).replace('_', ' ')}</span>
                        </label>
                    ))}
                </div>
            )}

            {/* Radio Button UI for Train */}
            {currentFilter.type === 'radio' && (
                 <div className="space-y-2">
                    {currentFilter.options.map((option) => (
                         <label key={option} className="flex items-center text-slate-600 dark:text-slate-300">
                            <input
                                type="radio"
                                name="train-stars"
                                className="h-4 w-4 border-gray-300 text-yellow-500 focus:ring-yellow-500/50"
                                checked={selectedValues.has(String(option))}
                                onChange={() => handleChange(String(option))}
                            />
                            <span className="ml-2 text-yellow-400">{'⭐'.repeat(option)}</span>
                        </label>
                    ))}
                </div>
            )}
        </div>
    );
};

export default VehicleClassFilter;