package ir.ac.kntu.abusafar.controller;

import ir.ac.kntu.abusafar.dto.payment.PaymentRecordDTO;
import ir.ac.kntu.abusafar.dto.report.ReportResponseDTO;
import ir.ac.kntu.abusafar.dto.reserve_record.ReserveRecordItemDTO;
import ir.ac.kntu.abusafar.dto.response.BaseResponse;
import ir.ac.kntu.abusafar.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/reservations/cancelled")
    public ResponseEntity<BaseResponse<List<ReserveRecordItemDTO>>> getCancelledReservations() {
        List<ReserveRecordItemDTO> cancelled = adminService.getAllCancelledReservations();
        return ResponseEntity.ok(BaseResponse.success(cancelled, "Successfully retrieved all cancelled reservations.", HttpStatus.OK.value()));
    }

    @GetMapping("/payments/{paymentId}")
    public ResponseEntity<BaseResponse<PaymentRecordDTO>> getPaymentDetails(@PathVariable Long paymentId) {
        return adminService.getPaymentDetails(paymentId)
                .map(payment -> ResponseEntity.ok(BaseResponse.success(payment)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(BaseResponse.fail(null, "Payment not found with ID: " + paymentId, HttpStatus.NOT_FOUND.value())));
    }

    @GetMapping("/reports")
    public ResponseEntity<BaseResponse<List<ReportResponseDTO>>> getAllReports() {
        List<ReportResponseDTO> reports = adminService.getAllReports();
        return ResponseEntity.ok(BaseResponse.success(reports));
    }

    @GetMapping("/reports/{reportId}")
    public ResponseEntity<BaseResponse<ReportResponseDTO>> getReportById(@PathVariable Long reportId) {
        return adminService.getReportById(reportId)
                .map(report -> ResponseEntity.ok(BaseResponse.success(report)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(BaseResponse.fail(null, "Report not found with ID: " + reportId, HttpStatus.NOT_FOUND.value())));
    }

    @GetMapping("/reports/user/{userId}")
    public ResponseEntity<BaseResponse<List<ReportResponseDTO>>> getReportsByUser(@PathVariable Long userId) {
        List<ReportResponseDTO> reports = adminService.getReportsByUserId(userId);
        return ResponseEntity.ok(BaseResponse.success(reports));
    }
}