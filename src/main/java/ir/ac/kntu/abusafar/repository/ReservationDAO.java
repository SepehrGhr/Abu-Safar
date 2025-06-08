package ir.ac.kntu.abusafar.repository;

import ir.ac.kntu.abusafar.dto.reservation.InitialReserveResultDTO;
import ir.ac.kntu.abusafar.dto.reservation.ReservationInputDTO;
import ir.ac.kntu.abusafar.dto.reservation.TicketReserveDetailsDTO;
import ir.ac.kntu.abusafar.model.Reservation;
import ir.ac.kntu.abusafar.util.constants.enums.ReserveStatus;

import java.util.List;
import java.util.Optional;

public interface ReservationDAO {
    Optional<Reservation> findById(Long reservationId);
    List<Short> getReservedSeatNumbersForTrip(Long tripId);
    Boolean updateStatus(Long reservationId, ReserveStatus status, Long cancelledBy);
    int deleteById(Long reservationId);
    InitialReserveResultDTO saveInitialReservation(ReservationInputDTO reservationInput, List<TicketReserveDetailsDTO> ticketDetailsList);
}
