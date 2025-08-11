package ir.ac.kntu.abusafar.service;

import ir.ac.kntu.abusafar.dto.cancellation.CancellationResponseDTO;
import ir.ac.kntu.abusafar.dto.payment.PaymentRecordDTO;
import ir.ac.kntu.abusafar.dto.report.ReportResponseDTO;
import ir.ac.kntu.abusafar.dto.reservation.EditReservationRequestDTO;
import ir.ac.kntu.abusafar.dto.reservation.ReserveConfirmationDTO;
import ir.ac.kntu.abusafar.dto.reserve_record.ReserveRecordItemDTO;

import java.util.List;
import java.util.Optional;

public interface AdminService {
    List<ReserveRecordItemDTO> getAllCancelledReservations();
    Optional<PaymentRecordDTO> getPaymentDetails(Long paymentId);
    List<ReportResponseDTO> getAllReports();
    Optional<ReportResponseDTO> getReportById(Long reportId);
    List<ReportResponseDTO> getReportsByUserId(Long userId);
    List<ReserveRecordItemDTO> getReservationDetailsById(Long reservationId);
    CancellationResponseDTO adminCancelReservation(Long reservationId, Long adminId);
    void changeSeatNumber(EditReservationRequestDTO request);
}