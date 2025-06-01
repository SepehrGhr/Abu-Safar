package ir.ac.kntu.abusafar.mapper.custom;

import ir.ac.kntu.abusafar.dto.ticket.TicketResultDetailsDTO;
import ir.ac.kntu.abusafar.model.Ticket;
import ir.ac.kntu.abusafar.service.LocationService;
import org.mapstruct.factory.Mappers;

public class TicketDetailsMapper {
    public static final TicketItemMapper INSTANCE = Mappers.getMapper(TicketItemMapper.class);
    private final LocationService locationService;

    private TicketDetailsMapper(LocationService locationService) {
        this.locationService = locationService;
    }

    public TicketResultDetailsDTO toDTO(Ticket ticket){

    }
}
