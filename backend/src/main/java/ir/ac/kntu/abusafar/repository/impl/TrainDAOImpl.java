package ir.ac.kntu.abusafar.repository.impl;

import ir.ac.kntu.abusafar.dto.vehicle.TrainDetailsDTO;
import ir.ac.kntu.abusafar.model.Train;
import ir.ac.kntu.abusafar.repository.TrainDAO;
import ir.ac.kntu.abusafar.util.constants.enums.TrainRoomType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TrainDAOImpl implements TrainDAO {

    private static final String SELECT_TRAIN_BY_TRIP_ID_SQL =
            "SELECT trip_id, stars, room_type FROM trains WHERE trip_id = ?";

    private static final RowMapper<Train> TRAIN_ROW_MAPPER = (rs, rowNum) -> {
        Long tripId = rs.getLong("trip_id");
        Short stars = rs.getShort("stars");
        TrainRoomType roomType = TrainRoomType.fromString(rs.getString("room_type"));
        return new Train(tripId, stars, roomType);
    };

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TrainDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<TrainDetailsDTO> findTrainDetailsByTripId(Long tripId) {
        if (tripId == null) {
            return Optional.empty();
        }

        try {
            Train train = jdbcTemplate.queryForObject(SELECT_TRAIN_BY_TRIP_ID_SQL, TRAIN_ROW_MAPPER, tripId
            );

            if (train != null) {
                TrainDetailsDTO dto = new TrainDetailsDTO(
                        train.getStars(),
                        train.getRoomType()
                );
                return Optional.of(dto);
            } else {
                return Optional.empty();
            }
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}