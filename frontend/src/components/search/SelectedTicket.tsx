import React from 'react';
import { motion } from 'framer-motion';
import { RotateCcw } from 'lucide-react';
import type { Ticket } from '/src/services/api/types';

interface SelectedTicketProps {
  ticket: Ticket;
  onDeselect: () => void;
  isOverlapped: boolean;
}

const SelectedTicket: React.FC<SelectedTicketProps> = ({ ticket, onDeselect, isOverlapped }) => {
  return (
    <motion.div
      layoutId={`ticket-${ticket.tripId}`}
      className={`relative w-[80px] h-[60px] cursor-pointer carpet-pattern rounded-xl ${isOverlapped ? '-ml-10' : ''}`}
      onClick={onDeselect}
      whileHover={{ scale: 1.1, y: -5, zIndex: 20 }}
      exit={{ opacity: 0, scale: 0.5 }}
      initial={{ opacity: 0, scale: 0.5 }}
      animate={{ opacity: 1, scale: 1, rotate: 90 }}
      transition={{ type: 'spring', stiffness: 300, damping: 30 }}
    >
      <motion.div
        className="deselect-overlay"
        initial={{ opacity: 0 }}
        whileHover={{ opacity: 1 }}
      >
        <RotateCcw className="w-6 h-6 text-white" />
      </motion.div>
    </motion.div>
  );
};

export default SelectedTicket;
