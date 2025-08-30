import React, { useState, useEffect, useCallback } from 'react';
import type { Location } from '../../services/api/types';
import { searchCitiesByName } from '../../services/api/locations';
import { MapPin } from 'lucide-react';

interface LocationSearchBarProps {
  // The selected location object from the parent (null if nothing is selected)
  value: Location | null;
  // The current text inside the input box, controlled by the parent
  query: string;
  // Callback for when the user types in the input
  onQueryChange: (query: string) => void;
  // Callback for when a user selects a location from the dropdown
  onLocationSelect: (location: Location) => void;
  placeholder?: string;
  className?: string;
}

const LocationSearchBar: React.FC<LocationSearchBarProps> = ({
  value,
  query,
  onQueryChange,
  onLocationSelect,
  placeholder = "City or airport",
  className = "w-full p-2 border border-gray-300 rounded-md"
}) => {
  const [results, setResults] = useState<Location[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [showResults, setShowResults] = useState(false);

  // Debounced search function to avoid excessive API calls
  const debouncedSearch = useCallback(
    debounce(async (searchQuery: string) => {
      // Don't search if a valid location is already selected and the query matches its city
      if (value && value.city === searchQuery) {
        setResults([]);
        return;
      }
      if (searchQuery.length > 1) {
        setIsLoading(true);
        try {
          const data = await searchCitiesByName(searchQuery);
          setResults(data);
        } catch (error) {
          console.error("Failed to fetch locations:", error);
          setResults([]);
        } finally {
          setIsLoading(false);
        }
      } else {
        setResults([]);
      }
    }, 300), // 300ms delay
    [value] // Reruns if the selected value changes
  );

  useEffect(() => {
    debouncedSearch(query);
  }, [query, debouncedSearch]);

  const handleSelect = (location: Location) => {
    onLocationSelect(location);
    setShowResults(false);
    setResults([]);
  };

  return (
    <div className="relative w-full">
      <input
        type="text"
        // The input's value is the query string from the parent, allowing typing
        value={query}
        onChange={(e) => onQueryChange(e.target.value)}
        onFocus={() => setShowResults(true)}
        // Delay onBlur to allow click events on the dropdown to register
        onBlur={() => setTimeout(() => setShowResults(false), 200)}
        placeholder={placeholder}
        className={className}
        autoComplete="off"
      />
      {/* Show dropdown only when focused and there is a query */}
      {showResults && query.length > 1 && (
        <div className="absolute z-10 w-full mt-1 bg-white border border-gray-300 rounded-md shadow-lg max-h-60 overflow-y-auto">
          {isLoading && <div className="p-2 text-gray-500">Searching...</div>}
          {!isLoading && results.length === 0 && query.length > 1 && (
            <div className="p-2 text-gray-500">No results found.</div>
          )}
          {!isLoading && results.length > 0 && (
            <ul>
              {results.map((location) => (
                <li
                  key={location.id}
                  className="p-3 hover:bg-gray-100 cursor-pointer text-gray-800"
                  // Use onMouseDown to ensure the click registers before the input's onBlur event fires
                  onMouseDown={() => handleSelect(location)}
                >
                  <div className="flex items-center">
                    <MapPin className="w-5 h-5 mr-3 text-gray-400" />
                    <div>
                      <div className="font-medium">{location.city}</div>
                      <div className="text-sm font-semibold text-gray-500">
                        {location.province}, {location.country}
                      </div>
                    </div>
                  </div>
                </li>
              ))}
            </ul>
          )}
        </div>
      )}
    </div>
  );
};

// Simple debounce utility function to limit how often the API is called
function debounce<F extends (...args: any[]) => any>(func: F, delay: number) {
  let timeoutId: ReturnType<typeof setTimeout>;
  return function(this: any, ...args: Parameters<F>) {
    clearTimeout(timeoutId);
    timeoutId = setTimeout(() => func.apply(this, args), delay);
  };
}

export default LocationSearchBar;
