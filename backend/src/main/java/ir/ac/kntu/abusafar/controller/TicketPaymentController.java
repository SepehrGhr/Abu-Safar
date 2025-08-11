package ir.ac.kntu.abusafar.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.ac.kntu.abusafar.dto.payment.PaymentRecordDTO;
import ir.ac.kntu.abusafar.dto.payment.PaymentRequestDTO;
import ir.ac.kntu.abusafar.dto.response.BaseResponse;
import ir.ac.kntu.abusafar.service.PaymentService;
import ir.ac.kntu.abusafar.util.constants.Routes;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Routes.API_KEY + "/payment")
@Tag(name = "Payment Processing", description = "APIs for processing payments for reservations")
@SecurityRequirement(name = "bearerAuth")
public class TicketPaymentController {

    private final PaymentService paymentService;

    @Autowired
    public TicketPaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(
            summary = "Process Payment for a Reservation",
            description = "Processes the payment for a reserved booking using the specified payment method (e.g., WALLET, CARD). On success, this finalizes the booking and updates the reservation status to 'PAID'."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment processed successfully"),
            @ApiResponse(responseCode = "400", description = "Payment failed (e.g., reservation expired, already paid, or insufficient wallet balance)"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not own this reservation"),
            @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    @PostMapping("/pay")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<PaymentRecordDTO>> processPayment(Authentication authentication, @Valid @RequestBody PaymentRequestDTO paymentRequest) {
        Long userId = Long.parseLong(authentication.getName());
        PaymentRecordDTO record = paymentService.processPayment(userId, paymentRequest);
        return ResponseEntity.ok(BaseResponse.success(record, "Payment processed successfully.", HttpStatus.OK.value()));
    }
}