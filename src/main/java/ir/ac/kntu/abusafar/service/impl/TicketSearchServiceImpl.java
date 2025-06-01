package ir.ac.kntu.abusafar.service.impl;

import ir.ac.kntu.abusafar.dto.ticket.TicketResultDetailsDTO;
import ir.ac.kntu.abusafar.dto.ticket.TicketSearchRequestDTO;
import ir.ac.kntu.abusafar.dto.ticket.TicketResultItemDTO;
import ir.ac.kntu.abusafar.mapper.custom.TicketItemMapper;
import ir.ac.kntu.abusafar.model.Ticket;
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
                .map(TicketItemMapper.INSTANCE::toDTO)
                .toList();
    }

    @Override
    public Optional<TicketResultDetailsDTO> selectTicket(Long trip_id, String age){

    }


}