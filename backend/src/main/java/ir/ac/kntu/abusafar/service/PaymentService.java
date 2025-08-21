package ir.ac.kntu.abusafar.service;

import ir.ac.kntu.abusafar.dto.payment.PaymentRecordDTO;
import ir.ac.kntu.abusafar.dto.payment.PaymentRequestDTO;

import java.util.List;

public interface PaymentService {
    PaymentRecordDTO processPayment(Long userId, PaymentRequestDTO paymentRequest);

    List<PaymentRecordDTO> getPaymentHistory(Long userId);
}
