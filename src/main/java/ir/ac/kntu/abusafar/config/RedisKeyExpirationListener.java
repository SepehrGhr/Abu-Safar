package ir.ac.kntu.abusafar.config;

import ir.ac.kntu.abusafar.service.BookingService;
import ir.ac.kntu.abusafar.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class RedisKeyExpirationListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisKeyExpirationListener.class);

    private static final String EXPIRE_PREFIX = "reservation:expire:";
    private static final String REMIND_PREFIX = "reservation:remind:";

    private final BookingService bookingService;
    private final NotificationService notificationService;

    @Autowired
    public RedisKeyExpirationListener(BookingService bookingService, NotificationService notificationService) {
        this.bookingService = bookingService;
        this.notificationService = notificationService;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = new String(message.getBody());
        LOGGER.info("Received expired key event from Redis: {}", expiredKey);

        if (expiredKey.startsWith(EXPIRE_PREFIX)) {
            handleReservationExpiry(expiredKey);
        } else if (expiredKey.startsWith(REMIND_PREFIX)) {
            handleReminderExpiry(expiredKey);
        }
    }

    private void handleReservationExpiry(String expiredKey) {
        try {
            Long reservationId = Long.parseLong(expiredKey.substring(EXPIRE_PREFIX.length()));
            LOGGER.info("Handling 10-minute expiry for reservation ID: {}", reservationId);
            bookingService.cancelExpiredReservation(reservationId);

        } catch (NumberFormatException e) {
            LOGGER.error("Could not parse reservation ID from expired key: {}", expiredKey, e);
        } catch (Exception e) {
            LOGGER.error("Error handling expired reservation for key: {}. Error: {}", expiredKey, e.getMessage(), e);
        }
    }

    private void handleReminderExpiry(String expiredKey) {
        try {
            Long reservationId = Long.parseLong(expiredKey.substring(REMIND_PREFIX.length()));
            LOGGER.info("Handling 5-minute reminder for reservation ID: {}", reservationId);

            notificationService.sendPaymentReminderEmail(reservationId);

        } catch (NumberFormatException e) {
            LOGGER.error("Could not parse reservation ID from reminder key: {}", expiredKey, e);
        } catch (Exception e) {
            LOGGER.error("Error sending payment reminder for key: {}. Error: {}", expiredKey, e.getMessage(), e);
        }
    }
}
