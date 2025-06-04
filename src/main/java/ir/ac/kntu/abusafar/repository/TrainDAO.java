package ir.ac.kntu.abusafar.repository;

import ir.ac.kntu.abusafar.dto.vehicle.TrainDetailsDTO;
import ir.ac.kntu.abusafar.util.constants.enums.TrainRoomType;

import java.util.Optional;

public interface TrainDAO {
    Optional<TrainDetailsDTO> findTrainDetailsByTripId(Long tripId);
}
