export interface Location {
  locationId: number;
  city: string;
  province: string;
  country: string;
}

export interface Ticket {
  tripId: number;
  origin: string;
  destination: string;
  departureTime: string;
  arrivalTime: string;
  price: number;
  companyName: string;
  vehicleType: 'FLIGHT' | 'BUS' | 'TRAIN';
  details: any;
}

export interface TicketSearchRequest {
  originId: number;
  destinationId: number;
  departureDate: string;
  tripVehicle: 'FLIGHT' | 'BUS' | 'TRAIN';
  ageCategory: 'ADULT' | 'CHILD' | 'INFANT';
}

export interface Company {
  id: number;
  name: string;
  vehicleType: string;
}


