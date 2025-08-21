package ir.ac.kntu.abusafar.repository;

import ir.ac.kntu.abusafar.model.Payment;
import ir.ac.kntu.abusafar.util.constants.enums.PaymentStatus;

import java.util.List;
import java.util.Optional;

public interface PaymentDAO {
    Optional<Payment> findById(Long paymentId);
    Optional<Payment> findPendingPayment(Long reservationId);
    int updatePaymentStatus(Long paymentId, PaymentStatus status);
    List<Payment> findByUserId(Long userId);
}
