package ir.ac.kntu.abusafar.model;
import ir.ac.kntu.abusafar.util.constants.enums.PaymentMeans;
import ir.ac.kntu.abusafar.util.constants.enums.PaymentStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@NoArgsConstructor
@Getter
@Setter
public class Payment {

    private Long paymentId;

    private Long reservationId;

    private Long userId;

    private PaymentStatus paymentStatus;

    private PaymentMeans paymentType;

    private OffsetDateTime paymentTimestamp;

    private BigDecimal price;
}
