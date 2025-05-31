package ir.ac.kntu.abusafar.dto.ticket;

import ir.ac.kntu.abusafar.util.constants.enums.AgeRange;
import ir.ac.kntu.abusafar.util.constants.enums.BusClass;
import ir.ac.kntu.abusafar.util.constants.enums.FlightClass;
import ir.ac.kntu.abusafar.util.constants.enums.TripType;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Setter
@Getter
public class TicketSearchRequestDTO {
    @NotBlank(message = "Origin city cannot be blank.")
    private String originCity;
    private String originProvince;
    private String originCountry;

    @NotBlank(message = "Destination city cannot be blank.")
    private String destinationCity;
    private String destinationProvince;
    private String destinationCountry;

    @NotBlank(message = "Departure date cannot be blank.")
    private LocalDate departureDate;
    private LocalTime departureTime;
    private String vehicleCompany;

    @NotBlank(message = "Trip Vehicle cannot be blank.")
    private TripType tripVehicle;
    private AgeRange ageCategory;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private BusClass busClass;
    private FlightClass flightClass;
    private Short trainStars;
}
