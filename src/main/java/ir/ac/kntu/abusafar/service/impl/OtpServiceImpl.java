package ir.ac.kntu.abusafar.service.impl;

import ir.ac.kntu.abusafar.exception.NotificationSendException;
import ir.ac.kntu.abusafar.model.User;
import ir.ac.kntu.abusafar.model.UserContact;
import ir.ac.kntu.abusafar.repository.UserDAO;
import ir.ac.kntu.abusafar.service.OtpService;
import ir.ac.kntu.abusafar.util.constants.Strings;
import ir.ac.kntu.abusafar.util.constants.enums.ContactType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.regex.Pattern;

//@TODO comments
@Service
public class OtpServiceImpl implements OtpService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OtpServiceImpl.class);

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
    );
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[1-9][0-9\\s().-]{7,20}$");

    private final JedisPool jedisPool;
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine emailTemplateEngine;
    private final UserDAO userDAO;

    @Value("${jwt.otp.cache.duration.ms}")
    private long otpCacheDurationMs;

    @Value("${otp.length}")
    private int otpLength;

    @Value("${otp.email.subject}")
    private String emailSubject;

    // emailTextFormat is not directly used in sendOtpViaEmail with HTML template, but kept for consistency
    @Value("${otp.email.text}")
    private String emailTextFormat;


    @Autowired
    public OtpServiceImpl(JedisPool jedisPool, JavaMailSender mailSender, @Qualifier("emailTemplateEngine") SpringTemplateEngine emailTemplateEngine, UserDAO userDAO) {
        this.jedisPool = jedisPool;
        this.mailSender = mailSender;
        this.emailTemplateEngine = emailTemplateEngine;
        this.userDAO = userDAO;
    }

    @Override
    public String generateAndSendOtp(ir.ac.kntu.abusafar.dto.user.UserInfoDTO user, String targetContactInfo) {
        String otp = generateRandomOtp();
        long otpCacheDurationSeconds = otpCacheDurationMs / 1000;

        // Use targetContactInfo directly as the primary part of the Redis key
        String redisKey = Strings.OTP_REDIS_PREFIX + targetContactInfo;

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(redisKey, otpCacheDurationSeconds, otp);
            LOGGER.info("OTP for contact {} stored in Redis with key {} and TTL {}s", targetContactInfo, redisKey, otpCacheDurationSeconds);
        } catch (JedisException e) {
            LOGGER.error("Failed to store OTP in Redis for contact {}. Error: {}", targetContactInfo, e.getMessage(), e);
            throw new NotificationSendException("Failed to store OTP in Redis for " + targetContactInfo, e);
        }

        // Determine email address for sending notification
        String emailForNotification = null;
        if (isEmail(targetContactInfo)) {
            emailForNotification = targetContactInfo;
        } else if (isPhoneNumber(targetContactInfo)) {
            // If OTP target is a phone number, try to find user's primary email to send the notification
            Optional<UserContact> primaryEmailContact = userDAO.findContactByUserIdAndType(user.getId(), ContactType.EMAIL);
            if (primaryEmailContact.isPresent()) {
                emailForNotification = primaryEmailContact.get().getContactInfo();
                LOGGER.info("OTP requested for phone {}, notification will be sent to primary email {}", targetContactInfo, emailForNotification);
            } else {
                LOGGER.warn("OTP requested for phone {} for user ID {}, but no primary email found for notification. OTP is in Redis.", targetContactInfo, user.getId());
            }
        } else {
            // Unidentified contact type, try to find primary email anyway for notification if possible
            LOGGER.warn("Target contact info {} was not identified as email/phone. Attempting to find primary email for user ID {}.", targetContactInfo, user.getId());
            Optional<UserContact> primaryEmailContact = userDAO.findContactByUserIdAndType(user.getId(), ContactType.EMAIL);
            if (primaryEmailContact.isPresent()) {
                emailForNotification = primaryEmailContact.get().getContactInfo();
            } else {
                LOGGER.warn("No primary email found for user ID {} to send notification for target contact {}.", user.getId(), targetContactInfo);
            }
        }

        if (emailForNotification != null) {
            try {
                sendOtpViaEmail(emailForNotification, otp); // Send actual email
            } catch (Exception e) {
                // Log error but don't fail the whole process if OTP is already in Redis
                LOGGER.error("Failed to send OTP email to {}. OTP for contact {}: {}. Error: {}", emailForNotification, targetContactInfo, otp, e.getMessage(), e);
                // Depending on policy, you might re-throw or handle.
                // For now, we assume OTP in Redis is the critical part.
                // throw new NotificationSendException("Failed to send OTP email to " + emailForNotification, e);
            }
        } else {
            LOGGER.info("OTP for contact {} generated and stored, but no email address found for sending notification.", targetContactInfo);
        }

        // Log the OTP for practice/testing if no email was found or if it's a phone number without email.
        // This logging was part of the original logic for non-email scenarios.
        if (emailForNotification == null && isPhoneNumber(targetContactInfo)) {
            LOGGER.info("Practice Project OTP for user ID {} (contact {}): {}", user.getId(), targetContactInfo, otp);
        }


        return otp;
    }


    @Override
    public boolean validateOtp(String contactInfo, String otp) { // Parameter renamed from userEmail to contactInfo
        String redisKey = Strings.OTP_REDIS_PREFIX + contactInfo;
        String storedOtp = null;

        try (Jedis jedis = jedisPool.getResource()) {
            storedOtp = jedis.get(redisKey);
            if (otp != null && otp.equals(storedOtp)) {
                jedis.del(redisKey); // OTP is single-use
                LOGGER.info("OTP validation successful for contact {}", contactInfo);
                return true;
            }
        } catch (JedisException e) {
            LOGGER.error("Redis error during OTP validation for contact {}. Error: {}", contactInfo, e.getMessage(), e);
            return false; // Treat Redis error as validation failure
        }

        LOGGER.warn("OTP validation failed for contact {}. Provided OTP: {}, Stored OTP: {}", contactInfo, otp, storedOtp);
        return false;
    }

    // findPrimaryEmailForUser is not directly used in the revised generateAndSendOtp logic's main flow
    // but kept as it might be useful for other purposes or if sendOtpViaEmail needs it explicitly.
    // However, sendOtpViaEmail directly receives the email to send to.
    private String findPrimaryEmailForUser(User user) {
        Optional<UserContact> emailContact = userDAO.findContactByUserIdAndType(user.getId(), ContactType.EMAIL);
        if (emailContact.isPresent()) {
            return emailContact.get().getContactInfo();
        }
        LOGGER.warn("No email contact found for user ID: {}", user.getId());
        return null;
    }

    private void sendOtpViaEmail(String email, String otp) {
        // This method remains largely the same as it sends the email to the provided 'email' address.
        // It uses the Thymeleaf template "otp-email.html"
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            Context context = new Context();
            context.setVariable("subject", emailSubject);
            context.setVariable("otpCode", otp);
            long validityMinutes = otpCacheDurationMs / (1000 * 60);
            context.setVariable("validityMessage", "This OTP is valid for " + validityMinutes + " minutes.");


            String htmlContent = emailTemplateEngine.process("otp-email", context); // Assuming template name is "otp-email"

            helper.setTo(email);
            helper.setSubject(emailSubject);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            LOGGER.info("Styled OTP HTML email sent to {}", email);

        } catch (MessagingException e) {
            LOGGER.error("Error creating or sending styled OTP HTML email to {}: {}", email, e.getMessage(), e);
            throw new NotificationSendException("Error sending styled OTP HTML email to " + email, e);
        } catch (MailException e) {
            LOGGER.error("General mail error sending styled OTP HTML email to {}: {}", email, e.getMessage(), e);
            throw new NotificationSendException("Error sending styled OTP HTML email to " + email, e);
        }
    }

    private String generateRandomOtp() {
        SecureRandom random = new SecureRandom();
        StringBuilder otpBuilder = new StringBuilder(otpLength);
        for (int i = 0; i < otpLength; i++) {
            otpBuilder.append(random.nextInt(10));
        }
        return otpBuilder.toString();
    }

    private boolean isEmail(String contactInfo) {
        if (contactInfo == null) return false;
        return EMAIL_PATTERN.matcher(contactInfo).matches();
    }

    private boolean isPhoneNumber(String contactInfo) {
        if (contactInfo == null) return false;
        return PHONE_PATTERN.matcher(contactInfo).matches();
    }
}