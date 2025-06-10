package ir.ac.kntu.abusafar.repository;

import ir.ac.kntu.abusafar.dto.reserve_record.RawHistoryRecordDTO;
import ir.ac.kntu.abusafar.util.constants.enums.TicketStatus;

import java.util.List;

public interface PaidReservationDAO {
    List<RawHistoryRecordDTO> findReservationHistoryByUserId(Long userId);
    List<RawHistoryRecordDTO> findReservationHistoryByStatus(TicketStatus statusFilter);
}
