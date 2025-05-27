package ir.ac.kntu.abusafar.model;

import ir.ac.kntu.abusafar.util.constants.enums.AgeRange;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class TicketReservation {

    private Long tripId;

    private AgeRange age;

    private Long reservationId;

    private Short seatNumber;
}
