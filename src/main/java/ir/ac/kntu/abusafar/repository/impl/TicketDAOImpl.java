package ir.ac.kntu.abusafar.repository.impl;

import ir.ac.kntu.abusafar.model.Ticket;
import ir.ac.kntu.abusafar.model.Trip;
import ir.ac.kntu.abusafar.repository.TicketDAO;
import ir.ac.kntu.abusafar.repository.params.TicketSearchParameters;
import ir.ac.kntu.abusafar.util.constants.enums.AgeRange;
import ir.ac.kntu.abusafar.util.constants.enums.TripType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;


import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

@Repository
public class TicketDAOImpl implements TicketDAO {
    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_COLUMNS =
            "tck.age AS tck_age, tck.price AS tck_price, tck.trip_vehicle AS tck_trip_vehicle, " +
                    "tr.trip_id, tr.origin_location_id, tr.destination_location_id, " +
                    "tr.departure_timestamp, tr.arrival_timestamp, tr.vehicle_company, " +
                    "tr.stop_count, tr.total_capacity, tr.reserved_capacity ";

    private static final String FROM_CLAUSE_BASE =
            "FROM tickets tck " +
                    "INNER JOIN trips tr ON tck.trip_id = tr.trip_id ";

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
        ticket.setAge(AgeRange.valueOf(rs.getString("age").toUpperCase()));
        ticket.setPrice(rs.getBigDecimal("price"));
        ticket.setTripVehicle(TripType.valueOf(rs.getString("trip_vehicle").toUpperCase()));
        ticket.setTrip(trip);

        return ticket;
    };

    @Autowired
    public TicketDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public List<Ticket> findTicketsByCriteria(TicketSearchParameters params) {
        StringBuilder sqlFromBuilder = new StringBuilder(FROM_CLAUSE_BASE);
        List<Object> queryParams = new ArrayList<>();
        StringBuilder sqlWhereBuilder = new StringBuilder();

        BiConsumer<StringBuilder, String> addCondition = (sb, condition) -> {
            if (sb.isEmpty()) {
                sb.append("WHERE ");
            } else {
                sb.append("AND ");
            }
            sb.append(condition).append(" ");
        };

        if (params.getOriginLocationIds() != null && !params.getOriginLocationIds().isEmpty()) {
            String inClausePlaceholders = String.join(",", Collections.nCopies(params.getOriginLocationIds().size(), "?"));
            addCondition.accept(sqlWhereBuilder, "tr.origin_location_id IN (" + inClausePlaceholders + ")");
            queryParams.addAll(params.getOriginLocationIds());
        }
        if (params.getDestinationLocationIds() != null && !params.getDestinationLocationIds().isEmpty()) {
            String inClausePlaceholders = String.join(",", Collections.nCopies(params.getDestinationLocationIds().size(), "?"));
            addCondition.accept(sqlWhereBuilder, "tr.destination_location_id IN (" + inClausePlaceholders + ")");
            queryParams.addAll(params.getDestinationLocationIds());
        }

        if (params.getDepartureFrom() != null && params.getDepartureTo() != null) {
            addCondition.accept(sqlWhereBuilder, "tr.departure_timestamp >= ? AND tr.departure_timestamp < ?");
            queryParams.add(params.getDepartureFrom());
            queryParams.add(params.getDepartureTo());
        } else if (params.getDepartureFrom() != null) {
            addCondition.accept(sqlWhereBuilder, "tr.departure_timestamp >= ?");
            queryParams.add(params.getDepartureFrom());
        } else if (params.getDepartureTo() != null) {
            addCondition.accept(sqlWhereBuilder, "tr.departure_timestamp < ?");
            queryParams.add(params.getDepartureTo());
        }


        if (params.getVehicleCompany() != null && !params.getVehicleCompany().trim().isEmpty()) {
            addCondition.accept(sqlWhereBuilder, "tr.vehicle_company ILIKE ?");
            queryParams.add("%" + params.getVehicleCompany() + "%");
        }

        if (params.getTripVehicle() != null) {
            addCondition.accept(sqlWhereBuilder, "tck.trip_vehicle = ?");
            queryParams.add(params.getTripVehicle().name());

            switch (params.getTripVehicle()) {
                case BUS:
                    if (params.getBusClass() != null) {
                        sqlFromBuilder.append("INNER JOIN buses b ON tr.trip_id = b.trip_id ");
                        addCondition.accept(sqlWhereBuilder, "b.class = ?");
                        queryParams.add(params.getBusClass().name());
                    }
                    break;
                case FLIGHT:
                    if (params.getFlightClass() != null) {
                        sqlFromBuilder.append("INNER JOIN flights f ON tr.trip_id = f.trip_id ");
                        addCondition.accept(sqlWhereBuilder, "f.class = ?");
                        queryParams.add(params.getFlightClass().name());
                    }
                    break;
                case TRAIN:
                    if (params.getTrainStars() != null && params.getTrainStars() > 0) {
                        sqlFromBuilder.append("INNER JOIN trains tn ON tr.trip_id = tn.trip_id ");
                        addCondition.accept(sqlWhereBuilder, "tn.stars >= ?");
                        queryParams.add(params.getTrainStars());
                    }
                    break;
            }
        }

        if (params.getAgeCategory() != null) {
            addCondition.accept(sqlWhereBuilder, "tck.age = ?");
            queryParams.add(params.getAgeCategory().name());
        }
        if (params.getMinPrice() != null) {
            addCondition.accept(sqlWhereBuilder, "tck.price >= ?");
            queryParams.add(params.getMinPrice());
        }
        if (params.getMaxPrice() != null) {
            addCondition.accept(sqlWhereBuilder, "tck.price <= ?");
            queryParams.add(params.getMaxPrice());
        }

        String finalSql = "SELECT " + SELECT_COLUMNS + sqlFromBuilder.toString() + sqlWhereBuilder.toString() + "ORDER BY tr.departure_timestamp ASC, tck.price ASC";

        return jdbcTemplate.query(finalSql, TICKET_WITH_TRIP_ROW_MAPPER, queryParams.toArray());
    }

}
