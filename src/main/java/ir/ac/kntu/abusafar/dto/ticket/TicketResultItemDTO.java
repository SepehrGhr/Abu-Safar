package ir.ac.kntu.abusafar.dto.ticket;

import ir.ac.kntu.abusafar.util.constants.enums.AgeRange;
import ir.ac.kntu.abusafar.util.constants.enums.TripType;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TicketResultItemDTO(
        Long tripId,
        AgeRange age,
        String originCity,
        String destinationCity,
        OffsetDateTime departureTimestamp,
        OffsetDateTime arrivalTimestamp,
        TripType tripVehicle,
        BigDecimal price,
        String vehicleCompany
) {
}
