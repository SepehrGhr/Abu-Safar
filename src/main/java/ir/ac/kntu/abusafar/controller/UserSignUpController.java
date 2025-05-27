package ir.ac.kntu.abusafar.controller;

import ir.ac.kntu.abusafar.dto.User.SignUpRequestDTO;
import ir.ac.kntu.abusafar.dto.User.UserInfoDTO;
import ir.ac.kntu.abusafar.dto.response.BaseResponse;
import ir.ac.kntu.abusafar.model.User;
import ir.ac.kntu.abusafar.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class UserSignUpController {

    private final UserService userService;

    @Autowired
    public UserSignUpController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<UserInfoDTO>> signUpUser(@Valid @RequestBody SignUpRequestDTO signUpRequest) {
        return ResponseEntity.status(201).body(BaseResponse.success(userService.signUp(signUpRequest)));
    }

//    private UserInfoDTO convertToUserResponse(User user) {
//        UserResponse response = new UserResponse();
//        response.setId(user.getId());
//        response.setFirstName(user.getFirstName());
//        response.setLastName(user.getLastName());
//        response.setCity(user.getCity());
//        response.setUserType(user.getUserType().name());
//        // We don't want to send email/phone here unless specifically requested,
//        // or retrieve them via userDAO.findContactByUserId if needed.
//        // For now, keeping it simple.
//        if (user.getSignUpDate() != null) {
//            response.setSignUpDate(user.getSignUpDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
//        }
//        return response;
//    }
}
