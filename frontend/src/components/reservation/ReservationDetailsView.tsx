import React from 'react';
import { Armchair, Wifi, UtensilsCrossed, Star, Info, Building2, Bus, Train, Plane } from 'lucide-react';
import type { ReservationTicket, BusDetails, FlightDetails, TrainDetails } from '/src/services/api/types';

interface ReservationDetailsViewProps {
  ticket: ReservationTicket;
}

const DetailItem: React.FC<{ icon: React.ElementType; label: string; value: React.ReactNode }> = ({ icon: Icon, label, value }) => (
  <div className="flex items-center text-sm text-slate-600 dark:text-slate-400">
    <Icon className="w-4 h-4 mr-2 text-slate-500 flex-shrink-0" />
    <span className="font-semibold text-slate-700 dark:text-slate-300 mr-1">{label}:</span>
    {value}
  </div>
);

export const ReservationDetailsView: React.FC<ReservationDetailsViewProps> = ({ ticket }) => {
  const { vehicleDetails, tripVehicle, service } = ticket;

  const renderVehicleSpecificDetails = () => {
    switch (tripVehicle) {
      case 'BUS':
        const bus = vehicleDetails as BusDetails;
        return (
          <>
            <DetailItem icon={Bus} label="Class" value={bus.classType} />
            <DetailItem icon={Armchair} label="Chair Type" value={bus.chairType} />
          </>
        );
      case 'FLIGHT':
        const flight = vehicleDetails as FlightDetails;
        return (
          <>
            {/* This will always be in the first column */}
            <DetailItem icon={Plane} label="Class" value={flight.classType.replace('_', ' ')} />

            {/* This div groups the airports, ensuring they land in the same column */}
            <div className="space-y-2">
              <DetailItem icon={Building2} label="Departure" value={flight.departureAirport} />
              <DetailItem icon={Building2} label="Arrival" value={flight.arrivalAirport} />
            </div>
          </>
        );
      case 'TRAIN':
        const train = vehicleDetails as TrainDetails;
        return (
          <>
            <DetailItem icon={Star} label="Stars" value={<span className="font-semibold flex items-center">{train.stars} <Star size={16} className="ml-1 text-yellow-400" /></span>} />
            <DetailItem icon={Train} label="Room Type" value={train.roomType} />
          </>
        );
      default:
        return null;
    }
  };

  return (
    <div className="bg-slate-100/50 dark:bg-slate-900/50 px-6 py-4 border-t border-slate-200/50 dark:border-slate-800/50">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-x-6 gap-y-2">
            {renderVehicleSpecificDetails()}

            {(service && service.length > 0) && (
              <div className="flex items-start text-sm text-slate-600 dark:text-slate-400 md:col-span-2 mt-1">
                <Info className="w-4 h-4 mr-2 text-slate-500 flex-shrink-0 mt-0.5" />
                <div className="flex flex-wrap gap-2 items-center">
                    <span className="font-semibold text-slate-700 dark:text-slate-300 mr-1">Services:</span>
                    {service.map(s => (
                        <div key={s} className="flex items-center space-x-1 bg-slate-200 dark:bg-slate-700 px-2 py-0.5 rounded-full text-xs capitalize" title={s}>
                            {s.toLowerCase().includes('food') && <UtensilsCrossed size={14} />}
                            {s.toLowerCase().includes('wifi') && <Wifi size={14} />}
                            {s.toLowerCase().includes('vip') && <Armchair size={14} />}
                            <span>{s.toLowerCase().replace('_', ' ')}</span>
                        </div>
                    ))}
                </div>
              </div>
            )}
        </div>
    </div>
  );
};