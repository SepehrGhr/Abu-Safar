package ir.ac.kntu.abusafar.model;

import ir.ac.kntu.abusafar.util.constants.enums.AccountStatus;
import ir.ac.kntu.abusafar.util.constants.enums.UserType;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

public class User {
    private Long id;
    private String firstName;
    private String lastName;
    private UserType userType;
    private AccountStatus accountStatus;
    private String city;
    private String hashedPassword;
    private LocalDate signUpDate;
    private String profilePicture;

    public User(Long id, String firstName, String lastName, UserType userType, AccountStatus accountStatus, String city, String hashedPassword, LocalDate signUpDate, String profilePicture) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userType = userType;
        this.accountStatus = accountStatus;
        this.city = city;
        this.hashedPassword = hashedPassword;
        this.signUpDate = signUpDate;
        this.profilePicture = profilePicture;
    }

    public User(String firstName, String lastName, UserType userType, AccountStatus accountStatus, String city, String hashedPassword, LocalDate signUpDate){
        this.firstName = firstName;
        this.lastName = lastName;
        this.userType = userType;
        this.accountStatus = accountStatus;
        this.city = city;
        this.hashedPassword = hashedPassword;
        this.signUpDate = signUpDate;
        this.profilePicture = "default.png";
    }

    public User() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public LocalDate getSignUpDate() {
        return signUpDate;
    }

    public void setSignUpDate(LocalDate signUpDate) {
        this.signUpDate = signUpDate;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
