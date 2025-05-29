package ir.ac.kntu.abusafar.dto.ticket;

import ir.ac.kntu.abusafar.util.constants.enums.TripType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;

@Setter
@Getter
public class TicketResultItemDTO {
    private String originCity;
    private String destinationCity;
    private OffsetDateTime departureTimestamp;
    private OffsetDateTime arrivalTimestamp;
    private TripType tripVehicle;
    private BigDecimal price;
    private String vehicleCompany;

}
