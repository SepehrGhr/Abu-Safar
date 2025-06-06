package ir.ac.kntu.abusafar.dto.reservation;

import java.time.OffsetDateTime;

public record ReserveConfirmationDTO (Long userId, Long reservationId, OffsetDateTime reservationDatetime,
                                      OffsetDateTime expirationDatetime, Boolean isRoundTrip, Short seatNumber){}
