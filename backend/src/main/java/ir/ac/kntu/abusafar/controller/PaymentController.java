package ir.ac.kntu.abusafar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.ac.kntu.abusafar.dto.payment.PaymentRecordDTO;
import ir.ac.kntu.abusafar.dto.response.BaseResponse;
import ir.ac.kntu.abusafar.service.PaymentService;
import ir.ac.kntu.abusafar.util.constants.Routes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(Routes.API_KEY + "/payments")
@Tag(name = "Payment History", description = "APIs for retrieving user payment information")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(
            summary = "Get Payment History",
            description = "Retrieves a list of all payments made by the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved payment history"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<List<PaymentRecordDTO>>> getPaymentHistory(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        List<PaymentRecordDTO> paymentHistory = paymentService.getPaymentHistory(userId);
        return ResponseEntity.ok(BaseResponse.success(paymentHistory, "Payment history retrieved successfully.", 200));
    }
}