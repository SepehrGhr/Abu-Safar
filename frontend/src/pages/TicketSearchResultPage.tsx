import { motion } from 'framer-motion';
import { useLocation } from 'react-router-dom';
import { ChevronUp, ChevronDown } from 'lucide-react';
import PersistentSearchBar from '../components/search/PersistentSearchBar';
import Filters from '../components/search/Filters';
import TicketCard from '../components/search/TicketCard';

const TicketSearchResultPage = () => {
    const location = useLocation();
    const { query } = location.state || { query: { from: 'N/A', to: 'N/A' } };

    const mockResults = [
        { id: 1, type: 'flight', company: 'Magic Carpet Air', logo: '✈️', from: 'AGR', to: 'DXB', departure: '08:00', arrival: '12:30', duration: '4h 30m', class: 'Economy', price: 350, seatsLeft: 5, stops: 0 },
        { id: 2, type: 'flight', company: 'Sultan Express', logo: '✈️', from: 'AGR', to: 'DXB', departure: '10:15', arrival: '16:00', duration: '5h 45m', class: 'Business', price: 680, seatsLeft: 2, stops: 1 },
    ];

    const listVariants = { hidden: { opacity: 0 }, visible: { opacity: 1, transition: { staggerChildren: 0.1 } } };

    return (
        <div className="pt-24 pb-16">
            <div className="container mx-auto px-4 sm:px-6 lg:px-8">
                <PersistentSearchBar query={query} />
                <div className="grid grid-cols-1 lg:grid-cols-4 gap-8">
                    <Filters />
                    <div className="lg:col-span-3">
                        <div className="flex justify-center mb-4"><button className="flex items-center space-x-1 text-sm font-semibold text-gray-600 dark:text-gray-300 hover:text-[#ebab5e] dark:hover:text-[#ebab5e]"><ChevronUp size={16} /><span>Earlier</span></button></div>
                        <motion.div className="space-y-4" variants={listVariants} initial="hidden" animate="visible">
                            {mockResults.map((ticket) => (
                                <TicketCard key={ticket.id} ticket={ticket} />
                            ))}
                        </motion.div>
                        <div className="flex justify-center mt-4"><button className="flex items-center space-x-1 text-sm font-semibold text-gray-600 dark:text-gray-300 hover:text-[#ebab5e] dark:hover:text-[#ebab5e]"><ChevronDown size={16} /><span>Later</span></button></div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default TicketSearchResultPage;