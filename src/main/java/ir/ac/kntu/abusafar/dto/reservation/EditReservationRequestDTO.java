package ir.ac.kntu.abusafar.dto.reservation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditReservationRequestDTO {

    @NotNull(message = "Reservation ID cannot be null")
    private Long reservationId;

    @NotNull(message = "Trip ID cannot be null")
    private Long tripId;

    @NotNull(message = "New seat number cannot be null")
    @Positive(message = "Seat number must be positive")
    private Short newSeatNumber;
}