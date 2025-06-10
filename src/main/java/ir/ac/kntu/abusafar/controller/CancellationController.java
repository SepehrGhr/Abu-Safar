package ir.ac.kntu.abusafar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.ac.kntu.abusafar.dto.cancellation.CancellationPenaltyRequestDTO;
import ir.ac.kntu.abusafar.dto.cancellation.CancellationPenaltyResponseDTO;
import ir.ac.kntu.abusafar.dto.cancellation.CancellationResponseDTO;
import ir.ac.kntu.abusafar.dto.response.BaseResponse;
import ir.ac.kntu.abusafar.service.CancellationService;
import ir.ac.kntu.abusafar.util.constants.Routes;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Routes.API_KEY + "/booking")
@Tag(name = "Booking Cancellation", description = "APIs for calculating penalties and cancelling reservations")
@SecurityRequirement(name = "bearerAuth")
public class CancellationController {

    private final CancellationService cancellationService;

    @Autowired
    public CancellationController(CancellationService cancellationService) {
        this.cancellationService = cancellationService;
    }

    @Operation(
            summary = "Calculate Cancellation Penalty",
            description = "Calculates the penalty fee and total refund amount for cancelling a reservation without actually performing the cancellation. Useful for showing the user the cost beforehand."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully calculated penalty"),
            @ApiResponse(responseCode = "404", description = "Reservation not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User is not authenticated or does not own the reservation")
    })
    @PostMapping("/cancel/calculate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<CancellationPenaltyResponseDTO>> calculateCancellationPenalty(
            Authentication authentication, @Valid @RequestBody CancellationPenaltyRequestDTO requestDTO) {
        Long userId = Long.parseLong(authentication.getName());
        CancellationPenaltyResponseDTO response = cancellationService.calculatePenalty(userId, requestDTO.getReservationId());
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @Operation(
            summary = "Confirm and Process Reservation Cancellation",
            description = "Processes the cancellation of a reservation. This is the final step and is irreversible. It updates the reservation status and refunds the user to their wallet after deducting penalties."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation successfully cancelled"),
            @ApiResponse(responseCode = "404", description = "Reservation not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User is not authenticated or does not own the reservation")
    })
    @PostMapping("/cancel/confirm")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<CancellationResponseDTO>> confirmCancellation(Authentication authentication,
                                                                                     @Valid @RequestBody CancellationPenaltyRequestDTO requestDTO) {
        Long userId = Long.parseLong(authentication.getName());
        CancellationResponseDTO response = cancellationService.confirmCancellation(userId, requestDTO.getReservationId());
        return ResponseEntity.ok(BaseResponse.success(response));
    }
}