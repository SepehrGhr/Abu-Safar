package ir.ac.kntu.abusafar.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Setter
@Getter
@AllArgsConstructor
public class InitialBookResultDTO {
    private Long userId;

    private Long reservationId;

    private OffsetDateTime reservationDatetime;

    private OffsetDateTime expirationDatetime;

    private Boolean isRoundTrip;

    private Short seatNumber;


}
