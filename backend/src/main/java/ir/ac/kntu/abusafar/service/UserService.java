package ir.ac.kntu.abusafar.service;

import ir.ac.kntu.abusafar.dto.user.UserDetailDTO;
import ir.ac.kntu.abusafar.dto.user.UserUpdateRequestDTO;
import ir.ac.kntu.abusafar.dto.user.SignUpRequestDTO;
import ir.ac.kntu.abusafar.dto.user.UserInfoDTO;
import ir.ac.kntu.abusafar.model.User;

import java.math.BigDecimal;
import java.util.Optional;

public interface UserService {

    UserInfoDTO getUserInfoDTO(User user);

    User createUserFromDTO(UserInfoDTO dto);

    UserInfoDTO signUp(SignUpRequestDTO signUpRequest);

    UserInfoDTO findUserById(Long userId);

    UserInfoDTO updateUserInfo(Long userId, UserUpdateRequestDTO updatedInfo);

    void deleteUser(Long userId);

    Optional<UserInfoDTO> findByEmail(String email);

    Optional<UserInfoDTO> findByPhoneNumber(String phoneNumber);

    void debitFromWallet(Long userId, BigDecimal amount);

    UserInfoDTO chargeWallet(Long userId, BigDecimal amount);

    UserDetailDTO getUserDetails(Long userId);
}
