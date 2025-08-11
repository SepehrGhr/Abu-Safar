package ir.ac.kntu.abusafar.service;

import ir.ac.kntu.abusafar.dto.ticket.TicketResultDetailsDTO;
import ir.ac.kntu.abusafar.dto.ticket.TicketResultItemDTO;
import ir.ac.kntu.abusafar.dto.ticket.TicketSearchRequestDTO;
import ir.ac.kntu.abusafar.dto.ticket.TicketSelectRequestDTO;
import ir.ac.kntu.abusafar.model.Ticket;

import java.util.List;
import java.util.Optional;

public interface TicketSearchService {
    List<TicketResultItemDTO> searchTickets(TicketSearchRequestDTO requestDTO);
    Optional<TicketResultDetailsDTO> selectTicket(TicketSelectRequestDTO requestDTO);
}
