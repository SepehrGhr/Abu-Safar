package ir.ac.kntu.abusafar.dto.ticket;

import ir.ac.kntu.abusafar.model.AdditionalService;
import ir.ac.kntu.abusafar.util.constants.enums.AgeRange;
import ir.ac.kntu.abusafar.util.constants.enums.BusClass;
import ir.ac.kntu.abusafar.util.constants.enums.FlightClass;
import ir.ac.kntu.abusafar.util.constants.enums.TripType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
public class TicketResultDetailsDTO {
    private String originCity;
    private String destinationCity;
    private OffsetDateTime departureTimestamp;
    private OffsetDateTime arrivalTimestamp;
    private TripType tripVehicle;
    private BigDecimal price;
    private String vehicleCompany;
    private BusClass busClass;
    private FlightClass flightClass;
    private Short trainStars;
    private Short stopCount;
    private Short totalCapacity;
    private Short reservedCapacity;
    private AgeRange age;
    private List<AdditionalService> service;
}
