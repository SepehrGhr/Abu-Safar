package ir.ac.kntu.abusafar.service;

import ir.ac.kntu.abusafar.dto.payment.PaymentRecordDTO;
import ir.ac.kntu.abusafar.dto.payment.PaymentRequestDTO;

public interface PaymentService {
    PaymentRecordDTO processPayment(Long userId, PaymentRequestDTO paymentRequest);
}
