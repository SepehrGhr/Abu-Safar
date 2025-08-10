package ir.ac.kntu.abusafar.dto.payment;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PaymentRecordDTO (
        Long paymentId,
        Long reservationId,
        String paymentStatus,
        String paymentType,
        OffsetDateTime paymentTimestamp,
        BigDecimal price
){
}
