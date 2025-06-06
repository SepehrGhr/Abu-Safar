package ir.ac.kntu.abusafar.service;

import ir.ac.kntu.abusafar.dto.reservation.InitialReserveResultDTO;
import ir.ac.kntu.abusafar.dto.reservation.ReserveConfirmationDTO;
import ir.ac.kntu.abusafar.dto.ticket.TicketSelectRequestDTO;

public interface BookingService {
    ReserveConfirmationDTO createOneWayReservation(Long userId, TicketSelectRequestDTO ticketRequest);
    ReserveConfirmationDTO createTwoWayReservation(Long userId, TicketSelectRequestDTO[] ticketRequests);
    void cancelExpiredReservation(Long reservationId);
}
