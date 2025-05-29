package ir.ac.kntu.abusafar.model;
import ir.ac.kntu.abusafar.util.constants.enums.BusChairCountType;
import ir.ac.kntu.abusafar.util.constants.enums.BusClass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Bus {

    private Long tripId;

    private BusClass classType; // Renamed 'class' to 'classType' to avoid keyword conflict

    private BusChairCountType chairType;
}
