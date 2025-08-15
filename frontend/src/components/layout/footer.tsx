import { Twitter, Facebook, Instagram } from 'lucide-react';
import Logo from '../../assets/logo.svg';

const Footer = () => {
    return (
        <footer className="bg-transparent text-gray-300 relative dark:bg-[#8c7646]/20 backdrop-blur-sm">
            <div className="container mx-auto py-16 px-4 sm:px-6 lg:px-8">
                <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-5 gap-8">
                    <div className="col-span-2 md:col-span-4 lg:col-span-1">
                        <a href="#" className="flex items-center space-x-3">
                            <img src={Logo} alt="AbuSafar Logo" className="h-8 w-8" />
                            <span className="font-kameron font-bold text-2xl text-white">AbuSafar</span>
                        </a>
                        <p className="mt-4 text-sm text-gray-400">Your journey starts here. Book with confidence.</p>
                        <div className="flex space-x-4 mt-6">
                            <a href="#" className="text-gray-400 hover:text-white"><Twitter/></a>
                            <a href="#" className="text-gray-400 hover:text-white"><Facebook/></a>
                            <a href="#" className="text-gray-400 hover:text-white"><Instagram/></a>
                        </div>
                    </div>
                    {/* Footer Links Here */}
                </div>
                <div className="mt-12 border-t border-slate-700/50 pt-8 text-center text-sm text-gray-400">
                    <p>&copy; {new Date().getFullYear()} AbuSafar Technologies Inc. All rights reserved.</p>
                </div>
            </div>
        </footer>
    );
};

export default Footer;