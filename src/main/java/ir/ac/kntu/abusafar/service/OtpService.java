package ir.ac.kntu.abusafar.service;

import ir.ac.kntu.abusafar.model.User;

public interface OtpService {

    String generateAndSendOtp(ir.ac.kntu.abusafar.dto.user.UserInfoDTO user, String targetContactInfo);

    boolean validateOtp(String userEmail, String otp);
}