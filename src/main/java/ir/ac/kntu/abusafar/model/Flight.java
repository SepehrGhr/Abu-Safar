package ir.ac.kntu.abusafar.model;
import ir.ac.kntu.abusafar.util.constants.enums.FlightClass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class Flight {

    private Long tripId;

    private FlightClass classType;

    private String departureAirport;

    private String arrivalAirport;
}
