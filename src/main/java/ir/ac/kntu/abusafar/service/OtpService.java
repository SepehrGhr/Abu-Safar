package ir.ac.kntu.abusafar.service;

import ir.ac.kntu.abusafar.model.User;

public interface OtpService {

    String generateAndSendOtp(User user, String targetContactInfo);

    boolean validateOtp(String userEmail, String otp);
}