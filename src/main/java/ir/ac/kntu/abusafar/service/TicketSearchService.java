package ir.ac.kntu.abusafar.service;

import ir.ac.kntu.abusafar.dto.ticket.TicketResultItemDTO;
import ir.ac.kntu.abusafar.dto.ticket.TicketSearchRequestDTO;
import ir.ac.kntu.abusafar.model.Ticket;

import java.util.List;

public interface TicketSearchService {
    List<TicketResultItemDTO> searchTickets(TicketSearchRequestDTO requestDTO);
    TicketResultItemDTO mapTicketToResultItemDTO(Ticket ticket);
}
