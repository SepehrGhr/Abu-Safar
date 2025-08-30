import React from 'react';
import { Armchair, Wifi, UtensilsCrossed, Star } from 'lucide-react';
import type { TicketDetails, BusDetails, FlightDetails, TrainDetails } from '/src/services/api/types';

interface TicketDetailsViewProps {
  details: TicketDetails;
}

const renderVehicleDetails = (details: TicketDetails) => {
    switch (details.tripVehicle) {
        case 'BUS':
            const bus = details.vehicleDetails as BusDetails;
            return (
                <>
                    {/* Correctly uses bus.classType */}
                    <p className="text-slate-600 dark:text-slate-300">Class: <span className="font-semibold">{bus.classType}</span></p>
                    <p className="text-slate-600 dark:text-slate-300">Chair Type: <span className="font-semibold">{bus.chairType}</span></p>
                </>
            );
        case 'FLIGHT':
            const flight = details.vehicleDetails as FlightDetails;
            return (
                <>
                    {/* Correctly uses flight.classType */}
                    <p className="text-slate-600 dark:text-slate-300">Class: <span className="font-semibold">{flight.classType.replace('_', ' ')}</span></p>
                    <p className="text-slate-600 dark:text-slate-300">Departure Airport: <span className="font-semibold">{flight.departureAirport}</span></p>
                    <p className="text-slate-600 dark:text-slate-300">Arrival Airport: <span className="font-semibold">{flight.arrivalAirport}</span></p>
                </>
            );
        case 'TRAIN':
            const train = details.vehicleDetails as TrainDetails;
            return (
                <>
                    <p className="flex items-center text-slate-600 dark:text-slate-300">
                        {/* Correctly uses train.stars */}
                        Stars: <span className="font-semibold flex items-center ml-1">{train.stars} <Star size={16} className="ml-1 text-yellow-400" /></span>
                    </p>
                    <p className="text-slate-600 dark:text-slate-300">Room Type: <span className="font-semibold">{train.roomType}</span></p>
                </>
            );
        default:
            return <p>No vehicle details available.</p>;
    }
};

const TicketDetailsView: React.FC<TicketDetailsViewProps> = ({ details }) => {
  const seatsLeft = details.totalCapacity - details.reservedCapacity;

  return (
    <div className="bg-slate-100 dark:bg-slate-800/50 p-4 border-t border-slate-200 dark:border-slate-700">
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-sm">
        <div>
          <h5 className="font-bold mb-2 text-slate-800 dark:text-slate-200">Trip Info</h5>
          <p className="text-slate-600 dark:text-slate-300">
            <span className="font-semibold">{seatsLeft > 0 ? seatsLeft : '0'}</span> seats left
          </p>
          <p className="text-slate-600 dark:text-slate-300">
            <span className="font-semibold">{details.stopCount}</span> stop(s)
          </p>
        </div>

        <div>
          <h5 className="font-bold mb-2 text-slate-800 dark:text-slate-200">Vehicle Details</h5>
          {renderVehicleDetails(details)}
        </div>

        <div>
          <h5 className="font-bold mb-2 text-slate-800 dark:text-slate-200">Services</h5>
          {details.service && details.service.length > 0 ? (
            <div className="flex flex-wrap gap-2">
              {details.service.map(service => (
                <div key={service} className="flex items-center space-x-1 bg-slate-200 dark:bg-slate-700 px-2 py-1 rounded-full text-xs capitalize" title={service}>
                  {service.toLowerCase().includes('food') && <UtensilsCrossed size={14} />}
                  {service.toLowerCase().includes('wifi') && <Wifi size={14} />}
                  {service.toLowerCase().includes('vip') && <Armchair size={14} />}
                  <span>{service.toLowerCase().replace('_', ' ')}</span>
                </div>
              ))}
            </div>
          ) : (
            <p className="text-slate-500 dark:text-slate-400">No extra services.</p>
          )}
        </div>
      </div>
    </div>
  );
};

export default TicketDetailsView;