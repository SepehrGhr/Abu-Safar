package ir.ac.kntu.abusafar.service;

import ir.ac.kntu.abusafar.dto.User.SignUpRequestDTO;
import ir.ac.kntu.abusafar.model.User;

public interface UserService {
    User signUp(SignUpRequestDTO signUpRequest);
}
