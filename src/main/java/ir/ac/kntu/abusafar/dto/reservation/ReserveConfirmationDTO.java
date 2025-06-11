package ir.ac.kntu.abusafar.dto.reservation;

import ir.ac.kntu.abusafar.dto.ticket.TicketResultItemDTO;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record ReserveConfirmationDTO (Long reservationId, OffsetDateTime reservationDatetime,
                                      OffsetDateTime expirationDatetime, Boolean isRoundTrip,
                                      List<TicketResultItemDTO> tickets,
                                      List<Short> seatNumbers,
                                      BigDecimal price){}
