import React, { useEffect } from 'react';
import DigitalPassport from '../components/auth/DigitalPassport';

// Helper to inject the Google Fonts link into the document head
const GoogleFontInjector = () => {
    useEffect(() => {
        const link = document.createElement('link');
        link.href = "https://fonts.googleapis.com/css2?family=Merriweather:wght@400;700&family=Inter:wght@400;500;700&display=swap";
        link.rel = 'stylesheet';
        document.head.appendChild(link);
        return () => {
            document.head.removeChild(link);
        };
    }, []);
    return null;
};

export default function AuthPage() {
    return (
        <>
            <GoogleFontInjector />
            <div
                className="min-h-screen flex items-center justify-center p-4 relative overflow-hidden bg-brand-sandLight dark:bg-brand-night transition-colors duration-300"
                style={{ fontFamily: "'Inter', sans-serif" }}
            >
                <div className="absolute inset-0 bg-white/30 dark:bg-black/50 [mask-image:radial-gradient(ellipse_at_center,transparent_20%,black)]"></div>
                <DigitalPassport />
            </div>
        </>
    );
}