package ir.ac.kntu.abusafar.repository.impl;

import ir.ac.kntu.abusafar.dto.reserve_record.RawHistoryRecordDTO;
import ir.ac.kntu.abusafar.repository.PaidReservationDAO;
import ir.ac.kntu.abusafar.util.constants.enums.AgeRange;
import ir.ac.kntu.abusafar.util.constants.enums.TicketStatus;
import ir.ac.kntu.abusafar.util.constants.enums.TripType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Repository
public class PaidReservationDAOImpl implements PaidReservationDAO {

    private final JdbcTemplate jdbcTemplate;

    private static final String FETCH_HISTORY_BY_USER_SQL = """
        SELECT
            CASE
                WHEN r.reserve_status = 'CANCELLED' THEN 'CANCELLED'
                WHEN r.reserve_status = 'RESERVED' THEN 'PENDING_PAYMENT'
                WHEN r.reserve_status = 'PAID' AND t.departure_timestamp > NOW() THEN 'UPCOMING_TRIP'
                WHEN r.reserve_status = 'PAID' AND t.departure_timestamp <= NOW() THEN 'PAST_TRIP'
                ELSE 'UNKNOWN'
            END AS calculated_status,
            r.reservation_id,
            r.is_round_trip,
            p.payment_id,
            p.payment_timestamp,
            tr.seat_number,
            t.trip_id,
            t.origin_location_id,
            t.destination_location_id,
            t.departure_timestamp,
            t.arrival_timestamp,
            c.name AS vehicle_company,
            tk.age AS tck_age,
            tk.price AS tck_price,
            tk.trip_vehicle AS tck_trip_vehicle
        FROM
            reservations r
        JOIN
            ticket_reservation tr ON r.reservation_id = tr.reservation_id
        JOIN
            trips t ON tr.trip_id = t.trip_id
        JOIN
            tickets tk ON t.trip_id = tk.trip_id AND tr.age = tk.age
        JOIN
            companies c ON t.company_id = c.company_id
        LEFT JOIN
            payments p ON r.reservation_id = p.reservation_id
        WHERE
            r.user_id = ?
        ORDER BY
            r.reservation_datetime DESC
    """;

    private static final String FETCH_HISTORY_BY_STATUS_SQL = """
        SELECT
            CASE
                WHEN r.reserve_status = 'CANCELLED' THEN 'CANCELLED'
                WHEN r.reserve_status = 'RESERVED' THEN 'PENDING_PAYMENT'
                WHEN r.reserve_status = 'PAID' AND t.departure_timestamp > NOW() THEN 'UPCOMING_TRIP'
                WHEN r.reserve_status = 'PAID' AND t.departure_timestamp <= NOW() THEN 'PAST_TRIP'
                ELSE 'UNKNOWN'
            END AS calculated_status,
            r.reservation_id, r.is_round_trip, p.payment_id, p.payment_timestamp,
            tr.seat_number, t.trip_id, t.origin_location_id, t.destination_location_id,
            t.departure_timestamp, t.arrival_timestamp, c.name AS vehicle_company,
            tk.age AS tck_age, tk.price AS tck_price, tk.trip_vehicle AS tck_trip_vehicle
        FROM
            reservations r
        JOIN ticket_reservation tr ON r.reservation_id = tr.reservation_id
        JOIN trips t ON tr.trip_id = t.trip_id
        JOIN tickets tk ON t.trip_id = tk.trip_id AND tr.age = tk.age
        JOIN companies c ON t.company_id = c.company_id
        LEFT JOIN payments p ON r.reservation_id = p.reservation_id
        WHERE
            CASE
                WHEN r.reserve_status = 'CANCELLED' THEN 'CANCELLED'
                WHEN r.reserve_status = 'RESERVED' THEN 'PENDING_PAYMENT'
                WHEN r.reserve_status = 'PAID' AND t.departure_timestamp > NOW() THEN 'UPCOMING_TRIP'
                ELSE 'PAST_TRIP'
            END = ?
        ORDER BY
            r.reservation_datetime DESC
    """;

    private final RowMapper<RawHistoryRecordDTO> rowMapper = (rs, rowNum) -> {
        Long paymentId = rs.getObject("payment_id", Long.class);
        OffsetDateTime paymentTimestamp = null;
        Timestamp ts = rs.getTimestamp("payment_timestamp");
        if (ts != null) {
            paymentTimestamp = ts.toInstant().atOffset(ZoneOffset.UTC);
        }

        return new RawHistoryRecordDTO(
                TicketStatus.valueOf(rs.getString("calculated_status")),
                rs.getLong("reservation_id"),
                rs.getBoolean("is_round_trip"),
                paymentId,
                paymentTimestamp,
                rs.getShort("seat_number"),
                rs.getLong("trip_id"),
                rs.getLong("origin_location_id"),
                rs.getLong("destination_location_id"),
                rs.getObject("departure_timestamp", OffsetDateTime.class),
                rs.getObject("arrival_timestamp", OffsetDateTime.class),
                rs.getString("vehicle_company"),
                AgeRange.valueOf(rs.getString("tck_age").toUpperCase()),
                rs.getBigDecimal("tck_price"),
                TripType.valueOf(rs.getString("tck_trip_vehicle").toUpperCase())
        );
    };

    @Autowired
    public PaidReservationDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<RawHistoryRecordDTO> findReservationHistoryByUserId(Long userId) {
        return jdbcTemplate.query(FETCH_HISTORY_BY_USER_SQL, rowMapper, userId);
    }

    @Override
    public List<RawHistoryRecordDTO> findReservationHistoryByStatus(TicketStatus statusFilter) {
        return jdbcTemplate.query(FETCH_HISTORY_BY_STATUS_SQL, rowMapper, statusFilter.name());
    }
}
