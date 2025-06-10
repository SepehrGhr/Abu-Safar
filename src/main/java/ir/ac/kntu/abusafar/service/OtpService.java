package ir.ac.kntu.abusafar.service;

import ir.ac.kntu.abusafar.dto.user.UserInfoDTO;
public interface OtpService {

    String generateAndSendOtp(UserInfoDTO user, String targetContactInfo);

    boolean validateOtp(String userEmail, String otp);
}