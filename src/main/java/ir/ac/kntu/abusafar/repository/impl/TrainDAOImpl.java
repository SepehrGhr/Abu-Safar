package ir.ac.kntu.abusafar.repository.impl;

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
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TrainDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Train> TRAIN_ROW_MAPPER = (rs, rowNum) -> {
        Short stars = rs.getShort("stars");
        String roomTypeStr = rs.getString("room_type");
        String id = rs.getString("trip_id");
        TrainRoomType roomType = TrainRoomType.getEnumValue(roomTypeStr);
        return new Train(Long.valueOf(id), stars, roomType);
    };

    @Override
    public Optional<Short> findTrainStarsById(Long tripId) {
        if (tripId == null) {
            return Optional.empty();
        }
        String sql = "SELECT stars FROM trains WHERE trip_id = ?";
        try {
            Short stars = jdbcTemplate.queryForObject(sql, Short.class, tripId);
            return Optional.ofNullable(stars);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<TrainRoomType> findTrainRoomTypeById(Long tripId) {
        if (tripId == null) {
            return Optional.empty();
        }
        String sql = "SELECT room_type FROM trains WHERE trip_id = ?";

        try {
            Train train = jdbcTemplate.queryForObject(
                    sql,
                    TRAIN_ROW_MAPPER,
                    tripId
            );
            return Optional.ofNullable(train != null ? train.getRoomType() : null);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
