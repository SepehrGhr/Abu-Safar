package ir.ac.kntu.abusafar.controller;

import ir.ac.kntu.abusafar.dto.response.BaseResponse;
import ir.ac.kntu.abusafar.dto.ticket.TicketResultItemDTO;
import ir.ac.kntu.abusafar.dto.ticket.TicketSearchRequestDTO;
import ir.ac.kntu.abusafar.exception.LocationNotFoundException;
import ir.ac.kntu.abusafar.service.TicketSearchService;
import ir.ac.kntu.abusafar.util.constants.Routes;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(Routes.API_KEY + "/tickets")
public class TicketSearchController {
    private final TicketSearchService ticketSearchService;

    @Autowired
    public TicketSearchController(TicketSearchService ticketSearchService) {
        this.ticketSearchService = ticketSearchService;
    }

    @GetMapping("/search")
   @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<?>> findTickets(@Valid @RequestBody TicketSearchRequestDTO requestDTO){
        try{
            return ResponseEntity.ok(BaseResponse.success(ticketSearchService.searchTickets(requestDTO)));
        } catch (LocationNotFoundException e){
            return ResponseEntity.ok(BaseResponse.success(Collections.emptyList(), "Location not found.", HttpStatus.OK.value()));
        }

    }

}
