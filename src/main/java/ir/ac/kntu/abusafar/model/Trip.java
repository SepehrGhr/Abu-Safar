package ir.ac.kntu.abusafar.model;

import java.time.OffsetDateTime;

public class Trip {

    private Long tripId;

    private Long originLocationId;

    private Long destinationLocationId;

    private java.time.OffsetDateTime departureTimestamp;

    private java.time.OffsetDateTime arrivalTimestamp;

    private String vehicleCompany;

    private Short stopCount;

    private Short totalCapacity;

    private Short reservedCapacity;

    public Trip(Long tripId, Long originLocationId, Long destinationLocationId, OffsetDateTime departureTimestamp, OffsetDateTime arrivalTimestamp, Short stopCount, String vehicleCompany, Short totalCapacity, Short reservedCapacity) {
        this.tripId = tripId;
        this.originLocationId = originLocationId;
        this.destinationLocationId = destinationLocationId;
        this.departureTimestamp = departureTimestamp;
        this.arrivalTimestamp = arrivalTimestamp;
        this.stopCount = stopCount;
        this.vehicleCompany = vehicleCompany;
        this.totalCapacity = totalCapacity;
        this.reservedCapacity = reservedCapacity;
    }

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public Long getOriginLocationId() {
        return originLocationId;
    }

    public void setOriginLocationId(Long originLocationId) {
        this.originLocationId = originLocationId;
    }

    public Long getDestinationLocationId() {
        return destinationLocationId;
    }

    public void setDestinationLocationId(Long destinationLocationId) {
        this.destinationLocationId = destinationLocationId;
    }

    public OffsetDateTime getDepartureTimestamp() {
        return departureTimestamp;
    }

    public void setDepartureTimestamp(OffsetDateTime departureTimestamp) {
        this.departureTimestamp = departureTimestamp;
    }

    public OffsetDateTime getArrivalTimestamp() {
        return arrivalTimestamp;
    }

    public void setArrivalTimestamp(OffsetDateTime arrivalTimestamp) {
        this.arrivalTimestamp = arrivalTimestamp;
    }

    public String getVehicleCompany() {
        return vehicleCompany;
    }

    public void setVehicleCompany(String vehicleCompany) {
        this.vehicleCompany = vehicleCompany;
    }

    public Short getStopCount() {
        return stopCount;
    }

    public void setStopCount(Short stopCount) {
        this.stopCount = stopCount;
    }

    public Short getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(Short totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    public Short getReservedCapacity() {
        return reservedCapacity;
    }

    public void setReservedCapacity(Short reservedCapacity) {
        this.reservedCapacity = reservedCapacity;
    }
}
