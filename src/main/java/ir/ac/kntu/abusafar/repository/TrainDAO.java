package ir.ac.kntu.abusafar.repository;

import ir.ac.kntu.abusafar.util.constants.enums.TrainRoomType;

import java.util.Optional;

public interface TrainDAO {
    Optional<Short> findTrainStarsById(Long tripId);
    Optional<TrainRoomType> findTrainRoomTypeById(Long tripId);

}
