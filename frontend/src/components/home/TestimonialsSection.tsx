import React from 'react';
import { Star } from 'lucide-react';

const testimonials = [
    {
        quote: "Booking with AbuSafar felt like a magic carpet ride. They showed me a whole new world of travel, effortlessly.",
        author: "Aisha R.",
        location: "Marrakech, Morocco"
    },
    {
        quote: "I tried to build my own travel site once. After weeks of work, I gave up and used AbuSafar. It was so much easier. I wish I had just started here.",
        author: "Hedie R.",
        location: "Saveh, Iran"
    },
    {
        quote: "Finding such amazing deals felt like discovering a treasure in a Cave of Wonders. My family's travel wishes were granted!",
        author: "Fatima S.",
        location: "Cairo, Egypt"
    }
];

export default function TestimonialsSection() {
    return (
        <div className="py-24 bg-white dark:bg-brand-night">
            <div className="container mx-auto px-4 sm:px-6 lg:px-8">
                <h2 className="text-4xl font-aladin text-center text-stone-800 dark:text-white mb-16">Tales from the Bazaar</h2>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
                    {testimonials.map((testimonial, index) => (
                        <div key={index} className="bg-brand-sandLight dark:bg-slate-900 p-8 rounded-lg shadow-lg relative transform hover:-translate-y-2 transition-transform duration-300">
                            <div className="absolute top-0 left-8 transform -translate-y-1/2 text-7xl text-passport-gold/30 font-serif">“</div>
                            <div className="flex mb-4">
                                {[...Array(5)].map((_, i) => <Star key={i} className="text-passport-gold" fill="currentColor" />)}
                            </div>
                            <p className="text-stone-600 dark:text-stone-300 italic mb-6">{testimonial.quote}</p>
                            <div className="text-right">
                                <p className="font-bold text-stone-700 dark:text-stone-200">{testimonial.author}</p>
                                <p className="text-sm text-stone-500 dark:text-stone-400">{testimonial.location}</p>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}