package ir.ac.kntu.abusafar.repository;

import ir.ac.kntu.abusafar.model.Trip;
import java.util.Optional;

public interface TripDAO {
    Optional<Trip> findById(Long tripId);
}