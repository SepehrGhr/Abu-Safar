package ir.ac.kntu.abusafar.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class UserUpdateRequestDTO {

    @Size(max = 100, message = "First name cannot exceed 100 characters")
    private String firstName;

    @Size(max = 100, message = "Last name cannot exceed 100 characters")
    private String lastName;

    @Size(max = 100, message = "City name cannot exceed 100 characters")
    private String city;

    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "^\\+?[1-9][0-9\\s().-]{7,20}$", message = "Invalid phone number format")
    private String phoneNumber;

    @Past(message = "Birth date must be in the past")
    private LocalDate birthdayDate;
}