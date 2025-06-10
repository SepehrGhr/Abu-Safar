package ir.ac.kntu.abusafar.dto.reserve_record;

import ir.ac.kntu.abusafar.util.constants.enums.AgeRange;
import ir.ac.kntu.abusafar.util.constants.enums.TicketStatus;
import ir.ac.kntu.abusafar.util.constants.enums.TripType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record RawHistoryRecordDTO(
        TicketStatus calculatedStatus,
        Long reservationId,
        Boolean isRoundTrip,
        Long paymentId,
        OffsetDateTime paymentTimestamp,
        Short seatNumber,
        Long tripId,
        Long originLocationId,
        Long destinationLocationId,
        OffsetDateTime departureTimestamp,
        OffsetDateTime arrivalTimestamp,
        String vehicleCompany,
        AgeRange ticketAge,
        BigDecimal ticketPrice,
        TripType tripVehicle
) {}