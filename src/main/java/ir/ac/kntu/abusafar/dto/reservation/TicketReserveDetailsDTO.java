package ir.ac.kntu.abusafar.dto.reservation;

import ir.ac.kntu.abusafar.util.constants.enums.AgeRange;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketReserveDetailsDTO {
    private Long tripId;

    private AgeRange age;

    private Short seatNumber;
}

