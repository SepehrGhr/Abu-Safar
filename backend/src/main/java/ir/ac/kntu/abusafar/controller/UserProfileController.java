package ir.ac.kntu.abusafar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.ac.kntu.abusafar.dto.response.BaseResponse;
import ir.ac.kntu.abusafar.dto.user.UserDetailDTO;
import ir.ac.kntu.abusafar.service.UserService;
import ir.ac.kntu.abusafar.util.constants.Routes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Routes.API_KEY + "/profile")
@Tag(name = "User Profile", description = "APIs for managing the authenticated user's profile")
@SecurityRequirement(name = "bearerAuth")
public class UserProfileController {

    private final UserService userService;

    @Autowired
    public UserProfileController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Get Current User's Details",
            description = "Retrieves detailed profile information for the authenticated user, including their email and phone number."
    )
    @ApiResponse(responseCode = "200", description = "User details retrieved successfully")
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<UserDetailDTO>> getCurrentUserInfo(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        UserDetailDTO userDetails = userService.getUserDetails(userId);
        return ResponseEntity.ok(BaseResponse.success(userDetails, "User details retrieved successfully.", 200));
    }
}