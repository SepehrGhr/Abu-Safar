package ir.ac.kntu.abusafar.service.impl;

import ir.ac.kntu.abusafar.dto.reservation.InitialReserveResultDTO;
import ir.ac.kntu.abusafar.dto.reservation.ReserveConfirmationDTO;
import ir.ac.kntu.abusafar.dto.reservation.ReservationInputDTO;
import ir.ac.kntu.abusafar.dto.reservation.TicketReserveDetailsDTO;
import ir.ac.kntu.abusafar.dto.ticket.TicketResultItemDTO;
import ir.ac.kntu.abusafar.dto.ticket.TicketSelectRequestDTO;
import ir.ac.kntu.abusafar.exception.*;
import ir.ac.kntu.abusafar.mapper.custom.TicketItemMapper;
import ir.ac.kntu.abusafar.model.Reservation;
import ir.ac.kntu.abusafar.model.Ticket;
import ir.ac.kntu.abusafar.model.Trip;
import ir.ac.kntu.abusafar.repository.ReservationDAO;
import ir.ac.kntu.abusafar.repository.TicketDAO;
import ir.ac.kntu.abusafar.service.BookingService;
import ir.ac.kntu.abusafar.service.RedisReserveService;
import ir.ac.kntu.abusafar.util.constants.enums.AgeRange;
import ir.ac.kntu.abusafar.util.constants.enums.ReserveStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final TicketDAO ticketDAO;
    private final ReservationDAO reservationDAO;
    private final RedisReserveService redisService;
    private final TicketItemMapper ticketItemMapper;

    private static final String REDIS_RESERVATION_EXPIRE_PREFIX = "reservation:expire:";
    private static final String REDIS_RESERVATION_REMIND_PREFIX = "reservation:remind:";
    private static final long TEN_MINUTES_IN_SECONDS = 10 * 60;
    private static final long FIVE_MINUTES_IN_SECONDS = 5 * 60;
    private static final String REDIS_KEY_VALUE = "active";

    private record ProcessedTicketInfo(Ticket ticket, BigDecimal price, Short seatNumber) {}

    @Autowired
    public BookingServiceImpl(TicketDAO ticketDAO, ReservationDAO reservationDAO, RedisReserveService redisService, TicketItemMapper ticketItemMapper) {
        this.ticketDAO = ticketDAO;
        this.reservationDAO = reservationDAO;
        this.redisService = redisService;
        this.ticketItemMapper = ticketItemMapper;
    }

    @Override
    @Transactional
    public ReserveConfirmationDTO createOneWayReservation(Long userId, TicketSelectRequestDTO ticketRequest) {
        if (userId == null || ticketRequest == null) {
            throw new IllegalArgumentException("User ID and TicketSelectRequestDTO cannot be null for one-way reservation.");
        }
        if (ticketRequest.getTripId() == null || ticketRequest.getAgeCategory() == null) {
            throw new IllegalArgumentException("Trip ID and Age Category in TicketSelectRequestDTO cannot be null.");
        }

        ProcessedTicketInfo processedTicket = processSingleTicketLeg(ticketRequest.getTripId(), ticketRequest.getAgeCategory());

        List<TicketReserveDetailsDTO> ticketDetailsList = Collections.singletonList(
                new TicketReserveDetailsDTO(
                        processedTicket.ticket().getTrip().getTripId(),
                        ticketRequest.getAgeCategory(),
                        processedTicket.seatNumber())
        );

        List<TicketResultItemDTO> tickets = List.of(ticketItemMapper.toDTO(processedTicket.ticket()));
        List<Short> seatNumbers = List.of(processedTicket.seatNumber());

        return completeReservationProcess(userId, ticketDetailsList, processedTicket.price(), false, tickets, seatNumbers);
    }

    @Override
    @Transactional
    public ReserveConfirmationDTO createTwoWayReservation(Long userId, TicketSelectRequestDTO[] ticketRequests) {
        if (userId == null || ticketRequests == null || ticketRequests.length != 2) {
            throw new IllegalArgumentException("User ID cannot be null and exactly two TicketSelectRequestDTOs are required for a two-way reservation.");
        }
        if (ticketRequests[0] == null || ticketRequests[0].getTripId() == null || ticketRequests[0].getAgeCategory() == null ||
                ticketRequests[1] == null || ticketRequests[1].getTripId() == null || ticketRequests[1].getAgeCategory() == null) {
            throw new IllegalArgumentException("Trip ID and Age Category in both TicketSelectRequestDTOs cannot be null.");
        }

        ProcessedTicketInfo outgoingProcessedTicket = processSingleTicketLeg(ticketRequests[0].getTripId(), ticketRequests[0].getAgeCategory());
        ProcessedTicketInfo returnProcessedTicket = processSingleTicketLeg(ticketRequests[1].getTripId(), ticketRequests[1].getAgeCategory());

        validateRoundTripLegs(outgoingProcessedTicket.ticket(), returnProcessedTicket.ticket());

        BigDecimal totalPrice = outgoingProcessedTicket.price().add(returnProcessedTicket.price());

        List<TicketReserveDetailsDTO> ticketDetailsList = List.of(
                new TicketReserveDetailsDTO(
                        outgoingProcessedTicket.ticket().getTrip().getTripId(),
                        ticketRequests[0].getAgeCategory(),
                        outgoingProcessedTicket.seatNumber()),
                new TicketReserveDetailsDTO(
                        returnProcessedTicket.ticket().getTrip().getTripId(),
                        ticketRequests[1].getAgeCategory(),
                        returnProcessedTicket.seatNumber())
        );

        List<TicketResultItemDTO> tickets = List.of(
                ticketItemMapper.toDTO(outgoingProcessedTicket.ticket()),
                ticketItemMapper.toDTO(returnProcessedTicket.ticket())
        );
        List<Short> seatNumbers = List.of(outgoingProcessedTicket.seatNumber(), returnProcessedTicket.seatNumber());

        return completeReservationProcess(userId, ticketDetailsList, totalPrice, true, tickets, seatNumbers);
    }

    @Override
    @Transactional
    public void cancelExpiredReservation(Long reservationId) {
        LOGGER.info("Attempting to DELETE expired reservation ID: {}", reservationId);
        Optional<Reservation> reservationOpt = reservationDAO.findById(reservationId);

        if (reservationOpt.isEmpty()) {
            LOGGER.warn("Cannot delete reservation ID: {}. It does not exist.", reservationId);
            return;
        }

        Reservation reservation = reservationOpt.get();
        if (reservation.getReserveStatus() != ReserveStatus.RESERVED) {
            LOGGER.info("Not deleting reservation ID: {}. Its status is now '{}', not 'RESERVED'.", reservationId, reservation.getReserveStatus());
            return;
        }
        int deletedRows = reservationDAO.deleteById(reservationId);

        if (deletedRows > 0) {
            LOGGER.info("Successfully DELETED expired reservation ID: {}.", reservationId);
        } else {
            LOGGER.warn("Failed to DELETE reservation ID: {}. It might have been deleted by another process.", reservationId);
        }
    }

    private ProcessedTicketInfo processSingleTicketLeg(Long tripId, AgeRange ageCategory) {
        Ticket selectedTicket = ticketDAO.findById(tripId, ageCategory)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found for trip ID: " + tripId + " and age: " + ageCategory));

        Trip trip = selectedTicket.getTrip();
        if (trip == null) {
            throw new ReservationFailedException("Trip details not found for the selected ticket (Trip ID: " + tripId + ").");
        }
        BigDecimal price = selectedTicket.getPrice();
        if (price == null) {
            throw new ReservationFailedException("Price not found for the selected ticket (Trip ID: " + tripId + ", Age: " + ageCategory + ").");
        }

        if (trip.getReservedCapacity() >= trip.getTotalCapacity()) {
            throw new TripCapacityExceededException("Trip " + tripId + " is already full (Pre-check).");
        }

        Short seatNumber = generateAvailableSeatNumber(tripId, trip.getTotalCapacity());
        return new ProcessedTicketInfo(selectedTicket, price, seatNumber);
    }

    private ReserveConfirmationDTO completeReservationProcess(
            Long userId,
            List<TicketReserveDetailsDTO> ticketDetailsList,
            BigDecimal totalPrice,
            boolean isRoundTrip,
            List<TicketResultItemDTO> tickets,
            List<Short> seatNumbers) {

        ReservationInputDTO reservationInput = new ReservationInputDTO(userId, isRoundTrip);

        InitialReserveResultDTO daoResult;
        try {
            daoResult = reservationDAO.saveInitialReservation(reservationInput, ticketDetailsList);
        } catch (TripCapacityExceededException e) {
            throw e;
        } catch (Exception e) {
            throw new ReservationPersistenceException("Failed to persist reservation in database.");
        }

        if (daoResult == null) {
            throw new ReservationFailedException("Reservation creation did not return expected results from database.");
        }

        String expiryKey = REDIS_RESERVATION_EXPIRE_PREFIX + daoResult.reservationId();
        redisService.setKeyWithTTL(expiryKey, REDIS_KEY_VALUE, TEN_MINUTES_IN_SECONDS);

        String reminderKey = REDIS_RESERVATION_REMIND_PREFIX + daoResult.reservationId();
        redisService.setKeyWithTTL(reminderKey, REDIS_KEY_VALUE, FIVE_MINUTES_IN_SECONDS);

        return new ReserveConfirmationDTO(
                daoResult.reservationId(),
                daoResult.reservationDatetime(),
                daoResult.expirationDatetime(),
                isRoundTrip,
                tickets,
                seatNumbers,
                totalPrice
        );
    }

    private void validateRoundTripLegs(Ticket outgoingTicket, Ticket returnTicket) {
        Trip outgoingTrip = outgoingTicket.getTrip();
        Trip returnTrip = returnTicket.getTrip();

        if (outgoingTrip == null || returnTrip == null) {
            throw new InvalidRoundTripException("Trip details missing for round trip validation.");
        }
        if (outgoingTrip.getOriginLocationId() == null || outgoingTrip.getDestinationLocationId() == null ||
                returnTrip.getOriginLocationId() == null || returnTrip.getDestinationLocationId() == null) {
            throw new InvalidRoundTripException("Trip location IDs are missing for round trip validation.");
        }
        if (!Objects.equals(outgoingTrip.getOriginLocationId(), returnTrip.getDestinationLocationId())) {
            throw new InvalidRoundTripException("Round trip mismatch: Outgoing trip's origin does not match return trip's destination.");
        }
        if (!Objects.equals(outgoingTrip.getDestinationLocationId(), returnTrip.getOriginLocationId())) {
            throw new InvalidRoundTripException("Round trip mismatch: Outgoing trip's destination does not match return trip's origin.");
        }
        if (returnTrip.getDepartureTimestamp() == null || outgoingTrip.getArrivalTimestamp() == null ||
                !returnTrip.getDepartureTimestamp().isAfter(outgoingTrip.getArrivalTimestamp())) {
            throw new InvalidRoundTripException("Round trip timing issue: Return trip must depart after the outgoing trip arrives.");
        }
    }

    private Short generateAvailableSeatNumber(Long tripId, short totalCapacity) {
        if (totalCapacity <= 0) {
            throw new SeatUnavailableException("No seats available for trip " + tripId + " as total capacity is zero or less.");
        }
        List<Short> takenSeats = reservationDAO.getReservedSeatNumbersForTrip(tripId);
        if (takenSeats.size() >= totalCapacity) {
            throw new TripCapacityExceededException("Trip " + tripId + " is full (Seat generation check).");
        }
        Random random = new Random();
        int attempts = 0;
        final int maxAttempts = Math.max(10, totalCapacity * 2);
        while (attempts < maxAttempts) {
            short potentialSeat = (short) (random.nextInt(totalCapacity) + 1);
            if (!takenSeats.contains(potentialSeat)) {
                return potentialSeat;
            }
            attempts++;
        }
        for (short i = 1; i <= totalCapacity; i++) {
            if (!takenSeats.contains(i)) {
                return i;
            }
        }
        throw new SeatUnavailableException("Could not assign a unique seat for trip " + tripId + " after multiple attempts.");
    }
}
