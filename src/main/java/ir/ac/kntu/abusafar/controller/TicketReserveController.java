package ir.ac.kntu.abusafar.controller;

import ir.ac.kntu.abusafar.dto.reservation.InitialBookResultDTO;
import ir.ac.kntu.abusafar.dto.response.BaseResponse;
import ir.ac.kntu.abusafar.dto.ticket.TicketSelectRequestDTO;
import ir.ac.kntu.abusafar.service.BookingService;
import ir.ac.kntu.abusafar.util.constants.Routes;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Routes.API_KEY + "/booking")
public class TicketReserveController {
    private final BookingService bookingService;
//    private final PaymentService paymentService;

    @Autowired
    public TicketReserveController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/reserve")
    public ResponseEntity<BaseResponse<InitialBookResultDTO>> initiateReservation(Authentication authentication, @Valid @RequestBody TicketSelectRequestDTO requestDTO) {
        return ResponseEntity.ok(BaseResponse.success(bookingService.createReservation(requestDTO)));
    }

//    @PostMapping("/pay")
//    public ResponseEntity<BaseResponse<String>> initiateReservation(Authentication authentication, @Valid @RequestBody PaymentRequestDTO requestDTO) {
//        String responseMessage = bookingService.createReservation(requestDTO);
//        return ResponseEntity.ok(BaseResponse.success(responseMessage, "Created reservation", HttpStatus.OK.value()));
//    }

}
