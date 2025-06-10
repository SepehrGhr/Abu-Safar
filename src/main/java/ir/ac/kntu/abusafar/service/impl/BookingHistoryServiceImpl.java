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

import java.util.List;
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

        List<ReserveRecordItemDTO> allRecords = rawRecords.stream()
                .map(this::mapToReserveRecordItemDTO)
                .collect(Collectors.toList());

        if (statusFilter.isPresent()) {
            return allRecords.stream()
                    .filter(record -> record.status() == statusFilter.get())
                    .collect(Collectors.toList());
        }
        return allRecords;
    }

    private ReserveRecordItemDTO mapToReserveRecordItemDTO(RawHistoryRecordDTO raw) {
        TicketResultItemDTO ticketInfo = createTicketInfo(raw);

        return new ReserveRecordItemDTO(
                raw.calculatedStatus(),
                raw.reservationId(),
                raw.paymentId(),
                raw.paymentTimestamp(),
                raw.seatNumber(),
                raw.isRoundTrip(),
                ticketInfo
        );
    }

    private TicketResultItemDTO createTicketInfo(RawHistoryRecordDTO raw) {
        TicketResultItemDTO ticketInfo = new TicketResultItemDTO();
        ticketInfo.setTripId(raw.tripId());
        ticketInfo.setAge(raw.ticketAge());
        ticketInfo.setDepartureTimestamp(raw.departureTimestamp());
        ticketInfo.setArrivalTimestamp(raw.arrivalTimestamp());
        ticketInfo.setTripVehicle(raw.tripVehicle());
        ticketInfo.setPrice(raw.ticketPrice());
        ticketInfo.setVehicleCompany(raw.vehicleCompany());

        locationService.getLocationById(raw.originLocationId())
                .map(LocationResponseDTO::getCity)
                .ifPresent(ticketInfo::setOriginCity);

        locationService.getLocationById(raw.destinationLocationId())
                .map(LocationResponseDTO::getCity)
                .ifPresent(ticketInfo::setDestinationCity);

        return ticketInfo;
    }
}