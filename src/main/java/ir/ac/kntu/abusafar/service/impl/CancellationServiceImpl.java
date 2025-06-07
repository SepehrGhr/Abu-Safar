package ir.ac.kntu.abusafar.service.impl;

import ir.ac.kntu.abusafar.dto.cancellation.CancellationPenaltyResponseDTO;
import ir.ac.kntu.abusafar.exception.CompanyNotFoundException;
import ir.ac.kntu.abusafar.exception.ReservationNotFoundException;
import ir.ac.kntu.abusafar.exception.TicketNotFoundException;
import ir.ac.kntu.abusafar.model.*;
import ir.ac.kntu.abusafar.repository.*;
import ir.ac.kntu.abusafar.service.CancellationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.OffsetDateTime;

@Service
public class CancellationServiceImpl implements CancellationService {

    private final ReservationDAO reservationDAO;
    private final TicketReservationDAO ticketReservationDAO;
    private final TripDAO tripDAO;
    private final TicketDAO ticketDAO;
    private final CompanyDAO companyDAO;

    @Autowired
    public CancellationServiceImpl(ReservationDAO reservationDAO, TicketReservationDAO ticketReservationDAO, TripDAO tripDAO, TicketDAO ticketDAO, CompanyDAO companyDAO) {
        this.reservationDAO = reservationDAO;
        this.ticketReservationDAO = ticketReservationDAO;
        this.tripDAO = tripDAO;
        this.ticketDAO = ticketDAO;
        this.companyDAO = companyDAO;
    }

    @Override
    @Transactional(readOnly = true)
    public CancellationPenaltyResponseDTO calculatePenalty(Long userId, Long reservationId, Long tripId) {
        Reservation reservation = reservationDAO.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation with ID " + reservationId + " not found."));

        if (!reservation.getUserId().equals(userId)) {
            throw new AccessDeniedException("You are not authorized to view this reservation's cancellation penalty.");
        }

        TicketReservation ticketReservation = ticketReservationDAO.findByReservationAndTrip(reservationId, tripId)
                .orElseThrow(() -> new TicketNotFoundException("No ticket for trip " + tripId + " found in the specified reservation."));

        Trip trip = tripDAO.findById(tripId)
                .orElseThrow(() -> new TicketNotFoundException("Trip with ID " + tripId + " not found."));

        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime departure = trip.getDepartureTimestamp();

        long diffInSeconds = Duration.between(now, departure).getSeconds();
        long hoursRemaining = diffInSeconds / 3600;

        if (hoursRemaining < 24) {
            return new CancellationPenaltyResponseDTO(null, null, null, "Cancellation is not possible as less than 24 hours remain until departure.");
        }

        Company company = companyDAO.findById(trip.getCompanyId())
                .orElseThrow(() -> new CompanyNotFoundException("Associated transport company for trip " + tripId + " not found."));
        Ticket ticket = ticketDAO.findById(tripId, ticketReservation.getAge())
                .orElseThrow(() -> new TicketNotFoundException("Ticket details for the specified trip and age not found."));

        BigDecimal originalPrice = ticket.getPrice();
        BigDecimal basePenaltyRate = company.getCancellationPenaltyRate();
        double modifier;

        long daysRemaining = hoursRemaining / 24;

        if (daysRemaining >= 7) {
            modifier = 0.50;
        } else if (daysRemaining >= 3) {
            modifier = 0.75;
        } else {
            modifier = 1.0;
        }

        BigDecimal finalPenaltyRate = basePenaltyRate.multiply(BigDecimal.valueOf(modifier));
        BigDecimal penaltyAmount = originalPrice.multiply(finalPenaltyRate).divide(BigDecimal.valueOf(100));
        BigDecimal refundAmount = originalPrice.subtract(penaltyAmount);

        return new CancellationPenaltyResponseDTO(originalPrice, penaltyAmount, refundAmount, "Penalty calculated successfully.");
    }
}
