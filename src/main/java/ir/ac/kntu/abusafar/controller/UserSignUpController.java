package ir.ac.kntu.abusafar.controller;

import ir.ac.kntu.abusafar.dto.User.SignUpRequestDTO;
import ir.ac.kntu.abusafar.dto.User.UserInfoDTO;
import ir.ac.kntu.abusafar.dto.response.BaseResponse;
import ir.ac.kntu.abusafar.service.UserService;
import ir.ac.kntu.abusafar.util.constants.Routes;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Routes.API_AUTH)
public class UserSignUpController {

    private final UserService userService;

    @Autowired
    public UserSignUpController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(Routes.SIGN_UP)
    public ResponseEntity<BaseResponse<UserInfoDTO>> signUpUser(@Valid @RequestBody SignUpRequestDTO signUpRequest) {
        return ResponseEntity.status(201).body(BaseResponse.success(userService.signUp(signUpRequest)));
    }
}
