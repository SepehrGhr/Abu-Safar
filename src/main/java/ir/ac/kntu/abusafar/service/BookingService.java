package ir.ac.kntu.abusafar.service;

import ir.ac.kntu.abusafar.dto.reservation.InitialBookResultDTO;
import ir.ac.kntu.abusafar.dto.ticket.TicketSelectRequestDTO;

public interface BookingService {
    InitialBookResultDTO createReservation(TicketSelectRequestDTO ticket);
}
