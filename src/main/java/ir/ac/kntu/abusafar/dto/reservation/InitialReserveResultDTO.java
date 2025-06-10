package ir.ac.kntu.abusafar.dto.reservation;


import java.time.OffsetDateTime;

public record InitialReserveResultDTO (Long reservationId, OffsetDateTime reservationDatetime,
                                       OffsetDateTime expirationDatetime, Boolean isRoundTrip){}

