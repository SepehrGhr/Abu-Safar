package ir.ac.kntu.abusafar.service;

import java.math.BigDecimal;

public interface NotificationService {
    void sendPaymentReminderEmail(Long reservationId);
    void sendBookingConfirmationEmail(Long reservationId);
    void sendCancellationConfirmationEmail(Long reservationId, BigDecimal refundedAmount, BigDecimal newWalletBalance);
}
