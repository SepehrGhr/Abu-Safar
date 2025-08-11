package ir.ac.kntu.abusafar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User Authentication", description = "APIs for user sign-up and login")
public class UserLoginController {

    private final AuthenticationService authenticationService;

    @Autowired
    public UserLoginController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Operation(
            summary = "Request OTP for Login",
            description = "Requests a One-Time Password (OTP) to be sent to the user's registered email or phone number for passwordless login."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OTP request processed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid contact information format"),
            @ApiResponse(responseCode = "404", description = "User not found with the given contact information")
    })
    @PostMapping("/login/otp/request")
    public ResponseEntity<BaseResponse<String>> requestOtpForLogin(@Valid @RequestBody OtpRequestDTO otpRequestDTO) {
        String responseMessage = authenticationService.requestOtpForLogin(otpRequestDTO);
        return ResponseEntity.ok(BaseResponse.success(responseMessage, "OTP request processed.", HttpStatus.OK.value()));
    }

    @Operation(
            summary = "Verify OTP and Login",
            description = "Verifies the provided OTP and, if valid, returns a JWT access token and user information to complete the login process."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful, JWT token returned"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired OTP provided"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/login/otp/verify")
    public ResponseEntity<BaseResponse<LoginResponseDTO>> verifyOtpAndLogin(@Valid @RequestBody OtpVerificationRequestDTO otpVerificationRequestDTO) {
        LoginResponseDTO loginResponse = authenticationService.verifyOtpAndLogin(otpVerificationRequestDTO);
        return ResponseEntity.ok(BaseResponse.success(loginResponse));
    }
}