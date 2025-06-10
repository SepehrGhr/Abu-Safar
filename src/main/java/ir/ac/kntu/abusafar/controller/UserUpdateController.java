package ir.ac.kntu.abusafar.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User Profile", description = "APIs for managing the authenticated user's profile")
@SecurityRequirement(name = "bearerAuth")
public class UserUpdateController {

    private final UserService userService;

    @Autowired
    public UserUpdateController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Update User Profile",
            description = "Allows an authenticated user to update their own profile information, such as name, city, and contact details."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User information updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data format or contact information is already in use by another account")
    })
    @PutMapping("/update")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<UserInfoDTO>> updateUserInfo(Authentication authentication, @Valid @RequestBody UserUpdateRequestDTO updateRequest) {
        Long userId = Long.parseLong(authentication.getName());
        UserInfoDTO updatedUser = userService.updateUserInfo(userId, updateRequest);
        return ResponseEntity.ok(BaseResponse.success(updatedUser, "User information updated successfully.", 200));
    }
}