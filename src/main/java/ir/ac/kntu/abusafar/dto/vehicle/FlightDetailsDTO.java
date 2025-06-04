package ir.ac.kntu.abusafar.dto.vehicle;

import ir.ac.kntu.abusafar.util.constants.enums.FlightClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class FlightDetailsDTO {

    private FlightClass classType;

    private String departureAirport;

    private String arrivalAirport;
}
