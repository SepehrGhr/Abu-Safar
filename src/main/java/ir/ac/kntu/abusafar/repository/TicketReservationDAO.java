package ir.ac.kntu.abusafar.repository;

import ir.ac.kntu.abusafar.model.TicketReservation;

import java.util.List;
import java.util.Optional;

public interface TicketReservationDAO {
    Optional<TicketReservation> findByReservationAndTrip(Long reservationId, Long tripId);

    List<TicketReservation> findAllByReservationId(Long reservationId);
    int updateSeatNumber(Long reservationId, Long tripId, short newSeatNumber);
}