package ir.ac.kntu.abusafar.service.impl;

import ir.ac.kntu.abusafar.dto.reserve_record.ReserveRecordItemDTO;
import ir.ac.kntu.abusafar.exception.NotificationSendException;
import ir.ac.kntu.abusafar.model.Reservation;
import ir.ac.kntu.abusafar.model.UserContact;
import ir.ac.kntu.abusafar.repository.ReservationDAO;
import ir.ac.kntu.abusafar.repository.UserDAO;
import ir.ac.kntu.abusafar.service.BookingHistoryService;
import ir.ac.kntu.abusafar.service.NotificationService;
import ir.ac.kntu.abusafar.util.constants.enums.ContactType;
import ir.ac.kntu.abusafar.util.constants.enums.ReserveStatus;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine emailTemplateEngine;
    private final UserDAO userDAO;
    private final ReservationDAO reservationDAO;
    private final BookingHistoryService bookingHistoryService;

    @Autowired
    public NotificationServiceImpl(JavaMailSender mailSender,
                                   @Qualifier("emailTemplateEngine") SpringTemplateEngine emailTemplateEngine,
                                   UserDAO userDAO,
                                   ReservationDAO reservationDAO,
                                   BookingHistoryService bookingHistoryService) {
        this.mailSender = mailSender;
        this.emailTemplateEngine = emailTemplateEngine;
        this.userDAO = userDAO;
        this.reservationDAO = reservationDAO;
        this.bookingHistoryService = bookingHistoryService;
    }

    @Override
    public void sendPaymentReminderEmail(Long reservationId) {
        Optional<Reservation> reservationOpt = reservationDAO.findById(reservationId);
        if (reservationOpt.isEmpty()) {
            LOGGER.warn("Attempted to send reminder for non-existent reservation ID: {}", reservationId);
            return;
        }

        Reservation reservation = reservationOpt.get();

        if (reservation.getReserveStatus() != ReserveStatus.RESERVED) {
            LOGGER.info("Skipping reminder for reservation ID: {} as its status is now '{}'", reservationId, reservation.getReserveStatus());
            return;
        }

        Long userId = reservation.getUserId();
        Optional<UserContact> userContactOpt = userDAO.findContactByUserIdAndType(userId, ContactType.EMAIL);

        if (userContactOpt.isEmpty()) {
            LOGGER.warn("Cannot send payment reminder for reservation ID: {}. No email found for user ID: {}.", reservationId, userId);
            return;
        }

        String userEmail = userContactOpt.get().getContactInfo();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm 'on' yyyy-MM-dd");
        String expirationTimeFormatted = reservation.getExpirationDatetime().format(formatter);

        Context context = new Context();
        context.setVariable("reservationId", reservation.getReservationId());
        context.setVariable("expirationTime", expirationTimeFormatted);

        sendEmail(userEmail, "Payment Reminder for Your AbuSafar Reservation", "payment-reminder-email", context);

    }

    @Override
    public void sendBookingConfirmationEmail(Long reservationId) {
        ReserveRecordItemDTO reservationDetails = bookingHistoryService.getReservationDetailsById(reservationId);

        Long userId = getUserIdFromReservation(reservationId);
        if (userId == null) {
            LOGGER.error("CRITICAL: Could not find user for reservation {}. Confirmation email not sent.", reservationId);
            return;
        }

        Optional<UserContact> userContactOpt = userDAO.findContactByUserIdAndType(userId, ContactType.EMAIL);

        if (userContactOpt.isEmpty()) {
            LOGGER.warn("Cannot send booking confirmation for reservation ID: {}. No email found for user ID: {}.", reservationId, userId);
            return;
        }

        String userEmail = userContactOpt.get().getContactInfo();
        Context context = new Context();
        context.setVariable("reservationId", reservationId);
        context.setVariable("tickets", reservationDetails.ticketsInformation());
        context.setVariable("seatNumbers", reservationDetails.seatNumbers());

        sendEmail(userEmail, "Your AbuSafar Booking is Confirmed!", "booking-confirmation-email", context);
    }

    @Override
    public void sendCancellationConfirmationEmail(Long reservationId, BigDecimal refundedAmount, BigDecimal newWalletBalance) {
        Long userId = getUserIdFromReservation(reservationId);
        if(userId == null) return;

        Optional<UserContact> userContactOpt = userDAO.findContactByUserIdAndType(userId, ContactType.EMAIL);

        if (userContactOpt.isEmpty()) {
            LOGGER.warn("Cannot send cancellation confirmation for reservation ID: {}. No email found for user ID: {}.", reservationId, userId);
            return;
        }

        String userEmail = userContactOpt.get().getContactInfo();
        Context context = new Context();
        context.setVariable("reservationId", reservationId);
        context.setVariable("refundedAmount", refundedAmount);
        context.setVariable("newWalletBalance", newWalletBalance);

        sendEmail(userEmail, "Your AbuSafar Reservation has been Cancelled", "cancellation-confirmation-email", context);
    }

    private void sendEmail(String to, String subject, String templateName, Context context) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            String htmlContent = emailTemplateEngine.process(templateName, context);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
            LOGGER.info("Email '{}' sent to {}", subject, to);
        } catch (MessagingException | MailException e) {
            LOGGER.error("Error sending email '{}' to {}: {}", subject, to, e.getMessage(), e);
            throw new NotificationSendException("Error sending email to " + to, e);
        }
    }

    private Long getUserIdFromReservation(Long reservationId) {
        return reservationDAO.findById(reservationId)
                .map(Reservation::getUserId)
                .orElseGet(() -> {
                    LOGGER.warn("Could not determine user ID for reservation {}", reservationId);
                    return null;
                });
    }
}
