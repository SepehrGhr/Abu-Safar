package ir.ac.kntu.abusafar.mapper.custom;

import ir.ac.kntu.abusafar.dto.location.LocationResponseDTO;
import ir.ac.kntu.abusafar.dto.ticket.TicketResultDetailsDTO;
import ir.ac.kntu.abusafar.dto.vehicle.VehicleDetailsDTO;
import ir.ac.kntu.abusafar.model.Company;
import ir.ac.kntu.abusafar.model.Ticket;
import ir.ac.kntu.abusafar.model.Trip;
import ir.ac.kntu.abusafar.service.LocationService;
import ir.ac.kntu.abusafar.service.TripService;
import ir.ac.kntu.abusafar.util.constants.enums.ServiceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class TicketDetailsMapper {

    private final LocationService locationService;
    private final TripService tripService;

    @Autowired
    public TicketDetailsMapper(LocationService locationService, TripService tripService) {
        this.locationService = locationService;
        this.tripService = tripService;
    }

    public TicketResultDetailsDTO toDTO(Ticket ticket) {
        if (ticket == null || ticket.getTrip() == null) {
            return null;
        }

        Trip trip = ticket.getTrip();

        String originCity = locationService.getLocationById(trip.getOriginLocationId())
                .map(LocationResponseDTO::city)
                .orElse("Unknown");

        String destinationCity = locationService.getLocationById(trip.getDestinationLocationId())
                .map(LocationResponseDTO::city)
                .orElse("Unknown");

        String companyName = tripService.getCompanyById(trip.getCompanyId())
                .map(Company::getName)
                .orElse("Unknown Company");

        List<ServiceType> services = trip.getTripId() != null
                ? tripService.getServicesForTrip(trip.getTripId())
                : Collections.emptyList();

        VehicleDetailsDTO vehicleSpecificDetails = switch (ticket.getTripVehicle()) {
            case BUS -> tripService.getBusDetails(trip.getTripId()).orElse(null);
            case FLIGHT -> tripService.getFlightDetails(trip.getTripId()).orElse(null);
            case TRAIN -> tripService.getTrainDetails(trip.getTripId()).orElse(null);
        };

        return new TicketResultDetailsDTO(
                originCity,
                destinationCity,
                trip.getDepartureTimestamp(),
                trip.getArrivalTimestamp(),
                ticket.getTripVehicle(),
                ticket.getPrice(),
                companyName,
                vehicleSpecificDetails,
                trip.getStopCount(),
                trip.getTotalCapacity(),
                trip.getReservedCapacity(),
                ticket.getAge(),
                services
        );
    }
}
