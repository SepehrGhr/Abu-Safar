package ir.ac.kntu.abusafar.dto.auth;

import jakarta.validation.constraints.NotBlank;

public class OtpRequestDTO {

    @NotBlank(message = "Contact information (email or phone number) cannot be blank.")
    private String contactInfo;

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
}