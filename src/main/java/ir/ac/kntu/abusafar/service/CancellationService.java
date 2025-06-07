package ir.ac.kntu.abusafar.service;

import ir.ac.kntu.abusafar.dto.cancellation.CancellationPenaltyResponseDTO;

public interface CancellationService {
    CancellationPenaltyResponseDTO calculatePenalty(Long userId, Long reservationId, Long tripId);
}