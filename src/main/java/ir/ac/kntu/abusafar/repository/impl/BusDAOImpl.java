package ir.ac.kntu.abusafar.repository.impl;

import ir.ac.kntu.abusafar.model.Bus;
import ir.ac.kntu.abusafar.repository.BusDAO;
import ir.ac.kntu.abusafar.util.constants.enums.BusChairCountType;
import ir.ac.kntu.abusafar.util.constants.enums.BusClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet; // Import for ResultSet
import java.sql.SQLException; // Import for SQLException
import java.util.Optional;

@Repository
public class BusDAOImpl implements BusDAO {

    private static final String SELECT_BUS_CLASS_BY_TRIP_ID_SQL =
            "SELECT class FROM buses WHERE trip_id = ?";

    private static final RowMapper<Bus> BUS_ROW_MAPPER = (rs, rowNum) -> {
        Long trip_id = rs.getLong("trip_id");
        BusClass busClass = BusClass.getEnumValue(rs.getString("class"));
        BusChairCountType chairType = BusChairCountType.getEnumValue(rs.getString("chair_type"));
        return new Bus(trip_id, busClass, chairType);
    };

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BusDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<BusClass> findBusClassByTripId(Long tripId) {
        if (tripId == null) {
            return Optional.empty();
        }

        try {
            Bus bus = jdbcTemplate.queryForObject(SELECT_BUS_CLASS_BY_TRIP_ID_SQL, BUS_ROW_MAPPER, tripId
            );
            return Optional.ofNullable(bus.getClassType());
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (Exception e) {
            System.err.println("Error fetching bus class for trip ID " + tripId + ": " + e.getMessage());
            return Optional.empty();
        }
    }
}