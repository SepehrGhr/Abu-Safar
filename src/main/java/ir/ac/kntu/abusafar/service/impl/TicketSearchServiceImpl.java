package ir.ac.kntu.abusafar.service.impl;

import ir.ac.kntu.abusafar.dto.location.LocationResponseDTO;
import ir.ac.kntu.abusafar.dto.ticket.TicketSearchRequestDTO;
import ir.ac.kntu.abusafar.dto.ticket.TicketResultItemDTO;
import ir.ac.kntu.abusafar.model.Ticket;
import ir.ac.kntu.abusafar.model.Trip;
import ir.ac.kntu.abusafar.repository.TicketDAO;
import ir.ac.kntu.abusafar.repository.params.TicketSearchParameters;
import ir.ac.kntu.abusafar.service.LocationService;
import ir.ac.kntu.abusafar.service.TicketSearchService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TicketSearchServiceImpl implements TicketSearchService {

    private final TicketDAO ticketDAO;
    private final LocationService locationService;

    @Autowired
    public TicketSearchServiceImpl(TicketDAO ticketDAO, LocationService locationService) {
        this.ticketDAO = ticketDAO;
        this.locationService = locationService;
    }

    @Override
    public List<TicketResultItemDTO> searchTickets(TicketSearchRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new IllegalArgumentException("Search request DTO cannot be null.");
        }
        List<Long> originIds = locationService.findLocationIdByDetails(requestDTO.getOriginCity(), requestDTO.getOriginProvince(), requestDTO.getOriginCountry());

        List<Long> destinationIds = locationService.findLocationIdByDetails(requestDTO.getDestinationCity(), requestDTO.getDestinationProvince(), requestDTO.getDestinationCountry());


        OffsetDateTime departureFrom = null;
        OffsetDateTime departureTo = null;
        if (requestDTO.getDepartureDate() != null) {
            LocalDate date = requestDTO.getDepartureDate();
            LocalTime time = requestDTO.getDepartureTime();
            ZoneOffset zone = ZoneOffset.UTC;

            if (time != null) {
                departureFrom = date.atTime(time).atOffset(zone);
                departureTo = date.atTime(time).plusHours(1).atOffset(zone);
            } else {
                departureFrom = date.atStartOfDay().atOffset(zone);
                departureTo = date.plusDays(1).atStartOfDay().atOffset(zone);
            }
        }

        TicketSearchParameters searchParams = new TicketSearchParameters(
                originIds,
                destinationIds,
                departureFrom,
                departureTo,
                requestDTO.getVehicleCompany(),
                requestDTO.getTripVehicle(),
                requestDTO.getAgeCategory(),
                requestDTO.getMinPrice(),
                requestDTO.getMaxPrice(),
                requestDTO.getBusClass(),
                requestDTO.getFlightClass(),
                requestDTO.getTrainStars()
        );

        List<Ticket> foundTickets = ticketDAO.findTicketsByCriteria(searchParams);

        if (foundTickets.isEmpty()) {
            return Collections.emptyList();
        }

        return foundTickets.stream()
                .map(this::mapTicketToResultItemDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TicketResultItemDTO mapTicketToResultItemDTO(Ticket ticket) {
        if (ticket == null || ticket.getTrip() == null) {
            return null; // Or throw an error
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