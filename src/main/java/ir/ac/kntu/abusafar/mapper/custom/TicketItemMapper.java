package ir.ac.kntu.abusafar.mapper.custom;

import ir.ac.kntu.abusafar.dto.location.LocationResponseDTO;
import ir.ac.kntu.abusafar.dto.ticket.TicketResultItemDTO;
import ir.ac.kntu.abusafar.model.Ticket;
import ir.ac.kntu.abusafar.model.Trip;
import ir.ac.kntu.abusafar.service.LocationService;
import org.mapstruct.factory.Mappers;

import java.util.Optional;

public class TicketItemMapper {
    public static final TicketItemMapper INSTANCE = Mappers.getMapper(TicketItemMapper.class);
    private final LocationService locationService;

    private TicketItemMapper(LocationService locationService) {
        this.locationService = locationService;
    }


    public TicketResultItemDTO toDTO(Ticket ticket) {
        if (ticket == null || ticket.getTrip() == null) {
            return null;
        }
        Trip trip = ticket.getTrip();

        String originCityName = "Unknown";
        if (trip.getOriginLocationId() != null) {
            Optional<LocationResponseDTO> originLocOpt = locationService.getLocationById(trip.getOriginLocationId());
            if (originLocOpt.isPresent()) {
                originCityName = originLocOpt.get().getCity();
            } else {
                System.err.println("Warning: Could not find location details for origin ID: " + trip.getOriginLocationId());
            }
        }

        String destinationCityName = "Unknown";
        if (trip.getDestinationLocationId() != null) {
            Optional<LocationResponseDTO> destLocOpt = locationService.getLocationById(trip.getDestinationLocationId());
            if (destLocOpt.isPresent()) {
                destinationCityName = destLocOpt.get().getCity();
            } else {
                System.err.println("Warning: Could not find location details for destination ID: " + trip.getDestinationLocationId());
            }
        }

        TicketResultItemDTO dto = new TicketResultItemDTO();
        dto.setTripId(trip.getTripId());
        dto.setAge(ticket.getAge());
        dto.setOriginCity(originCityName);
        dto.setDestinationCity(destinationCityName);
        dto.setDepartureTimestamp(trip.getDepartureTimestamp());
        dto.setArrivalTimestamp(trip.getArrivalTimestamp());
        dto.setTripVehicle(ticket.getTripVehicle());
        dto.setPrice(ticket.getPrice());
        dto.setVehicleCompany(trip.getVehicleCompany());

        return dto;
    }
}
