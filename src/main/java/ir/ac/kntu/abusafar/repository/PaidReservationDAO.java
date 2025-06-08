package ir.ac.kntu.abusafar.repository;

import ir.ac.kntu.abusafar.dto.reserve_record.RawHistoryRecordDTO;

import java.util.List;

public interface PaidReservationDAO {
    List<RawHistoryRecordDTO> findReservationHistoryByUserId(Long userId);
}
