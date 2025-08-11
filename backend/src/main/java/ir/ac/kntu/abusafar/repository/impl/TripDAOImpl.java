package ir.ac.kntu.abusafar.repository.impl;

import ir.ac.kntu.abusafar.model.Trip;
import ir.ac.kntu.abusafar.repository.TripDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
public class TripDAOImpl implements TripDAO {

    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_TRIP_BY_ID_SQL = "SELECT * FROM trips WHERE trip_id = ?";

    private final RowMapper<Trip> tripRowMapper = (rs, rowNum) -> new Trip(
            rs.getLong("trip_id"),
            rs.getLong("origin_location_id"),
            rs.getLong("destination_location_id"),
            rs.getObject("departure_timestamp", OffsetDateTime.class),
            rs.getObject("arrival_timestamp", OffsetDateTime.class),
            rs.getLong("company_id"),
            rs.getShort("stop_count"),
            rs.getShort("total_capacity"),
            rs.getShort("reserved_capacity")
    );

    @Autowired
    public TripDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Trip> findById(Long tripId) {
        try {
            Trip trip = jdbcTemplate.queryForObject(SELECT_TRIP_BY_ID_SQL, tripRowMapper, tripId);
            return Optional.ofNullable(trip);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}