package ir.ac.kntu.abusafar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.ac.kntu.abusafar.dto.reserve_record.ReserveRecordItemDTO;
import ir.ac.kntu.abusafar.dto.response.BaseResponse;
import ir.ac.kntu.abusafar.service.BookingHistoryService;
import ir.ac.kntu.abusafar.util.constants.Routes;
import ir.ac.kntu.abusafar.util.constants.enums.TicketStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(Routes.API_KEY + "/bookings")
@Tag(name = "Booking History", description = "APIs for viewing user reservation history")
@SecurityRequirement(name = "bearerAuth")
public class ReserveHistoryController {

    private final BookingHistoryService bookingHistoryService;

    @Autowired
    public ReserveHistoryController(BookingHistoryService bookingHistoryService) {
        this.bookingHistoryService = bookingHistoryService;
    }

    @Operation(
            summary = "Get User's Reservation History",
            description = "Retrieves a list of all reservations made by the currently authenticated user. Can be filtered by status."
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved reservation history")
    @GetMapping("/history")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<List<ReserveRecordItemDTO>>> getReservationHistory(
            Authentication authentication,
            @Parameter(description = "Optional filter to get reservations by a specific status (e.g., UPCOMING_TRIP, PAST_TRIP, CANCELLED).")
            @RequestParam(required = false) Optional<TicketStatus> status) {
        Long userId = Long.parseLong(authentication.getName());

        List<ReserveRecordItemDTO> history = bookingHistoryService.getReservationHistoryForUser(userId, status);
        return ResponseEntity.ok(BaseResponse.success(history));
    }
}