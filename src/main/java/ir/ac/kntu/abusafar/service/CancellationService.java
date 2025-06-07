package ir.ac.kntu.abusafar.service;

import ir.ac.kntu.abusafar.dto.cancellation.CancellationPenaltyResponseDTO;
import ir.ac.kntu.abusafar.dto.cancellation.CancellationResponseDTO;

public interface CancellationService {
    CancellationPenaltyResponseDTO calculatePenalty(Long userId, Long reservationId);
    CancellationResponseDTO confirmCancellation(Long userId, Long reservationId);
}