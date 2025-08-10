package ir.ac.kntu.abusafar.repository.impl;

import ir.ac.kntu.abusafar.dto.vehicle.FlightDetailsDTO;
import ir.ac.kntu.abusafar.model.Flight;
import ir.ac.kntu.abusafar.repository.FlightDAO;
import ir.ac.kntu.abusafar.util.constants.enums.FlightClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class FlightDAOImpl implements FlightDAO {

    private static final String SELECT_FLIGHT_BY_TRIP_ID_SQL =
            "SELECT trip_id, class, departure_airport, arrival_airport FROM flights WHERE trip_id = ?";

    private static final RowMapper<Flight> FLIGHT_ROW_MAPPER = (rs, rowNum) -> {
        Long tripId = rs.getLong("trip_id");
        FlightClass flightClass = FlightClass.getEnumValue(rs.getString("class"));
        String departureAirport = rs.getString("departure_airport");
        String arrivalAirport = rs.getString("arrival_airport");
        return new Flight(tripId, flightClass, departureAirport, arrivalAirport);
    };

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FlightDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<FlightDetailsDTO> findFlightDetailsByTripId(Long tripId) {
        if (tripId == null) {
            return Optional.empty();
        }

        try {
            Flight flight = jdbcTemplate.queryForObject(SELECT_FLIGHT_BY_TRIP_ID_SQL, FLIGHT_ROW_MAPPER, tripId
            );

            if (flight != null) {
                FlightDetailsDTO dto = new FlightDetailsDTO(
                        flight.getClassType(),
                        flight.getDepartureAirport(),
                        flight.getArrivalAirport()
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