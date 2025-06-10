package ir.ac.kntu.abusafar.service.impl;

import ir.ac.kntu.abusafar.dto.user.UserInfoDTO;
import ir.ac.kntu.abusafar.exception.NotificationSendException;
import ir.ac.kntu.abusafar.model.User;
import ir.ac.kntu.abusafar.model.UserContact;
import ir.ac.kntu.abusafar.repository.UserDAO;
import ir.ac.kntu.abusafar.service.OtpService;
import ir.ac.kntu.abusafar.util.constants.Strings;
import ir.ac.kntu.abusafar.util.constants.enums.ContactType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import okhttp3.*;
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

import java.io.IOException;
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

    private final OkHttpClient httpClient;
    private final UserDAO userDAO;

    @Value("${jwt.otp.cache.duration.ms}")
    private long otpCacheDurationMs;

    @Value("${otp.length}")
    private int otpLength;

    @Value("${otp.email.subject}")
    private String emailSubject;

    @Value("${otp.email.text}")
    private String emailTextFormat;

    @Value("${sms.api.url}")
    private String smsApiUrl;

    @Value("${sms.api.key}")
    private String smsApiKey;

    @Value("${sms.api.templateId}")
    private String smsApiTemplateId;


    @Autowired
    public OtpServiceImpl(JedisPool jedisPool, JavaMailSender mailSender, @Qualifier("emailTemplateEngine") SpringTemplateEngine emailTemplateEngine, OkHttpClient httpClient, UserDAO userDAO) {
        this.jedisPool = jedisPool;
        this.mailSender = mailSender;
        this.emailTemplateEngine = emailTemplateEngine;
        this.httpClient = httpClient;
        this.userDAO = userDAO;
    }

    @Override
    public String generateAndSendOtp(UserInfoDTO user, String targetContactInfo) {
        String otp = generateRandomOtp();
        long otpCacheDurationSeconds = otpCacheDurationMs / 1000;
        String redisKey = Strings.OTP_REDIS_PREFIX + targetContactInfo;

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(redisKey, otpCacheDurationSeconds, otp);
            LOGGER.info("OTP for contact {} stored in Redis with key {} and TTL {}s", targetContactInfo, redisKey, otpCacheDurationSeconds);
        } catch (JedisException e) {
            LOGGER.error("Failed to store OTP in Redis for contact {}. Error: {}", targetContactInfo, e.getMessage(), e);
            throw new NotificationSendException("Failed to store OTP in Redis for " + targetContactInfo, e);
        }

        if (isEmail(targetContactInfo)) {
            sendOtpViaEmail(targetContactInfo, otp);
        } else if (isPhoneNumber(targetContactInfo)) {
            sendOtpViaSms(targetContactInfo, otp);
        } else {
            LOGGER.warn("Unidentified contact type: {}. Cannot send OTP.", targetContactInfo);
        }

        return otp;
    }


    @Override
    public boolean validateOtp(String contactInfo, String otp) {
        String redisKey = Strings.OTP_REDIS_PREFIX + contactInfo;
        String storedOtp;

        try (Jedis jedis = jedisPool.getResource()) {
            storedOtp = jedis.get(redisKey);
            if (otp != null && otp.equals(storedOtp)) {
                jedis.del(redisKey);
                LOGGER.info("OTP validation successful for contact {}", contactInfo);
                return true;
            }
        } catch (JedisException e) {
            LOGGER.error("Redis error during OTP validation for contact {}. Error: {}", contactInfo, e.getMessage(), e);
            return false;
        }

        LOGGER.warn("OTP validation failed for contact {}. Provided OTP: {}, Stored OTP: {}", contactInfo, otp, storedOtp);
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

    private void sendOtpViaSms(String phoneNumber, String otp) {
        MediaType mediaType = MediaType.parse("application/json");

        String jsonBody = String.format(
                "{\"mobile\": \"%s\", \"templateId\": %s, \"parameters\": [{\"name\": \"Code\", \"value\": \"%s\"}]}",
                phoneNumber, smsApiTemplateId, otp
        );

        RequestBody body = RequestBody.create(jsonBody, mediaType);
        Request request = new Request.Builder()
                .url(smsApiUrl)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "text/plain")
                .addHeader("x-api-key", smsApiKey)
                .build();

        try {
            Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                LOGGER.info("Successfully sent OTP : {} SMS to phone number: {} {} {}", otp, phoneNumber, response.code(), response.body().string());
            } else {
                LOGGER.error("Failed to send OTP SMS to {}. Status: {}, Body: {}", phoneNumber, response.code(), response.body() != null ? response.body().string() : "null");
                throw new NotificationSendException("Failed to send OTP via SMS.");
            }
            if (response.body() != null) {
                response.body().close();
            }
        } catch (IOException e) {
            LOGGER.error("IOException while sending OTP SMS to {}: {}", phoneNumber, e.getMessage());
            throw new NotificationSendException("Error sending OTP SMS.", e);
        }
    }

    private void sendOtpViaEmail(String email, String otp) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            Context context = new Context();
            context.setVariable("subject", emailSubject);
            context.setVariable("otpCode", otp);
            long validityMinutes = otpCacheDurationMs / (1000 * 60);
            context.setVariable("validityMessage", "This OTP is valid for " + validityMinutes + " minutes.");


            String htmlContent = emailTemplateEngine.process("otp-email", context);

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