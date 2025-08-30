package ir.ac.kntu.abusafar.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
public class UserInfoDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String city;
    private String userType;
    private LocalDate signUpDate;
    private BigDecimal walletBalance;
    private LocalDate birthdayDate;
}