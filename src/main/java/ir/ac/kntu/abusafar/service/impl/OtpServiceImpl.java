package ir.ac.kntu.abusafar.service.impl;

import ir.ac.kntu.abusafar.exception.NotificationSendException;
import ir.ac.kntu.abusafar.model.User;
import ir.ac.kntu.abusafar.model.UserContact;
import ir.ac.kntu.abusafar.repository.UserDAO;
import ir.ac.kntu.abusafar.service.OtpService;
import ir.ac.kntu.abusafar.util.constants.Strings;
import ir.ac.kntu.abusafar.util.constants.enums.ContactType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class OtpServiceImpl implements OtpService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OtpServiceImpl.class);

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
    );
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[1-9][0-9\\s().-]{7,20}$");

    private final JedisPool jedisPool;
    private final JavaMailSender mailSender;
    private final UserDAO userDAO;

    @Value("${jwt.otp.cache.duration.ms}")
    private long otpCacheDurationMs;

    @Value("${otp.length}")
    private int otpLength;

    @Value("${otp.email.subject}")
    private String emailSubject;

    @Value("${otp.email.text}")
    private String emailTextFormat;

    @Autowired
    public OtpServiceImpl(JedisPool jedisPool, JavaMailSender mailSender, UserDAO userDAO) {
        this.jedisPool = jedisPool;
        this.mailSender = mailSender;
        this.userDAO = userDAO;
    }

    @Override
    public String generateAndSendOtp(User user, String targetContactInfo) {
        String otp = generateRandomOtp();
        String primaryEmail = findPrimaryEmailForUser(user);
        long otpCacheDurationSeconds = otpCacheDurationMs / 1000;

        if (primaryEmail == null) {
            if (isEmail(targetContactInfo)) {
                primaryEmail = targetContactInfo;
            } else {
                LOGGER.warn("User with ID {} has no primary email for OTP. OTP will be logged only for contact: {}", user.getId(), targetContactInfo);
                LOGGER.info("Practice Project OTP for user ID {}: {} (intended for {})", user.getId(), otp, targetContactInfo);
                String redisKey = Strings.OTP_REDIS_PREFIX + "user:" + user.getId() + ":" + targetContactInfo;
                try (Jedis jedis = jedisPool.getResource()) {
                    jedis.setex(redisKey, otpCacheDurationSeconds, otp);
                } catch (JedisException e) {
                    LOGGER.error("Failed to store OTP in Redis for {}. Error: {}", redisKey, e.getMessage(), e);
                }
                return otp;
            }
        }

        String redisKey = Strings.OTP_REDIS_PREFIX + primaryEmail;
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(redisKey, otpCacheDurationSeconds, otp);
            LOGGER.info("OTP for {} stored in Redis with TTL {}s", primaryEmail, otpCacheDurationSeconds);
        } catch (JedisException e) {
            LOGGER.error("Failed to store OTP in Redis for {}. Error: {}", primaryEmail, e.getMessage(), e);
            // Handle the exception (e.g., throw a custom exception or return an error state)
            // For now, rethrowing as a runtime exception or a specific application exception
            throw new NotificationSendException("Failed to store OTP in Redis for " + primaryEmail, e);
        }


        try {
            if (isEmail(targetContactInfo)) {
                sendOtpViaEmail(targetContactInfo, otp);
            } else if (isPhoneNumber(targetContactInfo)) {
                sendOtpViaEmail(primaryEmail, otp);
                LOGGER.info("OTP sent to primary email {} for user identified by phone {}", primaryEmail, targetContactInfo);
            } else {
                sendOtpViaEmail(primaryEmail, otp);
                LOGGER.warn("Target contact info {} was not identified as email/phone, sent OTP to primary email {}", targetContactInfo, primaryEmail);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to send OTP to {}. OTP: {}. Error: {}", primaryEmail, otp, e.getMessage(), e);
            // Note: OTP is in Redis. Consider if it should be removed on send failure.
            // For this example, we'll keep it, matching original logic.
            throw new NotificationSendException("Failed to send OTP to " + primaryEmail, e);
        }
        return otp;
    }


    @Override
    public boolean validateOtp(String userEmail, String otp) {
        String redisKey = Strings.OTP_REDIS_PREFIX + userEmail;
        String storedOtp = null;

        try (Jedis jedis = jedisPool.getResource()) {
            storedOtp = jedis.get(redisKey);
            if (otp != null && otp.equals(storedOtp)) {
                jedis.del(redisKey); // OTP is single-use
                LOGGER.info("OTP validation successful for {}", userEmail);
                return true;
            }
        } catch (JedisException e) {
            LOGGER.error("Redis error during OTP validation for {}. Error: {}", userEmail, e.getMessage(), e);
            // Decide how to handle Redis unavailability during validation.
            // For now, treat as validation failure.
            return false;
        }

        LOGGER.warn("OTP validation failed for {}. Provided OTP: {}, Stored OTP: {}", userEmail, otp, storedOtp);
        return false;
    }

    private String findPrimaryEmailForUser(User user) {
        Optional<UserContact> emailContact = userDAO.findContactByUserIdAndType(user.getId(), ContactType.EMAIL);
        if (emailContact.isPresent()) {
            return emailContact.get().getContactInfo();
        }
        LOGGER.warn("No email contact found for user ID: {}", user.getId());
        return null;
    }


    private void sendOtpViaEmail(String email, String otp) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(email);
            mailMessage.setSubject(emailSubject);
            mailMessage.setText(String.format(emailTextFormat, otp));
            mailSender.send(mailMessage);
            LOGGER.info("OTP email sent to {}", email);
        } catch (MailException e) {
            LOGGER.error("Error sending OTP email to {}: {}", email, e.getMessage(), e);
            throw new NotificationSendException("Error sending OTP email to " + email, e);
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