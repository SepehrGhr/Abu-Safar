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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class TicketDAOImpl implements TicketDAO {
    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_COLUMNS =
            "tck.age AS tck_age, tck.price AS tck_price, tck.trip_vehicle AS tck_trip_vehicle, " +
                    "tr.trip_id, tr.origin_location_id, tr.destination_location_id, " +
                    "tr.departure_timestamp, tr.arrival_timestamp, tr.vehicle_company, " +
                    "tr.stop_count, tr.total_capacity, tr.reserved_capacity ";

    private static final String FROM_CLAUSE_BASE =
            "tickets tck " +
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
        List<String> fromParts = new ArrayList<>();
        fromParts.add("FROM " + FROM_CLAUSE_BASE); // Add base FROM

        List<String> whereConditions = new ArrayList<>();
        List<Object> queryParams = new ArrayList<>();

        // Helper to add conditions to the list
        BiConsumer<String, Object[]> addConditionWithParams = (condition, paramsArray) -> {
            whereConditions.add(condition);
            if (paramsArray != null) {
                Collections.addAll(queryParams, paramsArray);
            }
        };

        BiConsumer<String, Object> addSingleParamCondition = (condition, param) -> {
            whereConditions.add(condition);
            queryParams.add(param);
        };


        if (params.getOriginLocationIds() != null && !params.getOriginLocationIds().isEmpty()) {
            String inClausePlaceholders = String.join(",", Collections.nCopies(params.getOriginLocationIds().size(), "?"));
            whereConditions.add("tr.origin_location_id IN (" + inClausePlaceholders + ")");
            queryParams.addAll(params.getOriginLocationIds());
        }
        if (params.getDestinationLocationIds() != null && !params.getDestinationLocationIds().isEmpty()) {
            String inClausePlaceholders = String.join(",", Collections.nCopies(params.getDestinationLocationIds().size(), "?"));
            whereConditions.add("tr.destination_location_id IN (" + inClausePlaceholders + ")");
            queryParams.addAll(params.getDestinationLocationIds());
        }

        if (params.getDepartureFrom() != null && params.getDepartureTo() != null) {
            whereConditions.add("tr.departure_timestamp >= ? AND tr.departure_timestamp < ?");
            queryParams.add(params.getDepartureFrom());
            queryParams.add(params.getDepartureTo());
        } else if (params.getDepartureFrom() != null) {
            addSingleParamCondition.accept("tr.departure_timestamp >= ?", params.getDepartureFrom());
        } else if (params.getDepartureTo() != null) {
            addSingleParamCondition.accept("tr.departure_timestamp < ?", params.getDepartureTo());
        }

        if (params.getVehicleCompany() != null && !params.getVehicleCompany().trim().isEmpty()) {
            // Assuming you confirmed ILIKE works with your PostgreSQL and fixed any casing for table/column names
            addSingleParamCondition.accept("tr.vehicle_company ILIKE ?", "%" + params.getVehicleCompany() + "%");
        }

        if (params.getTripVehicle() != null) {
            // If tck.trip_vehicle is a PostgreSQL ENUM type, you might need:
            // addSingleParamCondition.accept("tck.trip_vehicle = CAST(? AS your_enum_type_in_db)", params.getTripVehicle().name());
            // Or tck.trip_vehicle = ?::your_enum_type_in_db
            // If it's VARCHAR storing enum names, this is fine:
            addSingleParamCondition.accept("tck.trip_vehicle = CAST(? AS trip_type)", params.getTripVehicle().name());

            switch (params.getTripVehicle()) {
                case BUS:
                    if (params.getBusClass() != null) {
                        fromParts.add("INNER JOIN buses b ON tr.trip_id = b.trip_id");
                        addSingleParamCondition.accept("b.class = CAST(? AS bus_class)", params.getBusClass().name());
                    }
                    break;
                case FLIGHT:
                    if (params.getFlightClass() != null) {
                        fromParts.add("INNER JOIN flights f ON tr.trip_id = f.trip_id");
                        addSingleParamCondition.accept("f.class = CAST(? AS flight_class)", params.getFlightClass().name());
                    }
                    break;
                case TRAIN:
                    if (params.getTrainStars() != null && params.getTrainStars() > 0) {
                        fromParts.add("INNER JOIN trains tn ON tr.trip_id = tn.trip_id");
                        addSingleParamCondition.accept("tn.stars >= ?", params.getTrainStars());
                    }
                    break;
            }
        }

        if (params.getAgeCategory() != null) {
            addSingleParamCondition.accept("tck.age = CAST(? AS age_range)", params.getAgeCategory().name());
        }
        if (params.getMinPrice() != null) {
            addSingleParamCondition.accept("tck.price >= ?", params.getMinPrice());
        }
        if (params.getMaxPrice() != null) {
            addSingleParamCondition.accept("tck.price <= ?", params.getMaxPrice());
        }

        // Build the final SQL string robustly
        String selectClause = "SELECT " + SELECT_COLUMNS;
        String fromClauseStr = String.join(" ", fromParts); // Joins "FROM base" and "INNER JOIN ..."

        String whereClauseStr = "";
        if (!whereConditions.isEmpty()) {
            whereClauseStr = "WHERE " + String.join(" AND ", whereConditions);
        }

        String orderByClause = "ORDER BY tr.departure_timestamp ASC, tck.price ASC";

        // Use Stream to filter out empty parts and join with spaces
        // This ensures single spaces between clauses and handles empty WHERE clause correctly
        String finalSql = Stream.of(selectClause, fromClauseStr, whereClauseStr, orderByClause)
                .map(String::trim) // Trim each part in case of accidental extra spaces
                .filter(s -> !s.isEmpty()) // Remove empty parts (e.g., if whereClauseStr is empty)
                .collect(Collectors.joining(" "));

        // For debugging, print the SQL and parameters before execution:
        System.out.println("Executing SQL: " + finalSql);
        System.out.println("With parameters: " + queryParams);

        return jdbcTemplate.query(finalSql, TICKET_WITH_TRIP_ROW_MAPPER, queryParams.toArray());
    }

}
