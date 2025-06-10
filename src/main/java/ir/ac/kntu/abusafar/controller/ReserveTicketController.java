package ir.ac.kntu.abusafar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.ac.kntu.abusafar.dto.reservation.ReserveConfirmationDTO;
import ir.ac.kntu.abusafar.dto.response.BaseResponse;
import ir.ac.kntu.abusafar.dto.ticket.TicketSelectRequestDTO;
import ir.ac.kntu.abusafar.service.BookingService;
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
@Tag(name = "Ticket Reservation", description = "APIs for creating one-way and two-way reservations")
@SecurityRequirement(name = "bearerAuth")
public class ReserveTicketController {
    private final BookingService bookingService;

    @Autowired
    public ReserveTicketController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Operation(
            summary = "Create a One-Way Reservation",
            description = "Initiates a new one-way reservation for a single selected ticket. This holds the ticket for 10 minutes, awaiting payment."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation successfully initiated"),
            @ApiResponse(responseCode = "400", description = "Invalid ticket selection or trip is full"),
            @ApiResponse(responseCode = "404", description = "Ticket or Trip not found")
    })
    @PostMapping("/reserve/one_way")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<ReserveConfirmationDTO>> initiateOneWayReservation(Authentication authentication,
                                                                                          @Valid @RequestBody TicketSelectRequestDTO requestDTO) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(BaseResponse.success(bookingService.createOneWayReservation(userId, requestDTO)));
    }

    @Operation(
            summary = "Create a Two-Way (Round-Trip) Reservation",
            description = "Initiates a new two-way reservation for an outgoing and return ticket. Expects an array of two ticket selections. This holds the tickets for 10 minutes, awaiting payment."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation successfully initiated"),
            @ApiResponse(responseCode = "400", description = "Invalid ticket selections (e.g., return trip is before departure, destinations don't match, or trip is full)"),
            @ApiResponse(responseCode = "404", description = "One or both Tickets/Trips not found")
    })
    @PostMapping("/reserve/two_way")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<ReserveConfirmationDTO>> initiateTwoWayReservation(Authentication authentication,
                                                                                          @Valid @RequestBody TicketSelectRequestDTO[] requestDTO) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(BaseResponse.success(bookingService.createTwoWayReservation(userId, requestDTO)));
    }
}