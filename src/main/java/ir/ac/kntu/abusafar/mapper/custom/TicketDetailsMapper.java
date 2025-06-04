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


        switch (ticket.getTripVehicle()) {
            case BUS:
                Optional<BusDetailsDTO> busDetailsOpt = tripService.getBusDetails(trip.getTripId());
                busDetailsOpt.ifPresent(busDetails -> {
                    detailsDTO.setBusClass(busDetails.getClassType());
                    detailsDTO.setChairType(busDetails.getChairType());
                });
                break;
            case FLIGHT:
                Optional<FlightDetailsDTO> flightDetailsOpt = tripService.getFlightDetails(trip.getTripId());
                flightDetailsOpt.ifPresent(flightDetails -> {
                    detailsDTO.setFlightClass(flightDetails.getClassType());
                    detailsDTO.setDepartureAirport(flightDetails.getDepartureAirport());
                    detailsDTO.setArrivalAirport(flightDetails.getArrivalAirport());
                });
                break;
            case TRAIN:
                Optional<TrainDetailsDTO> trainDetailsOpt = tripService.getTrainDetails(trip.getTripId());
                trainDetailsOpt.ifPresent(trainDetails -> {
                    detailsDTO.setTrainStars(trainDetails.getStars());
                    detailsDTO.setRoomType(trainDetails.getRoomType());
                });
                break;
            default:
                break;
        }

        return detailsDTO;
    }
}