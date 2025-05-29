package ir.ac.kntu.abusafar.model;
import ir.ac.kntu.abusafar.util.constants.enums.TripType;
import ir.ac.kntu.abusafar.util.constants.enums.AgeRange;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@AllArgsConstructor
@Getter
@Setter
public class Ticket {
    private Long tripId;

    private AgeRange age;

    private BigDecimal price;

    private TripType tripVehicle;

}
