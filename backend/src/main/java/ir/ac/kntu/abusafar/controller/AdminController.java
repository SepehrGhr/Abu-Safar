package ir.ac.kntu.abusafar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.ac.kntu.abusafar.dto.cancellation.CancellationResponseDTO;
import ir.ac.kntu.abusafar.dto.payment.PaymentRecordDTO;
import ir.ac.kntu.abusafar.dto.report.ReportResponseDTO;
import ir.ac.kntu.abusafar.dto.reservation.EditReservationRequestDTO;
import ir.ac.kntu.abusafar.dto.reserve_record.ReserveRecordItemDTO;
import ir.ac.kntu.abusafar.dto.response.BaseResponse;
import ir.ac.kntu.abusafar.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Management", description = "Endpoints for administrative operations")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @Operation(summary = "Get All Cancelled Reservations", description = "Retrieves a list of all reservations with the status 'CANCELLED'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of cancelled reservations"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not have ADMIN role")
    })
    @GetMapping("/reservations/cancelled")
    public ResponseEntity<BaseResponse<List<ReserveRecordItemDTO>>> getCancelledReservations() {
        List<ReserveRecordItemDTO> cancelled = adminService.getAllCancelledReservations();
        return ResponseEntity.ok(BaseResponse.success(cancelled, "Successfully retrieved all cancelled reservations.", HttpStatus.OK.value()));
    }

    @Operation(summary = "Get Payment Details by ID", description = "Fetches the full details of a single payment record by its unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment details found"),
            @ApiResponse(responseCode = "404", description = "Payment not found with the given ID"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/payments/{paymentId}")
    public ResponseEntity<BaseResponse<PaymentRecordDTO>> getPaymentDetails(
            @Parameter(description = "The unique ID of the payment record.") @PathVariable Long paymentId) {
        return adminService.getPaymentDetails(paymentId)
                .map(payment -> ResponseEntity.ok(BaseResponse.success(payment)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(BaseResponse.fail(null, "Payment not found with ID: " + paymentId, HttpStatus.NOT_FOUND.value())));
    }

    @Operation(summary = "Get All User Reports", description = "Retrieves a list of all reports submitted by users.")
    @GetMapping("/reports")
    public ResponseEntity<BaseResponse<List<ReportResponseDTO>>> getAllReports() {
        List<ReportResponseDTO> reports = adminService.getAllReports();
        return ResponseEntity.ok(BaseResponse.success(reports));
    }

    @Operation(summary = "Get Report by ID", description = "Fetches a single report by its unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report found"),
            @ApiResponse(responseCode = "404", description = "Report not found with the given ID"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/reports/{reportId}")
    public ResponseEntity<BaseResponse<ReportResponseDTO>> getReportById(
            @Parameter(description = "The unique ID of the report.") @PathVariable Long reportId) {
        return adminService.getReportById(reportId)
                .map(report -> ResponseEntity.ok(BaseResponse.success(report)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(BaseResponse.fail(null, "Report not found with ID: " + reportId, HttpStatus.NOT_FOUND.value())));
    }

    @Operation(summary = "Get All Reports for a Specific User", description = "Retrieves all reports submitted by a specific user, identified by their user ID.")
    @GetMapping("/reports/user/{userId}")
    public ResponseEntity<BaseResponse<List<ReportResponseDTO>>> getReportsByUser(
            @Parameter(description = "The unique ID of the user.") @PathVariable Long userId) {
        List<ReportResponseDTO> reports = adminService.getReportsByUserId(userId);
        return ResponseEntity.ok(BaseResponse.success(reports));
    }

    @Operation(summary = "Get Reservation Details by ID", description = "Fetches the detailed information for a specific reservation, including ticket details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation details found"),
            @ApiResponse(responseCode = "404", description = "Reservation not found with the given ID"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/reservations/{reservationId}")
    public ResponseEntity<BaseResponse<List<ReserveRecordItemDTO>>> getReservationById(
            @Parameter(description = "The unique ID of the reservation.") @PathVariable Long reservationId) {
        List<ReserveRecordItemDTO> reservationDetails = adminService.getReservationDetailsById(reservationId);
        return ResponseEntity.ok(BaseResponse.success(reservationDetails));
    }

    @Operation(summary = "Cancel a Reservation (Admin)", description = "Forcefully cancels a reservation on behalf of a user. The logged-in admin's ID is recorded as the canceller.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation successfully cancelled"),
            @ApiResponse(responseCode = "404", description = "Reservation not found with the given ID")
    })
    @PostMapping("/reservations/{reservationId}/cancel")
    public ResponseEntity<BaseResponse<CancellationResponseDTO>> cancelReservation(Authentication authentication,
                                                                                   @Parameter(description = "The unique ID of the reservation to cancel.") @PathVariable Long reservationId) {
        Long adminId = Long.parseLong(authentication.getName());
        CancellationResponseDTO response = adminService.adminCancelReservation(reservationId, adminId);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @Operation(summary = "Change a Passenger's Seat Number", description = "Updates the seat number for a specific ticket leg within a reservation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Seat number updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request (e.g., seat is already taken or out of range)"),
            @ApiResponse(responseCode = "404", description = "The specified reservation or trip leg does not exist")
    })
    @PutMapping("/reservations/change-seat")
    public ResponseEntity<BaseResponse<String>> changeSeatNumber(@Valid @RequestBody EditReservationRequestDTO request) {
        adminService.changeSeatNumber(request);
        return ResponseEntity.ok(BaseResponse.success("Seat number updated successfully.", "Success", HttpStatus.OK.value()));
    }
}