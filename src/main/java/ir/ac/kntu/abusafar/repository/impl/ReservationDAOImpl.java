package ir.ac.kntu.abusafar.repository.impl;

import ir.ac.kntu.abusafar.model.Reservation;
import ir.ac.kntu.abusafar.util.constants.enums.ReserveStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

@Repository
public class ReservationDAOImpl {
    private final JdbcTemplate jdbcTemplate;

    private static final String SAVE_RESERVATION_SQL = "INSERT INTO reservations (user_id, reservation_datetime, expiration_datetime, reserve_status, is_round_trip, cancelled_by) VALUES (?, NOW(), ?, CAST(? AS reserve_status), ?, ?) RETURNING reservation_id, reservation_datetime";
    private static final String SAVE_TICKET_RESERVATION_SQL = "INSERT INTO ticket_reservation (trip_id, age, reservation_id, seat_number) VALUES (?, CAST(? AS age_range), ?, ?)";
    private static final String SAVE_INITIAL_PAYMENT_SQL = "INSERT INTO payments (reservation_id, user_id, payment_status, payment_type, payment_timestamp, price) VALUES (?, ?, CAST(? AS payment_status), NULL, NOW(), ?) RETURNING payment_id, payment_timestamp";
    private static final String FIND_RESERVATION_BY_ID_SQL = "SELECT reservation_id, user_id, reservation_datetime, expiration_datetime, reserve_status, is_round_trip, cancelled_by FROM reservations WHERE reservation_id = ?";
    private static final String UPDATE_RESERVATION_STATUS_SQL = "UPDATE reservations SET reserve_status = CAST(? AS reserve_status), cancelled_by = ? WHERE reservation_id = ?";
    private static final String GET_TICKET_INFO_FOR_CANCELLATION_SQL = "SELECT trip_id, COUNT(*) as num_seats FROM ticket_reservation WHERE reservation_id = ? GROUP BY trip_id";


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


}
