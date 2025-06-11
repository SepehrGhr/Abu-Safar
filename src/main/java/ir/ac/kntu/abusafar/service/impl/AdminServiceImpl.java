package ir.ac.kntu.abusafar.service.impl;

import ir.ac.kntu.abusafar.dto.cancellation.CancellationResponseDTO;
import ir.ac.kntu.abusafar.dto.location.LocationResponseDTO;
import ir.ac.kntu.abusafar.dto.payment.PaymentRecordDTO;
import ir.ac.kntu.abusafar.dto.report.ReportResponseDTO;
import ir.ac.kntu.abusafar.dto.reservation.EditReservationRequestDTO;
import ir.ac.kntu.abusafar.dto.reserve_record.RawHistoryRecordDTO;
import ir.ac.kntu.abusafar.dto.reserve_record.ReserveRecordItemDTO;
import ir.ac.kntu.abusafar.dto.ticket.TicketResultItemDTO;
import ir.ac.kntu.abusafar.exception.ReservationNotFoundException;
import ir.ac.kntu.abusafar.exception.SeatUnavailableException;
import ir.ac.kntu.abusafar.exception.TripNotFoundException;
import ir.ac.kntu.abusafar.mapper.PaymentMapper;
import ir.ac.kntu.abusafar.mapper.ReportMapper;
import ir.ac.kntu.abusafar.model.Trip;
import ir.ac.kntu.abusafar.repository.*;
import ir.ac.kntu.abusafar.service.AdminService;
import ir.ac.kntu.abusafar.service.CancellationService;
import ir.ac.kntu.abusafar.service.LocationService;
import ir.ac.kntu.abusafar.util.constants.enums.TicketStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private final ReservationDAO reservationDAO;
    private final PaidReservationDAO paidReservationDAO;
    private final LocationService locationService;
    private final PaymentDAO paymentDAO;
    private final ReportDAO reportDAO;
    private final CancellationService cancellationService;
    private final TicketReservationDAO ticketReservationDAO;
    private final TripDAO tripDAO;

    public AdminServiceImpl(ReservationDAO reservationDAO, PaidReservationDAO paidReservationDAO, LocationService locationService, PaymentDAO paymentDAO, ReportDAO reportDAO, CancellationService cancellationService, TicketReservationDAO ticketReservationDAO, TripDAO tripDAO) {
        this.reservationDAO = reservationDAO;
        this.paidReservationDAO = paidReservationDAO;
        this.locationService = locationService;
        this.paymentDAO = paymentDAO;
        this.reportDAO = reportDAO;
        this.cancellationService = cancellationService;
        this.ticketReservationDAO = ticketReservationDAO;
        this.tripDAO = tripDAO;
    }

    @Override
    public List<ReserveRecordItemDTO> getAllCancelledReservations() {
        List<RawHistoryRecordDTO> rawRecords = paidReservationDAO.findReservationHistoryByStatus(TicketStatus.CANCELLED);

        Map<Long, List<RawHistoryRecordDTO>> recordsByReservationId = rawRecords.stream()
                .collect(Collectors.groupingBy(RawHistoryRecordDTO::reservationId));

        List<ReserveRecordItemDTO> consolidatedRecords = new ArrayList<>();
        for (List<RawHistoryRecordDTO> group : recordsByReservationId.values()) {
            consolidatedRecords.add(consolidateRawRecords(group));
        }
        return consolidatedRecords;
    }

    @Override
    public List<ReserveRecordItemDTO> getReservationDetailsById(Long reservationId) {
        List<RawHistoryRecordDTO> rawRecords = paidReservationDAO.findDetailedReservationById(reservationId);
        if (rawRecords.isEmpty()) {
            throw new ReservationNotFoundException("Reservation with ID " + reservationId + " not found.");
        }

        Map<Long, List<RawHistoryRecordDTO>> recordsByReservationId = rawRecords.stream()
                .collect(Collectors.groupingBy(RawHistoryRecordDTO::reservationId));

        return recordsByReservationId.values().stream()
                .map(this::consolidateRawRecords)
                .collect(Collectors.toList());
    }

    private ReserveRecordItemDTO consolidateRawRecords(List<RawHistoryRecordDTO> group) {
        if (group == null || group.isEmpty()) {
            return null;
        }
        RawHistoryRecordDTO firstRecord = group.get(0);

        List<TicketResultItemDTO> tickets = group.stream()
                .map(this::createTicketInfo)
                .collect(Collectors.toList());

        List<Short> seatNumbers = group.stream()
                .map(RawHistoryRecordDTO::seatNumber)
                .collect(Collectors.toList());

        return new ReserveRecordItemDTO(
                firstRecord.calculatedStatus(),
                firstRecord.reservationId(),
                firstRecord.paymentId(),
                firstRecord.paymentTimestamp(),
                seatNumbers,
                firstRecord.isRoundTrip(),
                tickets
        );
    }

    private TicketResultItemDTO createTicketInfo(RawHistoryRecordDTO raw) {
        String originCity = locationService.getLocationById(raw.originLocationId())
                .map(LocationResponseDTO::city)
                .orElse("Unknown");

        String destinationCity = locationService.getLocationById(raw.destinationLocationId())
                .map(LocationResponseDTO::city)
                .orElse("Unknown");

        return new TicketResultItemDTO(
                raw.tripId(),
                raw.ticketAge(),
                originCity,
                destinationCity,
                raw.departureTimestamp(),
                raw.arrivalTimestamp(),
                raw.tripVehicle(),
                raw.ticketPrice(),
                raw.vehicleCompany()
        );
    }

    @Override
    public Optional<PaymentRecordDTO> getPaymentDetails(Long paymentId) {
        return paymentDAO.findById(paymentId)
                .map(PaymentMapper.INSTANCE::toDTO);
    }

    @Override
    public List<ReportResponseDTO> getAllReports() {
        return reportDAO.findAll().stream()
                .map(ReportMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ReportResponseDTO> getReportById(Long reportId) {
        return reportDAO.findById(reportId)
                .map(ReportMapper.INSTANCE::toDTO);
    }

    @Override
    public List<ReportResponseDTO> getReportsByUserId(Long userId) {
        return reportDAO.findAllByUserId(userId).stream()
                .map(ReportMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CancellationResponseDTO adminCancelReservation(Long reservationId, Long adminId) {
        return cancellationService.adminConfirmCancellation(reservationId, adminId);
    }

    @Override
    @Transactional
    public void changeSeatNumber(EditReservationRequestDTO request) {
        Trip trip = tripDAO.findById(request.getTripId())
                .orElseThrow(() -> new TripNotFoundException("Trip with ID " + request.getTripId() + " not found."));

        if (request.getNewSeatNumber() <= 0 || request.getNewSeatNumber() > trip.getTotalCapacity()) {
            throw new IllegalArgumentException("Seat number " + request.getNewSeatNumber() + " is out of range for this trip.");
        }

        List<Short> reservedSeats = reservationDAO.getReservedSeatNumbersForTrip(request.getTripId());
        if (reservedSeats.contains(request.getNewSeatNumber())) {
            throw new SeatUnavailableException("Seat " + request.getNewSeatNumber() + " is already taken on this trip.");
        }

        int updatedRows = ticketReservationDAO.updateSeatNumber(request.getReservationId(), request.getTripId(), request.getNewSeatNumber());

        if (updatedRows == 0) {
            throw new ReservationNotFoundException("Could not find a matching ticket reservation leg to update for reservation ID " + request.getReservationId() + " and trip ID " + request.getTripId());
        }
    }
}
