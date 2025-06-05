package ir.ac.kntu.abusafar.dto.payment;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PaymentDTO {
    private Long userId;

    private BigDecimal price;
}
