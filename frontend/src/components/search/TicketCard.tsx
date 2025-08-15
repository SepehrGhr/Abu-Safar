import { motion } from 'framer-motion';

const TicketCard = ({ ticket }) => {
    const itemVariants = { hidden: { opacity: 0, y: 20 }, visible: { opacity: 1, y: 0 } };
    const stopsText = ticket.stops === 0 ? 'Non-stop' : `${ticket.stops} stop${ticket.stops > 1 ? 's' : ''}`;

    return (
        <motion.div variants={itemVariants} className="bg-white/80 dark:bg-slate-950/50 backdrop-blur-md rounded-xl shadow-lg overflow-hidden transition-shadow hover:shadow-2xl">
            <div className="p-6 flex flex-col sm:flex-row items-center justify-between space-y-4 sm:space-y-0">
                <div className="flex items-center space-x-4">
                    <div className="text-3xl">{ticket.logo}</div>
                    <div>
                        <p className="font-bold">{ticket.company}</p>
                        <p className="text-sm text-gray-600 dark:text-gray-400">{ticket.class}</p>
                    </div>
                </div>
                <div className="flex items-center space-x-8 text-center">
                    <div>
                        <p className="text-xl font-bold">{ticket.departure}</p>
                        <p className="text-sm font-semibold">{ticket.from}</p>
                    </div>
                    <div className="text-sm text-gray-500 dark:text-gray-400">
                        <p>{ticket.duration}</p>
                        <div className="w-24 h-px bg-gray-300 dark:bg-slate-600 my-1"></div>
                        <p>{stopsText}</p>
                    </div>
                    <div>
                        <p className="text-xl font-bold">{ticket.arrival}</p>
                        <p className="text-sm font-semibold">{ticket.to}</p>
                    </div>
                </div>
                <div className="flex flex-col items-center text-center">
                    <p className="text-2xl font-bold text-[#a57c44] dark:text-[#ebab5e]">${ticket.price}</p>
                    <motion.button whileHover={{scale: 1.05}} whileTap={{scale: 0.95}} className="px-6 py-2 mt-2 bg-[#ebab5e] text-white font-bold rounded-lg hover:bg-[#d49e54] transition-colors">Select</motion.button>
                    <p className="text-xs text-gray-500 dark:text-gray-400 mt-2">{ticket.seatsLeft} seats left</p>
                </div>
            </div>
        </motion.div>
    );
};

export default TicketCard;