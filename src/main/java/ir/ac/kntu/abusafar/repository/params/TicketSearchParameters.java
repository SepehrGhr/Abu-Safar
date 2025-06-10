package ir.ac.kntu.abusafar.repository.params;

import ir.ac.kntu.abusafar.util.constants.enums.AgeRange;
import ir.ac.kntu.abusafar.util.constants.enums.BusClass;
import ir.ac.kntu.abusafar.util.constants.enums.FlightClass;
import ir.ac.kntu.abusafar.util.constants.enums.TripType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
public class TicketSearchParameters {

    private final List<Long> originLocationIds;
    private final List<Long> destinationLocationIds;
    private final OffsetDateTime departureFrom;
    private final OffsetDateTime departureTo;
    private final String vehicleCompany;
    private final TripType tripVehicle;
    private final AgeRange ageCategory;
    private final BigDecimal minPrice;
    private final BigDecimal maxPrice;
    private final BusClass busClass;
    private final FlightClass flightClass;
    private final Short trainStars;
}
