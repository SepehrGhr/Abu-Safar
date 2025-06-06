package ir.ac.kntu.abusafar.dto.reservation;

import ir.ac.kntu.abusafar.util.constants.enums.AgeRange;

public record TicketReserveDetailsDTO (Long tripId, AgeRange age, Short seatNumber){}

