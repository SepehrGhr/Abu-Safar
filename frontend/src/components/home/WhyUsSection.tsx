import React from 'react';
import { Sparkles, KeyRound, Compass } from 'lucide-react';

const features = [
    {
        icon: <Sparkles size={32} />,
        title: "24/7 Genie Support",
        description: "Our dedicated support team is always here to grant your travel wishes, anytime, anywhere."
    },
    {
        icon: <KeyRound size={32} />,
        title: "Exclusive Access",
        description: "Unlock curated travel experiences and premium deals you won't find anywhere else."
    },
    {
        icon: <Compass size={32} />,
        title: "Seamless Navigation",
        description: "From the moment you book to your final arrival, we guide you every step of your journey."
    }
];

export default function WhyUsSection() {
    return (
        <div className="py-24 bg-brand-sandLight dark:bg-slate-900">
            <div className="container mx-auto px-4 sm:px-6 lg:px-8">
                <h2 className="text-4xl font-aladin text-center text-stone-800 dark:text-white mb-16">Why Travel With Us?</h2>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-12 text-center">
                    {features.map((feature, index) => (
                        <div key={index} className="flex flex-col items-center">
                            <div className="p-4 bg-passport-gold/20 text-passport-gold rounded-full mb-6">
                                {feature.icon}
                            </div>
                            <h3 className="text-2xl font-kameron font-bold text-stone-700 dark:text-stone-200 mb-4">{feature.title}</h3>
                            <p className="text-stone-500 dark:text-stone-400">{feature.description}</p>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}