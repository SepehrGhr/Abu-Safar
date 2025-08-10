package ir.ac.kntu.abusafar.service;

import ir.ac.kntu.abusafar.dto.auth.LoginResponseDTO;
import ir.ac.kntu.abusafar.dto.auth.OtpRequestDTO;
import ir.ac.kntu.abusafar.dto.auth.OtpVerificationRequestDTO;

public interface AuthenticationService {

    String requestOtpForLogin(OtpRequestDTO otpRequestDTO);

    LoginResponseDTO verifyOtpAndLogin(OtpVerificationRequestDTO otpVerificationRequestDTO);
}