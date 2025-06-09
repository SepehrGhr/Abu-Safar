package ir.ac.kntu.abusafar.controller;

import ir.ac.kntu.abusafar.dto.reserve_record.ReserveRecordItemDTO;
import ir.ac.kntu.abusafar.dto.response.BaseResponse;
import ir.ac.kntu.abusafar.service.BookingHistoryService;
import ir.ac.kntu.abusafar.util.constants.Routes;
import ir.ac.kntu.abusafar.util.constants.enums.TicketStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(Routes.API_KEY + "/bookings")
public class ReserveHistoryController {

    private final BookingHistoryService bookingHistoryService;

    @Autowired
    public ReserveHistoryController(BookingHistoryService bookingHistoryService) {
        this.bookingHistoryService = bookingHistoryService;
    }

    @GetMapping("/history")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<List<ReserveRecordItemDTO>>> getReservationHistory(Authentication authentication,
            @RequestParam(required = false) Optional<TicketStatus> status) {
        Long userId = Long.parseLong(authentication.getName());

        List<ReserveRecordItemDTO> history = bookingHistoryService.getReservationHistoryForUser(userId, status);
        return ResponseEntity.ok(BaseResponse.success(history));
    }
}
