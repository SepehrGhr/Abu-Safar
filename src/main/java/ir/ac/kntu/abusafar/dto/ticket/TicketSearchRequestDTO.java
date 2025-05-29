package ir.ac.kntu.abusafar.dto.ticket;

import ir.ac.kntu.abusafar.util.constants.enums.AgeRange;
import ir.ac.kntu.abusafar.util.constants.enums.BusClass;
import ir.ac.kntu.abusafar.util.constants.enums.FlightClass;
import ir.ac.kntu.abusafar.util.constants.enums.TripType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Setter
@Getter
public class TicketSearchRequestDTO {
    private String originCity;
    private String originProvince;
    private String originCountry;

    private String destinationCity;
    private String destinationProvince;
    private String destinationCountry;

    private LocalDate departureDate;
    private LocalTime departureTime;
    private String vehicleCompany;
    private TripType tripVehicle;
    private AgeRange ageCategory;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    private BusClass busClass;
    private FlightClass flightClass;
    private short trainStars;
}
