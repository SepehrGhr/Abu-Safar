package ir.ac.kntu.abusafar.service.impl;

import ir.ac.kntu.abusafar.dto.location.LocationResponseDTO;
import ir.ac.kntu.abusafar.dto.reserve_record.RawHistoryRecordDTO;
import ir.ac.kntu.abusafar.dto.reserve_record.ReserveRecordItemDTO;
import ir.ac.kntu.abusafar.dto.ticket.TicketResultItemDTO;
import ir.ac.kntu.abusafar.repository.PaidReservationDAO;
import ir.ac.kntu.abusafar.service.BookingHistoryService;
import ir.ac.kntu.abusafar.service.LocationService;
import ir.ac.kntu.abusafar.util.constants.enums.TicketStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingHistoryServiceImpl implements BookingHistoryService {

    private final PaidReservationDAO paidReservationDAO;
    private final LocationService locationService;

    @Autowired
    public BookingHistoryServiceImpl(PaidReservationDAO paidReservationDAO, LocationService locationService) {
        this.paidReservationDAO = paidReservationDAO;
        this.locationService = locationService;
    }

    @Override
    public List<ReserveRecordItemDTO> getReservationHistoryForUser(Long userId, Optional<TicketStatus> statusFilter) {
        List<RawHistoryRecordDTO> rawRecords = paidReservationDAO.findReservationHistoryByUserId(userId);

        Map<Long, List<RawHistoryRecordDTO>> recordsByReservationId = rawRecords.stream()
                .collect(Collectors.groupingBy(RawHistoryRecordDTO::reservationId));

        List<ReserveRecordItemDTO> consolidatedRecords = new ArrayList<>();
        for (List<RawHistoryRecordDTO> group : recordsByReservationId.values()) {
            RawHistoryRecordDTO firstRecord = group.get(0);

            List<TicketResultItemDTO> tickets = group.stream()
                    .map(this::createTicketInfo)
                    .collect(Collectors.toList());

            List<Short> seatNumbers = group.stream()
                    .map(RawHistoryRecordDTO::seatNumber)
                    .collect(Collectors.toList());

            consolidatedRecords.add(new ReserveRecordItemDTO(
                    firstRecord.calculatedStatus(),
                    firstRecord.reservationId(),
                    firstRecord.paymentId(),
                    firstRecord.paymentTimestamp(),
                    seatNumbers,
                    firstRecord.isRoundTrip(),
                    tickets
            ));
        }

        List<ReserveRecordItemDTO> filteredRecords = consolidatedRecords;
        if (statusFilter.isPresent()) {
            TicketStatus filter = statusFilter.get();
            filteredRecords = consolidatedRecords.stream()
                    .filter(record -> record.status() == filter)
                    .collect(Collectors.toList());
        }

        filteredRecords.sort((r1, r2) -> {
            if (r1.paymentTimestamp() == null && r2.paymentTimestamp() == null) return 0;
            if (r1.paymentTimestamp() == null) return 1;
            if (r2.paymentTimestamp() == null) return -1;
            return r2.paymentTimestamp().compareTo(r1.paymentTimestamp());
        });

        return filteredRecords;
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
}
