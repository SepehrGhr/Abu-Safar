package ir.ac.kntu.abusafar.dto.cancellation;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CancellationPenaltyResponseDTO {
    private BigDecimal originalPrice;
    private BigDecimal penaltyAmount;
    private BigDecimal refundAmount;
    private String message;
}