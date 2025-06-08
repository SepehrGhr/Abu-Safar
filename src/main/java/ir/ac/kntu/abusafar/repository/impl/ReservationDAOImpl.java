package ir.ac.kntu.abusafar.repository.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ir.ac.kntu.abusafar.dto.reservation.InitialReserveResultDTO;
import ir.ac.kntu.abusafar.dto.reservation.ReservationInputDTO;
import ir.ac.kntu.abusafar.dto.reservation.TicketReserveDetailsDTO;
import ir.ac.kntu.abusafar.exception.ReservationPersistenceException;
import ir.ac.kntu.abusafar.exception.TripCapacityExceededException;
import ir.ac.kntu.abusafar.model.Reservation;
import ir.ac.kntu.abusafar.model.TicketReservation;
import ir.ac.kntu.abusafar.repository.ReservationDAO;
import ir.ac.kntu.abusafar.util.constants.enums.AgeRange;
import ir.ac.kntu.abusafar.util.constants.enums.ReserveStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ReservationDAOImpl implements ReservationDAO {

    private final JdbcTemplate jdbcTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationDAOImpl.class);

    private static final String SAVE_RESERVATION_SQL = "INSERT INTO reservations (user_id, is_round_trip) VALUES (?, ?) RETURNING reservation_id, reservation_datetime, expiration_datetime, reserve_status";
    private static final String SAVE_TICKET_RESERVATION_SQL = "INSERT INTO ticket_reservation (trip_id, age, reservation_id, seat_number) VALUES (?, CAST(? AS age_range), ?, ?)";
    private static final String FIND_RESERVATION_BY_ID_SQL = "SELECT reservation_id, user_id, reservation_datetime, expiration_datetime, reserve_status, is_round_trip, cancelled_by FROM reservations WHERE reservation_id = ?";
    private static final String UPDATE_RESERVATION_STATUS_SQL = "UPDATE reservations SET reserve_status = CAST(? AS reserve_status), cancelled_by = ? WHERE reservation_id = ?";
    private static final String GET_RESERVED_SEATS_SQL = "SELECT tr.seat_number FROM ticket_reservation tr JOIN reservations r ON tr.reservation_id = r.reservation_id WHERE tr.trip_id = ? AND r.reserve_status IN ('RESERVED', 'PAID')";
    private static final String DELETE_RESERVATION_BY_ID_SQL = "DELETE FROM reservations WHERE reservation_id = ?";

    @Autowired
    public ReservationDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static class ReservationRowMapper implements RowMapper<Reservation> {
        @Override
        public Reservation mapRow(ResultSet rs, int rowNum) throws SQLException {
            Long cancelledById = rs.getLong("cancelled_by");
            if (rs.wasNull()) {
                cancelledById = null;
            }
            return new Reservation(
                    rs.getLong("reservation_id"),
                    rs.getLong("user_id"),
                    rs.getObject("reservation_datetime", OffsetDateTime.class),
                    rs.getObject("expiration_datetime", OffsetDateTime.class),
                    ReserveStatus.valueOf(rs.getString("reserve_status").toUpperCase()),
                    rs.getBoolean("is_round_trip"),
                    cancelledById
            );
        }
    }

    private static class TicketReservationRowMapper implements RowMapper<TicketReservation> {
        @Override
        public TicketReservation mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new TicketReservation(
                    rs.getLong("trip_id"),
                    AgeRange.valueOf(rs.getString("age").toUpperCase()),
                    rs.getLong("reservation_id"),
                    rs.getShort("seat_number")
            );
        }
    }

    @Override
    public InitialReserveResultDTO saveInitialReservation(ReservationInputDTO reservationInput, List<TicketReserveDetailsDTO> ticketDetailsList) {
        KeyHolder reservationKeyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SAVE_RESERVATION_SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, reservationInput.userId());
            ps.setBoolean(2, reservationInput.isRoundTrip());
            return ps;
        }, reservationKeyHolder);

        Map<String, Object> reservationKeys = reservationKeyHolder.getKeys();

        if (reservationKeys == null || reservationKeys.get("reservation_id") == null) {
            throw new ReservationPersistenceException("Failed to create reservation, could not retrieve generated ID.");
        }
        Long reservationId = ((Number) reservationKeys.get("reservation_id")).longValue();
        OffsetDateTime reservationTimestamp = ((Timestamp) reservationKeys.get("reservation_datetime")).toInstant().atOffset(ZoneOffset.UTC);
        OffsetDateTime expirationTimestamp = ((Timestamp) reservationKeys.get("expiration_datetime")).toInstant().atOffset(ZoneOffset.UTC);
        boolean isRoundTrip = reservationInput.isRoundTrip();

        for (TicketReserveDetailsDTO detail : ticketDetailsList) {
            try {
                jdbcTemplate.update(SAVE_TICKET_RESERVATION_SQL,
                        detail.tripId(), detail.age().name(), reservationId, detail.seatNumber());
            } catch (DataAccessException e) {
                LOGGER.error("Database error while saving ticket_reservation for trip {}. Full exception: ", detail.tripId(), e);

                if (e.getMessage() != null && e.getMessage().toLowerCase().contains("fully booked")) {
                    throw new TripCapacityExceededException("Failed to reserve ticket for trip " + detail.tripId() + ": capacity full.");
                }
                throw new ReservationPersistenceException("Failed to save ticket reservation detail for trip " + detail.tripId());
            }
        }

        return new InitialReserveResultDTO(reservationId, reservationTimestamp, expirationTimestamp, isRoundTrip);
    }
    @Override
    public Optional<Reservation> findById(Long reservationId) {
        try {
            Reservation reservation = jdbcTemplate.queryForObject(FIND_RESERVATION_BY_ID_SQL, new ReservationRowMapper(), reservationId);
            return Optional.ofNullable(reservation);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Boolean updateStatus(Long reservationId, ReserveStatus newStatus, Long cancelledBy) {
        int rowsAffected = jdbcTemplate.update(UPDATE_RESERVATION_STATUS_SQL, newStatus.name(), cancelledBy, reservationId);
        return rowsAffected > 0;
    }

    @Override
    public int deleteById(Long reservationId) {
        return jdbcTemplate.update(DELETE_RESERVATION_BY_ID_SQL, reservationId);
    }

    @Override
    public List<Short> getReservedSeatNumbersForTrip(Long tripId) {
        return jdbcTemplate.queryForList(GET_RESERVED_SEATS_SQL, Short.class, tripId);
    }
}
