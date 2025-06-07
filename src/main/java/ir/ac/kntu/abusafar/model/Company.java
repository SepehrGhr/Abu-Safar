package ir.ac.kntu.abusafar.model;

import ir.ac.kntu.abusafar.util.constants.enums.TripType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
public class Company {
    private Long id;
    private String name;
    private TripType vehicleType;
    private BigDecimal cancellationPenaltyRate;
    private String logoPictureUrl;
    private String description;
    private boolean isActive;
}