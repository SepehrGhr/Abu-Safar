package ir.ac.kntu.abusafar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.ac.kntu.abusafar.dto.user.SignUpRequestDTO;
import ir.ac.kntu.abusafar.dto.user.UserInfoDTO;
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
@Tag(name = "User Authentication", description = "APIs for user sign-up and login")
public class UserSignUpController {

    private final UserService userService;

    @Autowired
    public UserSignUpController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Register a New User",
            description = "Creates a new user account. Requires either an email or a phone number."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User account created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user data provided (e.g., email already exists, invalid password format)")
    })
    @PostMapping(Routes.SIGN_UP)
    public ResponseEntity<BaseResponse<UserInfoDTO>> signUpUser(@Valid @RequestBody SignUpRequestDTO signUpRequest) {
        return ResponseEntity.status(201).body(BaseResponse.success(userService.signUp(signUpRequest)));
    }
}