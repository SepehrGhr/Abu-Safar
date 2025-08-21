export interface Location {
  locationId: number;
  city: string;
  province: string;
  country: string;
}

export interface Ticket {
  tripId: number;
  age: 'ADULT' | 'CHILD' | 'BABY';
  originCity: string;
  destinationCity: string;
  departureTimestamp: string;
  arrivalTimestamp: string;
  tripVehicle: 'TRAIN' | 'BUS' | 'FLIGHT';
  price: number;
  vehicleCompany: string;
}

export interface TicketSearchRequest {
  originId: number;
  destinationId: number;
  departureDate: string;
  tripVehicle: 'FLIGHT' | 'BUS' | 'TRAIN';
  ageCategory: 'ADULT' | 'CHILD' | 'BABY';
  vehicleCompany?: string;
  minPrice?: number;
  maxPrice?: number;
  busClass?: string[];
  flightClass?: string[];
  trainStars?: number;
}

export interface Company {
  id: number;
  name: string;
  vehicleType: string;
}

export interface TicketSelectRequest {
  tripId: number;
  ageCategory: 'ADULT' | 'CHILD' | 'BABY';
}

export interface BusDetails {
  classType: string;
  chairType: string;
}

export interface FlightDetails {
  classType: string;
  departureAirport: string;
  arrivalAirport: string;
}

export interface TrainDetails {
  stars: number;
  roomType: string;
}

export interface TicketDetails {
  origin: string;
  destination: string;
  departureTimestamp: string;
  arrivalTimestamp: string;
  tripVehicle: 'TRAIN' | 'BUS' | 'FLIGHT';
  price: number;
  companyName: string;
  vehicleDetails: BusDetails | FlightDetails | TrainDetails;
  stopCount: number;
  totalCapacity: number;
  reservedCapacity: number;
  age: 'ADULT' | 'CHILD' | 'BABY';
  service: string[];
}

export interface ReservationTicket {
    tripId: number;
    originCity: string;
    destinationCity: string;
    departureTimestamp: string;
    arrivalTimestamp: string;
    tripVehicle: 'TRAIN' | 'BUS' | 'FLIGHT';
    price: number;
    companyName: string;
    vehicleDetails: BusDetails | FlightDetails | TrainDetails;
    stopCount: number;
    totalCapacity: number;
    reservedCapacity: number;
    age: 'ADULT' | 'CHILD' | 'BABY';
    service: string[];
}

export interface ReserveConfirmation {
  reservationId: number;
  reservationDatetime: string;
  expirationDatetime: string;
  isRoundTrip: boolean;
  tickets: ReservationTicket[];
  seatNumbers: number[];
  price: number;
}