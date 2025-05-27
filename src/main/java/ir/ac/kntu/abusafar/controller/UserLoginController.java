package ir.ac.kntu.abusafar.controller;

import ir.ac.kntu.abusafar.dto.auth.LoginResponseDTO;
import ir.ac.kntu.abusafar.dto.auth.OtpRequestDTO;
import ir.ac.kntu.abusafar.dto.auth.OtpVerificationRequestDTO;
import ir.ac.kntu.abusafar.dto.response.BaseResponse;
import ir.ac.kntu.abusafar.service.AuthenticationService;
import ir.ac.kntu.abusafar.util.constants.Routes;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Routes.API_AUTH)
public class UserLoginController {

    private final AuthenticationService authenticationService;

    @Autowired
    public UserLoginController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login/otp/request")
    public ResponseEntity<BaseResponse<String>> requestOtpForLogin(@Valid @RequestBody OtpRequestDTO otpRequestDTO) {
        String responseMessage = authenticationService.requestOtpForLogin(otpRequestDTO);
        return ResponseEntity.ok(BaseResponse.success(responseMessage, "OTP request processed.", HttpStatus.OK.value()));
    }

    @PostMapping("/login/otp/verify")
    public ResponseEntity<BaseResponse<LoginResponseDTO>> verifyOtpAndLogin(@Valid @RequestBody OtpVerificationRequestDTO otpVerificationRequestDTO) {
        LoginResponseDTO loginResponse = authenticationService.verifyOtpAndLogin(otpVerificationRequestDTO);
        return ResponseEntity.ok(BaseResponse.success(loginResponse));
    }


}
