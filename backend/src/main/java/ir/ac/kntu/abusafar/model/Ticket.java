package ir.ac.kntu.abusafar.model;
import ir.ac.kntu.abusafar.util.constants.enums.TripType;
import ir.ac.kntu.abusafar.util.constants.enums.AgeRange;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Ticket {
    private Trip trip;

    private AgeRange age;

    private BigDecimal price;

    private TripType tripVehicle;

}
