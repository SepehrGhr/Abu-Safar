package ir.ac.kntu.abusafar.dto.cancellation;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancellationPenaltyRequestDTO {

    @NotNull(message = "Reservation ID cannot be null.")
    private Long reservationId;

}