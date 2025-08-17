import { motion } from 'framer-motion';
import { useLocation } from 'react-router-dom';
import { ChevronUp, ChevronDown } from 'lucide-react';
import PersistentSearchBar from '../components/search/PersistentSearchBar';
import Filters from '../components/search/Filters';
import TicketCard from '../components/search/TicketCard';
import backgroundLightImage from '../assets/images/day.jpg';
import backgroundDarkImage from '../assets/images/night.jpg';
import NavigateButton from '../components/common/NavigateButton'

const TicketSearchResultPage = () => {
    const query = { from: 'AGR', to: 'DXB' };

    const mockResults = [
        { id: 1, type: 'flight', company: 'Magic Carpet Air', logo: '✈️', from: 'AGR', to: 'DXB', departure: '08:00', arrival: '12:30', duration: '4h 30m', class: 'Economy', price: 350, seatsLeft: 5, stops: 0 },
        { id: 2, type: 'flight', company: 'Sultan Express', logo: '✈️', from: 'AGR', to: 'DXB', departure: '10:15', arrival: '16:00', duration: '5h 45m', class: 'Business', price: 680, seatsLeft: 2, stops: 1 },
    ];

    const listVariants = { hidden: { opacity: 0 }, visible: { opacity: 1, transition: { staggerChildren: 0.1 } } };

    return (
        <div className="relative min-h-screen font-sans">

            <div className="absolute inset-0 bg-cover bg-[center_top_35%] opacity-30 -z-10 block dark:hidden"
            style={{ backgroundImage: `url(${backgroundLightImage})` }}
           >
            </div>

            <div className="absolute inset-0 bg-cover bg-[center_top_10%] opacity-30 -z-10 hidden dark:block"
            style={{ backgroundImage: `url(${backgroundDarkImage})` }}
            >
            </div>

            <div className="relative z-10 pt-24 pb-16">
                <div className="container mx-auto px-4 sm:px-6 lg:px-8">
                    <PersistentSearchBar query={query} />
                    <div className="grid grid-cols-1 lg:grid-cols-4 gap-8">
                        <Filters />
                        <div className="lg:col-span-3">
                            <div className="flex justify-center mb-4">
                                <NavigateButton onClick={() => alert('Loading earlier results...')}>
                                    <ChevronUp size={16} />
                                    <span>Earlier</span>
                                </NavigateButton>
                            </div>

                            <motion.div className="space-y-4" variants={listVariants} initial="hidden" animate="visible">
                                {mockResults.map((ticket) => (
                                    <TicketCard key={ticket.id} ticket={ticket} />
                                ))}
                            </motion.div>

                            <div className="flex justify-center mt-4">
                                <NavigateButton onClick={() => alert('Loading later results...')}>
                                    <ChevronDown size={16} />
                                    <span>Later</span>
                                </NavigateButton>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default TicketSearchResultPage;
