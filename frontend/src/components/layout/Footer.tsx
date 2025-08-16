import { Twitter, Facebook, Instagram } from 'lucide-react';
import CarpetLogo from '../icons/Carpet';

const Footer = () => {
    const companyLinks = [
        { name: 'About Us', href: '#' },
        { name: 'Careers', href: '#' },
        { name: 'Press', href: '#' },
    ];

    const serviceLinks = [
        { name: 'Flights', href: '#' },
        { name: 'Buses', href: '#' },
        { name: 'Trains', href: '#' },
    ];

    const supportLinks = [
        { name: 'Help Center', href: '#' },
        { name: 'Contact Us', href: '#' },
        { name: 'FAQs', href: '#' },
    ];

    const legalLinks = [
        { name: 'Terms of Service', href: '#' },
        { name: 'Privacy Policy', href: '#' },
    ];


    return (
        <footer className="bg-slate-100/80 text-gray-600 dark:bg-brand-dark/20 dark:text-gray-300 relative backdrop-blur-sm">
            <div className="container mx-auto py-16 px-4 sm:px-6 lg:px-8">
                <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-5 gap-8">
                    {/* Logo and Social Links Section */}
                    <div className="col-span-2 md:col-span-4 lg:col-span-1">
                        <a href="#" className="flex items-center space-x-3 text-slate-950 dark:text-white">
                            <CarpetLogo className="h-8 w-8" />
                            <span className="font-kameron font-bold text-2xl">AbuSafar</span>
                        </a>
                        <p className="mt-4 text-sm text-gray-500 dark:text-gray-400">Your journey starts here. Book with confidence.</p>
                        <div className="flex space-x-4 mt-6">
                            <a href="#" className="text-gray-400 hover:text-white"><Twitter/></a>
                            <a href="#" className="text-gray-400 hover:text-white"><Facebook/></a>
                            <a href="#" className="text-gray-400 hover:text-white"><Instagram/></a>
                        </div>
                    </div>

                    {/* Company Links */}
                    <div>
                        <h3 className="text-sm font-semibold text-slate-800 dark:text-white tracking-wider uppercase">Company</h3>
                        <ul className="mt-4 space-y-3">
                            {companyLinks.map((link) => (
                                <li key={link.name}>
                                    <a href={link.href} className="text-base text-gray-500 dark:text-gray-400 hover:text-brand-primary dark:hover:text-white">{link.name}</a>
                                </li>
                            ))}
                        </ul>
                    </div>

                    {/* Services Links */}
                    <div>
                        <h3 className="text-sm font-semibold text-slate-800 dark:text-white tracking-wider uppercase">Services</h3>
                        <ul className="mt-4 space-y-3">
                            {serviceLinks.map((link) => (
                                <li key={link.name}>
                                    <a href={link.href} className="text-base text-gray-500 dark:text-gray-400 hover:text-brand-primary dark:hover:text-white">{link.name}</a>
                                </li>
                            ))}
                        </ul>
                    </div>

                    {/* Support Links */}
                    <div>
                        <h3 className="text-sm font-semibold text-slate-800 dark:text-white tracking-wider uppercase">Support</h3>
                        <ul className="mt-4 space-y-3">
                            {supportLinks.map((link) => (
                                <li key={link.name}>
                                    <a href={link.href} className="text-base text-gray-500 dark:text-gray-400 hover:text-brand-primary dark:hover:text-white">{link.name}</a>
                                </li>
                            ))}
                        </ul>
                    </div>

                    {/* Legal Links */}
                    <div>
                        <h3 className="text-sm font-semibold text-slate-800 dark:text-white tracking-wider uppercase">Legal</h3>
                        <ul className="mt-4 space-y-3">
                            {legalLinks.map((link) => (
                                <li key={link.name}>
                                    <a href={link.href} className="text-base text-gray-500 dark:text-gray-400 hover:text-brand-primary dark:hover:text-white">{link.name}</a>
                                </li>
                            ))}
                        </ul>
                    </div>
                </div>

                <div className="mt-12 border-t border-slate-300 dark:border-slate-700/50 pt-8 text-center text-sm text-gray-500 dark:text-gray-400">
                    <p>&copy; {new Date().getFullYear()} AbuSafar Technologies Inc. All rights reserved.</p>
                </div>
            </div>
        </footer>
    );
};

export default Footer;