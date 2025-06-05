package ir.ac.kntu.abusafar.dto.reservation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationInputDTO {
    private Long userId;
    private Boolean isRoundTrip;
}
