package ir.ac.kntu.abusafar.model;

import ir.ac.kntu.abusafar.util.constants.enums.ReserveStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Setter
@Getter
@AllArgsConstructor
public class Reservation {

    private Long reservationId;

    private Long userId;

    private OffsetDateTime reservationDatetime;

    private OffsetDateTime expirationDatetime;

    private ReserveStatus reserveStatus;

    private Boolean isRoundTrip;

    private Long cancelledBy;

}
