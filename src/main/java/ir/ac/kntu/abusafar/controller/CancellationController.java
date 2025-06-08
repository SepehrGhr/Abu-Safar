package ir.ac.kntu.abusafar.controller;

import ir.ac.kntu.abusafar.dto.cancellation.CancellationPenaltyRequestDTO;
import ir.ac.kntu.abusafar.dto.cancellation.CancellationPenaltyResponseDTO;
import ir.ac.kntu.abusafar.dto.cancellation.CancellationResponseDTO;
import ir.ac.kntu.abusafar.dto.response.BaseResponse;
import ir.ac.kntu.abusafar.service.CancellationService;
import ir.ac.kntu.abusafar.util.constants.Routes;
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
@RequestMapping(Routes.API_KEY + "/booking")
public class CancellationController {

    private final CancellationService cancellationService;

    @Autowired
    public CancellationController(CancellationService cancellationService) {
        this.cancellationService = cancellationService;
    }

    @PostMapping("/cancel/calculate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<CancellationPenaltyResponseDTO>> calculateCancellationPenalty(
            Authentication authentication, @Valid @RequestBody CancellationPenaltyRequestDTO requestDTO) {
        Long userId = Long.parseLong(authentication.getName());
        CancellationPenaltyResponseDTO response = cancellationService.calculatePenalty(userId, requestDTO.getReservationId());
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @PostMapping("/cancel/confirm")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<CancellationResponseDTO>> confirmCancellation(Authentication authentication,
            @Valid @RequestBody CancellationPenaltyRequestDTO requestDTO) {
        Long userId = Long.parseLong(authentication.getName());
        CancellationResponseDTO response = cancellationService.confirmCancellation(userId, requestDTO.getReservationId());
        return ResponseEntity.ok(BaseResponse.success(response));
    }
}