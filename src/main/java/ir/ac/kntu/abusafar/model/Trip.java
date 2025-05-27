package ir.ac.kntu.abusafar.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@NoArgsConstructor
@Setter
@Getter
public class Trip {

    private Long tripId;

    private Long originLocationId;

    private Long destinationLocationId;

    private OffsetDateTime departureTimestamp;

    private OffsetDateTime arrivalTimestamp;

    private String vehicleCompany;

    private Short stopCount;

    private Short totalCapacity;

    private Short reservedCapacity;

}
