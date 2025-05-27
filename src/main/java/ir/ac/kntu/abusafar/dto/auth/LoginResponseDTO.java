package ir.ac.kntu.abusafar.dto.auth;

import ir.ac.kntu.abusafar.dto.User.UserInfoDTO;

public class LoginResponseDTO {

    private String accessToken;
    private String tokenType = "Bearer";
    private UserInfoDTO user;

    public LoginResponseDTO(String accessToken, UserInfoDTO user) {
        this.accessToken = accessToken;
        this.user = user;
    }

    public LoginResponseDTO(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public UserInfoDTO getUser() {
        return user;
    }

    public void setUser(UserInfoDTO user) {
        this.user = user;
    }
}