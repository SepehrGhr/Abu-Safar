package ir.ac.kntu.abusafar.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class OtpVerificationRequestDTO {

    @NotBlank(message = "Contact information cannot be blank.")
    private String contactInfo;

    @NotBlank(message = "OTP cannot be blank.")
    @Size(min = 4, max = 6, message = "OTP length must be between 4 and 6 characters.")
    private String otp;

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}