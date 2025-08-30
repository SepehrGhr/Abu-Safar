package ir.ac.kntu.abusafar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.ac.kntu.abusafar.dto.response.BaseResponse;
import ir.ac.kntu.abusafar.dto.user.ChargeWalletRequestDTO;
import ir.ac.kntu.abusafar.dto.user.UserInfoDTO;
import ir.ac.kntu.abusafar.service.UserService;
import ir.ac.kntu.abusafar.util.constants.Routes;
import ir.ac.kntu.abusafar.util.constants.enums.ResponseCode;
import ir.ac.kntu.abusafar.util.constants.enums.ResponseMessage;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Routes.API_KEY + "/wallet")
@Tag(name = "User Wallet", description = "APIs for managing the authenticated user's wallet")
@SecurityRequirement(name = "bearerAuth")
public class WalletController {

    private final UserService userService;

    @Autowired
    public WalletController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Charge User Wallet",
            description = "Allows an authenticated user to add funds to their own wallet."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Wallet charged successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid amount provided"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/charge")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<UserInfoDTO>> chargeWallet(Authentication authentication, @Valid @RequestBody ChargeWalletRequestDTO requestDTO) {
        Long userId = Long.parseLong(authentication.getName());
        UserInfoDTO updatedUserInfo = userService.chargeWallet(userId, requestDTO.getAmount());
        return ResponseEntity.ok(BaseResponse.success(updatedUserInfo));
    }
}