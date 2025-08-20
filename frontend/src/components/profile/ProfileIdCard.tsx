import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { User, Mail, Phone, MapPin, Edit3, Save } from 'lucide-react';
import { ShinyButton, Spinner } from '../auth/common'; 
import { updateUserInfo } from '../../services/api/apiService'; 
import type { UserUpdateData } from '../../services/api/apiService';
import { useAuth } from '../../context/AuthContext';

const ProfilePicture = ({ user, size = 'w-24 h-24' }) => {
    const [imgError, setImgError] = useState(false);

    const initials = `${user.firstName?.[0] || ''}${user.lastName?.[0] || ''}`.toUpperCase();

    return (
        <div className={`rounded-full flex items-center justify-center bg-passport-gold/20 text-passport-gold font-bold text-3xl ${size}`}>
            {user.profilePictureUrl && !imgError ? (
                <img
                    src={user.profilePictureUrl}
                    alt={`${user.firstName} ${user.lastName}`}
                    className="w-full h-full object-cover rounded-full"
                    onError={() => setImgError(true)}
                />
            ) : (
                <span>{initials}</span>
            )}
        </div>
    );
};

export default function ProfileIdCard({ user }) {
    const [isFlipped, setIsFlipped] = useState(false);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');
    const { updateUser } = useAuth();

    const [formData, setFormData] = useState<Partial<UserUpdateData>>({
        firstName: user.firstName,
        lastName: user.lastName,
        city: user.city,
        // email and phone are not editable in this version, but can be added
    });

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleUpdate = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setError('');
        try {
            const changes = Object.keys(formData).reduce((acc, key) => {
                if (formData[key] !== user[key]) {
                    acc[key] = formData[key];
                }
                return acc;
            }, {});

            if (Object.keys(changes).length > 0) {
                const response = await updateUserInfo(changes);
                updateUser(response.data); 
            }
            setIsFlipped(false);
        } catch (err) {
            setError(err.response?.data?.message || 'Update failed.');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="[perspective:1000px]">
            <motion.div
                className="relative w-full h-[300px]"
                animate={{ rotateY: isFlipped ? 180 : 0 }}
                transition={{ duration: 0.6 }}
                style={{ transformStyle: 'preserve-3d' }}
            >
                {/* Front of the Card */}
                <div className="absolute w-full h-full bg-white/80 dark:bg-slate-900/50 backdrop-blur-lg rounded-2xl shadow-xl p-8 flex items-center" style={{ backfaceVisibility: 'hidden' }}>
                    <div className="w-1/3 flex flex-col items-center">
                        <ProfilePicture user={user} size="w-28 h-28" />
                        <h2 className="mt-4 text-2xl font-bold text-stone-800 dark:text-white">{user.firstName} {user.lastName}</h2>
                        <p className="text-sm text-stone-500 dark:text-stone-400">Member since {new Date(user.signUpDate).toLocaleDateString('en-US', { year: 'numeric', month: 'long' })}</p>
                    </div>
                    <div className="w-2/3 pl-8 grid grid-cols-2 gap-x-6 gap-y-4">
                        <InfoField icon={<User />} label="Full Name" value={`${user.firstName} ${user.lastName}`} />
                        <InfoField icon={<MapPin />} label="City" value={user.city} />
                        <InfoField icon={<Mail />} label="Email" value={user.email || 'Not provided'} />
                        <InfoField icon={<Phone />} label="Phone" value={user.phoneNumber || 'Not provided'} />
                    </div>
                    <button onClick={() => setIsFlipped(true)} className="absolute top-6 right-6 p-2 text-stone-500 hover:text-passport-gold transition-colors">
                        <Edit3 />
                    </button>
                </div>

                {/* Back of the Card */}
                <div className="absolute w-full h-full bg-white/80 dark:bg-slate-900/50 backdrop-blur-lg rounded-2xl shadow-xl p-8" style={{ backfaceVisibility: 'hidden', transform: 'rotateY(180deg)' }}>
                    <form onSubmit={handleUpdate} className="flex flex-col h-full">
                        <h3 className="text-xl font-bold text-stone-800 dark:text-white mb-4">Edit Your Information</h3>
                        <div className="flex-grow grid grid-cols-2 gap-4">
                            <EditField name="firstName" label="First Name" defaultValue={formData.firstName} onChange={handleInputChange} />
                            <EditField name="lastName" label="Last Name" defaultValue={formData.lastName} onChange={handleInputChange} />
                            <EditField name="city" label="City" defaultValue={formData.city} onChange={handleInputChange} />
                            {/* In this version, email/phone are not editable, but can be added back */}
                            <EditField name="birthdayDate" label="Birthday" defaultValue={formData.birthdayDate} onChange={handleInputChange} type="date"/>
                        </div>
                        <div className="flex items-center justify-end space-x-4 mt-4">
                            {error && <p className="text-xs text-red-500">{error}</p>}
                             <button type="button" onClick={() => setIsFlipped(false)} className="text-sm font-semibold text-stone-600 dark:text-stone-300">Cancel</button>
                            <ShinyButton className="w-auto px-6" disabled={isLoading}>{isLoading ? <Spinner/> : <><Save size={16}/><span>Save</span></>}</ShinyButton>
                        </div>
                    </form>
                </div>
            </motion.div>
        </div>
    );
}

const InfoField = ({ icon, label, value }) => (
    <div>
        <div className="flex items-center text-sm font-semibold text-stone-500 dark:text-stone-400">
            {React.cloneElement(icon, { size: 16, className: 'mr-2' })}
            {label}
        </div>
        <p className="text-stone-800 dark:text-white font-medium">{value}</p>
    </div>
);

const EditField = ({ name, label, ...props }) => (
    <div>
        <label htmlFor={name} className="block text-xs font-medium text-stone-600 dark:text-stone-400 mb-1">{label}</label>
        <input name={name} id={name} className="input-field py-2" {...props} />
    </div>
);