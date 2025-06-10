package ir.ac.kntu.abusafar.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@AllArgsConstructor
@Setter
@Getter
public class Trip {

    private Long tripId;
    private Long originLocationId;
    private Long destinationLocationId;
    private OffsetDateTime departureTimestamp;
    private OffsetDateTime arrivalTimestamp;
    private Long companyId;
    private Short stopCount;
    private Short totalCapacity;
    private Short reservedCapacity;

}