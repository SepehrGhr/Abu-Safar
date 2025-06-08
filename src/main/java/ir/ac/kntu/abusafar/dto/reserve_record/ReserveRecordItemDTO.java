package ir.ac.kntu.abusafar.dto.reserve_record;

import ir.ac.kntu.abusafar.dto.ticket.TicketResultItemDTO;
import ir.ac.kntu.abusafar.util.constants.enums.TicketStatus;

import java.time.OffsetDateTime;

public record ReserveRecordItemDTO (
        TicketStatus status,
        Long reservationId,
        Long paymentId,
        OffsetDateTime paymentTimestamp,
        Short seatNumber,
        Boolean isRoundTrip,
        TicketResultItemDTO ticketInformation
) {
}
