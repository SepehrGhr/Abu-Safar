package ir.ac.kntu.abusafar.dto.User;

import java.time.LocalDate;

public class UserInfoDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String city;
    private String userType;
    private LocalDate signUpDate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
    public LocalDate getSignUpDate() { return signUpDate; }
    public void setSignUpDate(LocalDate signUpDate) { this.signUpDate = signUpDate; }
}
