package ir.ac.kntu.abusafar.controller;

import ir.ac.kntu.abusafar.dto.response.BaseResponse;
import ir.ac.kntu.abusafar.dto.user.UserUpdateRequestDTO;
import ir.ac.kntu.abusafar.dto.user.UserInfoDTO;
import ir.ac.kntu.abusafar.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class UserUpdateController {

    private final UserService userService;

    @Autowired
    public UserUpdateController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/update")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<UserInfoDTO>> updateUserInfo(Authentication authentication, @Valid @RequestBody UserUpdateRequestDTO updateRequest) {
        Long userId = Long.parseLong(authentication.getName());
        UserInfoDTO updatedUser = userService.updateUserInfo(userId, updateRequest);
        return ResponseEntity.ok(BaseResponse.success(updatedUser, "User information updated successfully.", 200));
    }
}