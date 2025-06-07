package ir.ac.kntu.abusafar.dto.cancellation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class CancellationResponseDTO {
    private String message;
    private BigDecimal refundedAmount;
    private BigDecimal newWalletBalance;
}