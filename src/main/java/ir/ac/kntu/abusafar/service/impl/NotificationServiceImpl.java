package ir.ac.kntu.abusafar.service.impl;

import ir.ac.kntu.abusafar.exception.NotificationSendException;
import ir.ac.kntu.abusafar.model.Reservation;
import ir.ac.kntu.abusafar.model.UserContact;
import ir.ac.kntu.abusafar.repository.ReservationDAO;
import ir.ac.kntu.abusafar.repository.UserDAO;
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

import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private static final String REMINDER_EMAIL_SUBJECT = "Payment Reminder for Your AbuSafar Reservation";

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine emailTemplateEngine;
    private final ReservationDAO reservationDAO;
    private final UserDAO userDAO;

    @Autowired
    public NotificationServiceImpl(JavaMailSender mailSender,
                                   @Qualifier("emailTemplateEngine") SpringTemplateEngine emailTemplateEngine,
                                   ReservationDAO reservationDAO,
                                   UserDAO userDAO) {
        this.mailSender = mailSender;
        this.emailTemplateEngine = emailTemplateEngine;
        this.reservationDAO = reservationDAO;
        this.userDAO = userDAO;
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

        Optional<UserContact> userContactOpt = userDAO.findContactByUserIdAndType(reservation.getUserId(), ContactType.EMAIL);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm 'on' yyyy-MM-dd");
        String expirationTimeFormatted = reservation.getExpirationDatetime().format(formatter);
        String reminderMessage = String.format(
                "Reminder: Payment for reservation #%d is required before %s.",
                reservation.getReservationId(),
                expirationTimeFormatted
        );

        if (userContactOpt.isEmpty()) {
            LOGGER.warn("Cannot send email reminder for reservation ID: {}. No email found for user ID: {}. Printing to console.", reservationId, reservation.getUserId());
            System.out.println("--- PAYMENT REMINDER ---");
            System.out.println(reminderMessage);
            System.out.println("------------------------");
            return;
        }

        String userEmail = userContactOpt.get().getContactInfo();

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            Context context = new Context();
            context.setVariable("subject", REMINDER_EMAIL_SUBJECT);
            context.setVariable("reservationId", reservation.getReservationId());
            context.setVariable("expirationTime", expirationTimeFormatted);

            String htmlContent = emailTemplateEngine.process("payment-reminder-email", context);

            helper.setTo(userEmail);
            helper.setSubject(REMINDER_EMAIL_SUBJECT);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            LOGGER.info("Payment reminder email sent to {} for reservation ID: {}", userEmail, reservationId);
        } catch (MessagingException | MailException e) {
            LOGGER.error("Error sending payment reminder email for reservation ID {}: {}", reservationId, e.getMessage(), e);
            throw new NotificationSendException("Error sending payment reminder email to " + userEmail, e);
        }
    }
}
