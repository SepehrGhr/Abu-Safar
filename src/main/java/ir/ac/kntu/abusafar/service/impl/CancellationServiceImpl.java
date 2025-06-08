package ir.ac.kntu.abusafar.service.impl;

import ir.ac.kntu.abusafar.dto.cancellation.CancellationPenaltyResponseDTO;
import ir.ac.kntu.abusafar.dto.cancellation.CancellationResponseDTO;
import ir.ac.kntu.abusafar.exception.*;
import ir.ac.kntu.abusafar.model.*;
import ir.ac.kntu.abusafar.repository.*;
import ir.ac.kntu.abusafar.service.CancellationService;
import ir.ac.kntu.abusafar.util.constants.enums.ReserveStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class CancellationServiceImpl implements CancellationService {

    private final ReservationDAO reservationDAO;
    private final TicketReservationDAO ticketReservationDAO;
    private final TripDAO tripDAO;
    private final TicketDAO ticketDAO;
    private final CompanyDAO companyDAO;
    private final UserDAO userDAO;

    @Autowired
    public CancellationServiceImpl(ReservationDAO reservationDAO, TicketReservationDAO ticketReservationDAO, TripDAO tripDAO, TicketDAO ticketDAO, CompanyDAO companyDAO, UserDAO userDAO) {
        this.reservationDAO = reservationDAO;
        this.ticketReservationDAO = ticketReservationDAO;
        this.tripDAO = tripDAO;
        this.ticketDAO = ticketDAO;
        this.companyDAO = companyDAO;
        this.userDAO = userDAO;
    }

    @Override
    @Transactional(readOnly = true)
    public CancellationPenaltyResponseDTO calculatePenalty(Long userId, Long reservationId) {
        Reservation reservation = reservationDAO.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation with ID " + reservationId + " not found."));

        if (!reservation.getUserId().equals(userId)) {
            throw new AccessDeniedException("You are not authorized to view this reservation's cancellation penalty.");
        }

        if (reservation.getReserveStatus() == ReserveStatus.CANCELLED) {
            throw new IllegalStateException("This reservation has already been cancelled.");
        }

        List<TicketReservation> ticketReservations = ticketReservationDAO.findAllByReservationId(reservationId);
        if (ticketReservations.isEmpty()) {
            throw new TicketNotFoundException("No tickets found for this reservation. It may have already been cancelled.");
        }

        BigDecimal totalOriginalPrice = BigDecimal.ZERO;
        BigDecimal totalPenaltyAmount = BigDecimal.ZERO;

        for (TicketReservation ticketRes : ticketReservations) {
            Trip trip = tripDAO.findById(ticketRes.getTripId())
                    .orElseThrow(() -> new TicketNotFoundException("Trip with ID " + ticketRes.getTripId() + " not found."));

            if (Duration.between(OffsetDateTime.now(), trip.getDepartureTimestamp()).toHours() < 24) {
                return new CancellationPenaltyResponseDTO(null, null, null, "Cancellation is not possible. At least one trip in the reservation departs in less than 24 hours.");
            }

            Company company = companyDAO.findById(trip.getCompanyId())
                    .orElseThrow(() -> new CompanyNotFoundException("Associated transport company for trip " + trip.getTripId() + " not found."));

            Ticket ticket = ticketDAO.findById(trip.getTripId(), ticketRes.getAge())
                    .orElseThrow(() -> new TicketNotFoundException("Ticket details not found for trip " + trip.getTripId()));

            BigDecimal originalPrice = ticket.getPrice();
            BigDecimal basePenaltyRate = company.getCancellationPenaltyRate();
            long daysRemaining = Duration.between(OffsetDateTime.now(), trip.getDepartureTimestamp()).toDays();

            double modifier = (daysRemaining >= 7) ? 0.50 : (daysRemaining >= 3) ? 0.75 : 1.0;

            BigDecimal penaltyForThisTicket = originalPrice.multiply(basePenaltyRate).multiply(BigDecimal.valueOf(modifier)).divide(BigDecimal.valueOf(100));

            totalOriginalPrice = totalOriginalPrice.add(originalPrice);
            totalPenaltyAmount = totalPenaltyAmount.add(penaltyForThisTicket);
        }

        BigDecimal totalRefundAmount = totalOriginalPrice.subtract(totalPenaltyAmount);
        return new CancellationPenaltyResponseDTO(totalOriginalPrice, totalPenaltyAmount, totalRefundAmount, "Penalty calculated successfully for the entire reservation.");
    }

    @Override
    @Transactional
    public CancellationResponseDTO confirmCancellation(Long userId, Long reservationId) {
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Cancellation failed: User not found."));

        CancellationPenaltyResponseDTO penaltyDetails = calculatePenalty(userId, reservationId);

        if (penaltyDetails.getRefundAmount() == null) {
            throw new IllegalStateException(penaltyDetails.getMessage());
        }

        BigDecimal totalRefund = penaltyDetails.getRefundAmount();
        BigDecimal newWalletBalance = user.getWalletBalance().add(totalRefund);
        userDAO.updateWalletBalance(userId, newWalletBalance);

        List<TicketReservation> ticketsToCancel = ticketReservationDAO.findAllByReservationId(reservationId);

        reservationDAO.updateStatus(reservationId, ReserveStatus.CANCELLED, userId);

        return new CancellationResponseDTO(
                "Reservation " + reservationId + " cancelled successfully.",
                totalRefund,
                newWalletBalance
        );
    }
}
