package ir.ac.kntu.abusafar.dto.reserve_record;

import ir.ac.kntu.abusafar.dto.ticket.TicketResultItemDTO;
import ir.ac.kntu.abusafar.util.constants.enums.TicketStatus;

import java.time.OffsetDateTime;
import java.util.List;

public record ReserveRecordItemDTO (
        TicketStatus status,
        Long reservationId,
        Long paymentId,
        OffsetDateTime paymentTimestamp,
        List<Short> seatNumbers,
        Boolean isRoundTrip,
        List<TicketResultItemDTO> ticketsInformation
) {
}