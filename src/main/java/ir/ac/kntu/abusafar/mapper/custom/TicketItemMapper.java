package ir.ac.kntu.abusafar.mapper.custom;

import ir.ac.kntu.abusafar.dto.location.LocationResponseDTO;
import ir.ac.kntu.abusafar.dto.ticket.TicketResultItemDTO;
import ir.ac.kntu.abusafar.model.Company;
import ir.ac.kntu.abusafar.model.Ticket;
import ir.ac.kntu.abusafar.model.Trip;
import ir.ac.kntu.abusafar.repository.CompanyDAO;
import ir.ac.kntu.abusafar.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TicketItemMapper {

    private final LocationService locationService;
    private final CompanyDAO companyDAO;

    @Autowired
    public TicketItemMapper(LocationService locationService, CompanyDAO companyDAO) {
        this.locationService = locationService;
        this.companyDAO = companyDAO;
    }

    public TicketResultItemDTO toDTO(Ticket ticket) {
        if (ticket == null || ticket.getTrip() == null) {
            return null;
        }
        Trip trip = ticket.getTrip();

        String originCityName = locationService.getLocationById(trip.getOriginLocationId())
                .map(LocationResponseDTO::city)
                .orElse("Unknown");

        String destinationCityName = locationService.getLocationById(trip.getDestinationLocationId())
                .map(LocationResponseDTO::city)
                .orElse("Unknown");

        String companyName = companyDAO.findById(trip.getCompanyId())
                .map(Company::getName)
                .orElse("Unknown Company");

        return new TicketResultItemDTO(
                trip.getTripId(),
                ticket.getAge(),
                originCityName,
                destinationCityName,
                trip.getDepartureTimestamp(),
                trip.getArrivalTimestamp(),
                ticket.getTripVehicle(),
                ticket.getPrice(),
                companyName
        );
    }
}
