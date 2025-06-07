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
        TicketResultItemDTO dto = new TicketResultItemDTO();

        String originCityName = "Unknown";
        if (trip.getOriginLocationId() != null) {
            Optional<LocationResponseDTO> originLocOpt = locationService.getLocationById(trip.getOriginLocationId());
            originLocOpt.ifPresent(locationResponseDTO -> dto.setOriginCity(locationResponseDTO.getCity()));
        }

        String destinationCityName = "Unknown";
        if (trip.getDestinationLocationId() != null) {
            Optional<LocationResponseDTO> destLocOpt = locationService.getLocationById(trip.getDestinationLocationId());
            destLocOpt.ifPresent(locationResponseDTO -> dto.setDestinationCity(locationResponseDTO.getCity()));
        }

        if (trip.getCompanyId() != null) {
            companyDAO.findById(trip.getCompanyId())
                    .map(Company::getName)
                    .ifPresent(dto::setVehicleCompany);
        } else {
            dto.setVehicleCompany("Unknown Company");
        }

        dto.setTripId(trip.getTripId());
        dto.setAge(ticket.getAge());
        dto.setDepartureTimestamp(trip.getDepartureTimestamp());
        dto.setArrivalTimestamp(trip.getArrivalTimestamp());
        dto.setTripVehicle(ticket.getTripVehicle());
        dto.setPrice(ticket.getPrice());

        return dto;
    }
}