import React, { useState } from 'react';
import { Calendar } from 'lucide-react';

interface CustomDateInputProps {
    disabled?: boolean;
    placeholder: string;
    id: string;
}

const CustomDateInput: React.FC<CustomDateInputProps> = ({ disabled = false, placeholder, id }) => {
    const [dateValue, setDateValue] = useState('');
    const [isFocused, setIsFocused] = useState(false);

    const handleDateChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setDateValue(e.target.value);
    }

    const formattedDate = dateValue ? new Date(dateValue + 'T00:00:00').toLocaleDateString('en-US', { day: 'numeric', month: 'short', weekday: 'short' }) : '';

    return (
        <label htmlFor={id} className="relative cursor-pointer">
            <Calendar className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-white/50 pointer-events-none" />
            <div className={`w-full bg-white/10 backdrop-blur-md text-white border-2 rounded-xl py-3 pl-12 pr-4 transition ${isFocused ? 'border-orange-400' : 'border-transparent'}`}>
                {formattedDate ? formattedDate : <span className="text-white/50">{placeholder}</span>}
            </div>
            <input
                id={id}
                type="date"
                onChange={handleDateChange}
                onFocus={() => setIsFocused(true)}
                onBlur={() => setIsFocused(false)}
                disabled={disabled}
                className="absolute inset-0 opacity-0 w-full h-full cursor-pointer"
            />
        </label>
    );
}

export default CustomDateInput;