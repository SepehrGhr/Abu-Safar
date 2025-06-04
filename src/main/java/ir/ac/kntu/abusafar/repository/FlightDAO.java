package ir.ac.kntu.abusafar.repository;

import ir.ac.kntu.abusafar.dto.vehicle.FlightDetailsDTO;

import java.util.Optional;

public interface FlightDAO {
    Optional<FlightDetailsDTO> findFlightDetailsByTripId(Long tripId);
}
