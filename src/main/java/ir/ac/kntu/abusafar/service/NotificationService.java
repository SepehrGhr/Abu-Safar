package ir.ac.kntu.abusafar.service;

public interface NotificationService {
    void sendPaymentReminderEmail(Long reservationId);
}
