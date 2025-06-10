package ir.ac.kntu.abusafar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.ac.kntu.abusafar.dto.response.BaseResponse;
import ir.ac.kntu.abusafar.dto.ticket.TicketResultDetailsDTO;
import ir.ac.kntu.abusafar.dto.ticket.TicketResultItemDTO;
import ir.ac.kntu.abusafar.dto.ticket.TicketSearchRequestDTO;
import ir.ac.kntu.abusafar.dto.ticket.TicketSelectRequestDTO;
import ir.ac.kntu.abusafar.exception.LocationNotFoundException;
import ir.ac.kntu.abusafar.service.TicketSearchService;
import ir.ac.kntu.abusafar.util.constants.Routes;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(Routes.API_KEY + "/tickets")
@Tag(name = "Ticket Search", description = "Public APIs for searching and selecting tickets")
public class TicketSearchController {
    private final TicketSearchService ticketSearchService;

    @Autowired
    public TicketSearchController(TicketSearchService ticketSearchService) {
        this.ticketSearchService = ticketSearchService;
    }

    @Operation(
            summary = "Search for available tickets",
            description = "Public endpoint to search for one-way tickets based on criteria like origin, destination, date, and vehicle type. Authentication is not required."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved a list of tickets (can be empty if no matching trips or locations are found)"),
            @ApiResponse(responseCode = "400", description = "Invalid search criteria provided")
    })
    @PostMapping("/search")
    public ResponseEntity<BaseResponse<?>> findTickets(@Valid @RequestBody TicketSearchRequestDTO requestDTO){
        try{
            List<TicketResultItemDTO> results = ticketSearchService.searchTickets(requestDTO);
            return ResponseEntity.ok(BaseResponse.success(results));
        } catch (LocationNotFoundException e){
            return ResponseEntity.ok(BaseResponse.success(Collections.emptyList(), "Origin or destination not found.", HttpStatus.OK.value()));
        }

    }

    @Operation(
            summary = "Select a specific ticket to view details",
            description = "Public endpoint to retrieve the full details for a single ticket, including vehicle-specific information and services, before a user commits to booking."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket details found"),
            @ApiResponse(responseCode = "404", description = "Ticket not found for the given criteria"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    @PostMapping("/select")
    public ResponseEntity<BaseResponse<?>> selectTicket(@Valid @RequestBody TicketSelectRequestDTO requestDTO){
        try{
            Optional<TicketResultDetailsDTO> ticketDetailsOpt = ticketSearchService.selectTicket(requestDTO);
            if (ticketDetailsOpt.isPresent()) {
                return ResponseEntity.ok(BaseResponse.success(ticketDetailsOpt.get()));
            } else {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(BaseResponse.fail(null, "Ticket not found for the given criteria.", HttpStatus.NOT_FOUND.value()));
            }
        } catch (LocationNotFoundException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(BaseResponse.fail(null, e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }
}