package ir.ac.kntu.abusafar.controller;

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
public class TicketPaymentController {

    private final PaymentService paymentService;

    @Autowired
    public TicketPaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/pay")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<PaymentRecordDTO>> processPayment(Authentication authentication, @Valid @RequestBody PaymentRequestDTO paymentRequest) {
        Long userId = Long.parseLong(authentication.getName());
        PaymentRecordDTO record = paymentService.processPayment(userId, paymentRequest);
        return ResponseEntity.ok(BaseResponse.success(record, "Payment processed successfully.", HttpStatus.OK.value()));
    }
}
