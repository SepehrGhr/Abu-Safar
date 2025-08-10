package ir.ac.kntu.abusafar.service;

import ir.ac.kntu.abusafar.dto.reserve_record.ReserveRecordItemDTO;
import ir.ac.kntu.abusafar.util.constants.enums.TicketStatus;

import java.util.List;
import java.util.Optional;

public interface BookingHistoryService {
    List<ReserveRecordItemDTO> getReservationHistoryForUser(Long userId, Optional<TicketStatus> statusFilter);
    ReserveRecordItemDTO getReservationDetailsById(Long reservationId);
}
