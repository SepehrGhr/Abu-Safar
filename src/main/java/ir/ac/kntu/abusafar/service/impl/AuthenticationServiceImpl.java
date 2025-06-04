package ir.ac.kntu.abusafar.service.impl;

import ir.ac.kntu.abusafar.dto.user.UserInfoDTO;
import ir.ac.kntu.abusafar.dto.auth.LoginResponseDTO;
import ir.ac.kntu.abusafar.dto.auth.OtpRequestDTO;
import ir.ac.kntu.abusafar.dto.auth.OtpVerificationRequestDTO;
import ir.ac.kntu.abusafar.exception.OtpValidationException;
import ir.ac.kntu.abusafar.exception.UserNotFoundException;
import ir.ac.kntu.abusafar.mapper.UserMapper;
import ir.ac.kntu.abusafar.model.User;
import ir.ac.kntu.abusafar.repository.UserDAO;
import ir.ac.kntu.abusafar.security.jwt.JwtTokenProvider;
import ir.ac.kntu.abusafar.service.AuthenticationService;
import ir.ac.kntu.abusafar.service.OtpService;
import ir.ac.kntu.abusafar.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[1-9][0-9\\s().-]{7,20}$");

    private final UserDAO userDAO;
    private final OtpService otpService;
    private final JwtTokenProvider jwtTokenProvider;

    private final UserService userService;

    @Autowired
    public AuthenticationServiceImpl(UserDAO userDAO,
                                     OtpService otpService,
                                     JwtTokenProvider jwtTokenProvider, UserService userService) {
        this.userDAO = userDAO;
        this.otpService = otpService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @Override
    public String requestOtpForLogin(OtpRequestDTO otpRequestDTO) {
        String contactInfo = otpRequestDTO.getContactInfo();
        UserInfoDTO user;
        Optional<UserInfoDTO> userOptional;

        if (isEmail(contactInfo)) {
            userOptional = userService.findByEmail(contactInfo);
        } else if (isPhoneNumber(contactInfo)) {
            userOptional = userService.findByPhoneNumber(contactInfo);
        } else {
            throw new IllegalArgumentException("Invalid contact information format provided: " + contactInfo);
        }

        user = userOptional.orElseThrow(() ->
                new UserNotFoundException("User not found with contact: " + contactInfo)
        );

        otpService.generateAndSendOtp(user, contactInfo);

        LOGGER.info("OTP requested for user ID: {} via contact: {}", user.getId(), contactInfo);
        return "OTP has been sent to the registered contact method (or logged if unavailable/applicable).";
    }

    @Override
    public LoginResponseDTO verifyOtpAndLogin(OtpVerificationRequestDTO otpVerificationRequestDTO) {
        String contactInfo = otpVerificationRequestDTO.getContactInfo();
        String otp = otpVerificationRequestDTO.getOtp();
        User user;
        Optional<User> userOptional;

        if (isEmail(contactInfo)) {
            userOptional = userDAO.findByEmail(contactInfo);
        } else if (isPhoneNumber(contactInfo)) {
            userOptional = userDAO.findByPhoneNumber(contactInfo);
        } else {
            throw new IllegalArgumentException("Invalid contact information format for OTP verification: " + contactInfo);
        }

        user = userOptional.orElseThrow(() ->
                new UserNotFoundException("User not found with contact: " + contactInfo + " for OTP verification.")
        );

        boolean isValidOtp = otpService.validateOtp(contactInfo, otp);

        if (!isValidOtp) {
            throw new OtpValidationException("Invalid or expired OTP for contact: " + contactInfo);
        }

        if (user.getId() == null) {
            LOGGER.error("User ID is null for contact: {}. Cannot generate JWT.", contactInfo);
            throw new OtpValidationException("User ID missing, cannot complete login for contact: " + contactInfo);
        }
        String jwtSubject = user.getId().toString();
        String accessToken = jwtTokenProvider.generateToken(jwtSubject);

        UserInfoDTO userInfoDTO = UserMapper.INSTANCE.toDTO(user);

        LOGGER.info("User (ID: {}) logged in successfully via OTP using contact {}.", user.getId(), contactInfo);
        return new LoginResponseDTO(accessToken, userInfoDTO);
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