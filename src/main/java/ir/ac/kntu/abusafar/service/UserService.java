package ir.ac.kntu.abusafar.service;

import ir.ac.kntu.abusafar.dto.User.SignUpRequestDTO;
import ir.ac.kntu.abusafar.dto.User.UserInfoDTO;
import ir.ac.kntu.abusafar.model.User;

public interface UserService {

    UserInfoDTO getUserInfoDTO(User user);

    User createUserFromDTO(UserInfoDTO dto);

    UserInfoDTO signUp(SignUpRequestDTO signUpRequest);
}
