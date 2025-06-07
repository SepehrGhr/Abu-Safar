package ir.ac.kntu.abusafar.repository.impl;

import ir.ac.kntu.abusafar.model.TicketReservation;
import ir.ac.kntu.abusafar.repository.TicketReservationDAO;
import ir.ac.kntu.abusafar.util.constants.enums.AgeRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TicketReservationDAOImpl implements TicketReservationDAO {

    private final JdbcTemplate jdbcTemplate;

    private static final String FIND_BY_RESERVATION_AND_TRIP_SQL = "SELECT * FROM ticket_reservation WHERE reservation_id = ? AND trip_id = ?";

    private final RowMapper<TicketReservation> rowMapper = (rs, rowNum) -> new TicketReservation(
            rs.getLong("trip_id"),
            AgeRange.valueOf(rs.getString("age").toUpperCase()),
            rs.getLong("reservation_id"),
            rs.getShort("seat_number")
    );

    @Autowired
    public TicketReservationDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<TicketReservation> findByReservationAndTrip(Long reservationId, Long tripId) {
        try {
            TicketReservation ticketReservation = jdbcTemplate.queryForObject(FIND_BY_RESERVATION_AND_TRIP_SQL, rowMapper, reservationId, tripId);
            return Optional.ofNullable(ticketReservation);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}