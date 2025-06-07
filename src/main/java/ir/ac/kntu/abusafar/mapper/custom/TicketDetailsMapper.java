package ir.ac.kntu.abusafar.mapper.custom;

import ir.ac.kntu.abusafar.dto.vehicle.*;
import ir.ac.kntu.abusafar.dto.location.LocationResponseDTO;
import ir.ac.kntu.abusafar.dto.ticket.TicketResultDetailsDTO;
import ir.ac.kntu.abusafar.model.Ticket;
import ir.ac.kntu.abusafar.model.Trip;
import ir.ac.kntu.abusafar.service.LocationService;
import ir.ac.kntu.abusafar.service.TripService;
import ir.ac.kntu.abusafar.util.constants.enums.ServiceType;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        TicketResultDetailsDTO detailsDTO = new TicketResultDetailsDTO();

        detailsDTO.setPrice(ticket.getPrice());
        detailsDTO.setAge(ticket.getAge());
        detailsDTO.setTripVehicle(ticket.getTripVehicle());

        detailsDTO.setDepartureTimestamp(trip.getDepartureTimestamp());
        detailsDTO.setArrivalTimestamp(trip.getArrivalTimestamp());
        detailsDTO.setVehicleCompany(trip.getVehicleCompany());
        detailsDTO.setStopCount(trip.getStopCount());
        detailsDTO.setTotalCapacity(trip.getTotalCapacity());
        detailsDTO.setReservedCapacity(trip.getReservedCapacity());

        if (trip.getOriginLocationId() != null) {
            locationService.getLocationById(trip.getOriginLocationId())
                    .map(LocationResponseDTO::getCity)
                    .ifPresentOrElse(detailsDTO::setOriginCity,
                            () -> detailsDTO.setOriginCity("Unknown"));
        } else {
            detailsDTO.setOriginCity("Unknown");
        }

        if (trip.getDestinationLocationId() != null) {
            locationService.getLocationById(trip.getDestinationLocationId())
                    .map(LocationResponseDTO::getCity)
                    .ifPresentOrElse(detailsDTO::setDestinationCity,
                            () -> detailsDTO.setDestinationCity("Unknown"));
        } else {
            detailsDTO.setDestinationCity("Unknown");
        }

        if (trip.getTripId() != null) {
            List<ServiceType> services = tripService.getServicesForTrip(trip.getTripId());
            if (services != null) {
                detailsDTO.setService(services);
            } else {
                detailsDTO.setService(Collections.emptyList());
            }
        } else {
            detailsDTO.setService(Collections.emptyList());
        }

        VehicleDetailsDTO vehicleSpecificDetails = switch (ticket.getTripVehicle()) {
            case BUS -> tripService.getBusDetails(trip.getTripId()).orElse(null);
            case FLIGHT -> tripService.getFlightDetails(trip.getTripId()).orElse(null);
            case TRAIN -> tripService.getTrainDetails(trip.getTripId()).orElse(null);
        };
        detailsDTO.setVehicleDetails(vehicleSpecificDetails);

        return detailsDTO;
    }
}