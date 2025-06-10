package ir.ac.kntu.abusafar.dto.vehicle;

import ir.ac.kntu.abusafar.util.constants.enums.FlightClass;

public record FlightDetailsDTO(FlightClass classType, String departureAirport, String arrivalAirport) implements VehicleDetailsDTO {}