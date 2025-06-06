package ir.ac.kntu.abusafar.dto.payment;

import java.math.BigDecimal;

public record PaymentDTO (Long userId, BigDecimal price) {}
