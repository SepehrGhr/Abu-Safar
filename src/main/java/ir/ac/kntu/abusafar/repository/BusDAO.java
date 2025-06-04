package ir.ac.kntu.abusafar.repository;

import ir.ac.kntu.abusafar.dto.vehicle.BusDetailsDTO;

import java.util.Optional;

public interface BusDAO {
    Optional<BusDetailsDTO> findBusDetailsByTripId(Long tripId);
}
