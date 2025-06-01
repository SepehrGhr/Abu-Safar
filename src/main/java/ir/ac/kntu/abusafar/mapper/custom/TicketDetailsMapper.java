package ir.ac.kntu.abusafar.mapper.custom;

import ir.ac.kntu.abusafar.dto.location.LocationResponseDTO;
import ir.ac.kntu.abusafar.dto.ticket.TicketResultDetailsDTO;
import ir.ac.kntu.abusafar.model.Ticket;
import ir.ac.kntu.abusafar.model.Trip;
import ir.ac.kntu.abusafar.service.LocationService;
import ir.ac.kntu.abusafar.service.TripService;
import ir.ac.kntu.abusafar.util.constants.enums.ServiceType;
import ir.ac.kntu.abusafar.util.constants.enums.TripType;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TicketDetailsMapper {
    public static final TicketItemMapper INSTANCE = Mappers.getMapper(TicketItemMapper.class);
    private final LocationService locationService;
    private final TripService tripService;

    private TicketDetailsMapper(LocationService locationService, TripService tripService) {
        this.locationService = locationService;
        this.tripService = tripService;
    }

    public TicketResultDetailsDTO toDTO(Ticket ticket) {
        if (ticket == null || ticket.getTrip() == null) {
            return null;
        }

        Trip trip = ticket.getTrip();
        TicketResultDetailsDTO dto = new TicketResultDetailsDTO();

        String originCityName = "Unknown";
        if (trip.getOriginLocationId() != null) {
            Optional<LocationResponseDTO> originLocOpt = locationService.getLocationById(trip.getOriginLocationId());
            if (originLocOpt.isPresent()) {
                originCityName = originLocOpt.get().getCity();
            } else {
                // Log or handle missing location more robustly if needed
                System.err.println("Warning: Could not find location details for origin ID: " + trip.getOriginLocationId());
            }
        }
        dto.setOriginCity(originCityName);

        String destinationCityName = "Unknown";
        if (trip.getDestinationLocationId() != null) {
            Optional<LocationResponseDTO> destLocOpt = locationService.getLocationById(trip.getDestinationLocationId());
            if (destLocOpt.isPresent()) {
                destinationCityName = destLocOpt.get().getCity();
            } else {
                System.err.println("Warning: Could not find location details for destination ID: " + trip.getDestinationLocationId());
            }
        }
        dto.setDestinationCity(destinationCityName);

        dto.setDepartureTimestamp(trip.getDepartureTimestamp());
        dto.setArrivalTimestamp(trip.getArrivalTimestamp());
        dto.setTripVehicle(ticket.getTripVehicle()); // from Ticket
        dto.setPrice(ticket.getPrice());             // from Ticket
        dto.setAge(ticket.getAge());                 // from Ticket
        dto.setVehicleCompany(trip.getVehicleCompany());
        dto.setStopCount(trip.getStopCount());
        dto.setTotalCapacity(trip.getTotalCapacity());
        dto.setReservedCapacity(trip.getReservedCapacity());

        if (ticket.getTripVehicle() == TripType.BUS && trip.getBusClass() != null) {
            dto.setBusClass(trip.getBusClass());
        } else if (ticket.getTripVehicle() == TripType.FLIGHT && trip.getFlightClass() != null) {
            dto.setFlightClass(trip.getFlightClass());
        } else if (ticket.getTripVehicle() == TripType.TRAIN && trip.getTrainStars() != null) {
            dto.setTrainStars(trip.getTrainStars());
        }
        if (trip.getTripId() != null) {
            List<ServiceType> services = tripService.getServicesForTrip(trip.getTripId());
            dto.setService(services != null ? services : Collections.emptyList());
        } else {
            dto.setService(Collections.emptyList());
        }

        return dto;
    }
}
