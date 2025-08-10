package ir.ac.kntu.abusafar.dto.payment;

import ir.ac.kntu.abusafar.util.constants.enums.PaymentMeans;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PaymentRequestDTO {

    @NotNull(message = "Reservation ID cannot be null")
    private Long reservationId;

    @NotNull(message = "Payment means cannot be null")
    private PaymentMeans paymentMeans;
}