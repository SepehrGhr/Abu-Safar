package ir.ac.kntu.abusafar.dto.ticket;

import ir.ac.kntu.abusafar.dto.vehicle.VehicleDetailsDTO;
import ir.ac.kntu.abusafar.util.constants.enums.AgeRange;
import ir.ac.kntu.abusafar.util.constants.enums.ServiceType;
import ir.ac.kntu.abusafar.util.constants.enums.TripType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record TicketResultDetailsDTO(
        String originCity,
        String destinationCity,
        OffsetDateTime departureTimestamp,
        OffsetDateTime arrivalTimestamp,
        TripType tripVehicle,
        BigDecimal price,
        String vehicleCompany,
        VehicleDetailsDTO vehicleDetails,
        Short stopCount,
        Short totalCapacity,
        Short reservedCapacity,
        AgeRange age,
        List<ServiceType> service
) {
}
