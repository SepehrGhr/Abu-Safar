package ir.ac.kntu.abusafar.repository.impl;

import ir.ac.kntu.abusafar.model.Ticket;
import ir.ac.kntu.abusafar.model.Trip;
import ir.ac.kntu.abusafar.repository.TicketDAO;
import ir.ac.kntu.abusafar.util.constants.enums.AgeRange;
import ir.ac.kntu.abusafar.util.constants.enums.TripType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;


import java.time.OffsetDateTime;

@Repository
public class TicketDAOImpl implements TicketDAO {
    private final JdbcTemplate jdbcTemplate;


    private final RowMapper<Ticket> TICKET_WITH_TRIP_ROW_MAPPER = (rs, rowNum) -> {
        Trip trip = new Trip(
                rs.getLong("trip_id"),
                rs.getLong("origin_location_id"),
                rs.getLong("destination_location_id"),
                rs.getObject("departure_timestamp", OffsetDateTime.class),
                rs.getObject("arrival_timestamp", OffsetDateTime.class),
                rs.getString("vehicle_company"),
                rs.getShort("stop_count"),
                rs.getShort("total_capacity"),
                rs.getShort("reserved_capacity")
        );
        Ticket ticket = new Ticket();
        ticket.setAge(AgeRange.valueOf(rs.getString("account_status").toUpperCase()));
        ticket.setPrice(rs.getBigDecimal("price"));
        ticket.setTripVehicle(TripType.valueOf(rs.getString("trip_vehicle").toUpperCase()));
        ticket.setTrip(trip);

        return ticket;
    };

    @Autowired
    public TicketDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
