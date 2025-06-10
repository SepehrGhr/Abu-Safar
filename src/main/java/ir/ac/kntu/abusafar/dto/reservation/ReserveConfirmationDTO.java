package ir.ac.kntu.abusafar.dto.reservation;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record ReserveConfirmationDTO (Long reservationId, OffsetDateTime reservationDatetime,
                                      OffsetDateTime expirationDatetime, Boolean isRoundTrip, Short seatNumber,
                                      BigDecimal price){}
