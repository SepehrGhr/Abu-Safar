package ir.ac.kntu.abusafar.repository.impl;

import ir.ac.kntu.abusafar.dto.ticket.TicketResultItemDTO;
import ir.ac.kntu.abusafar.model.Ticket;
import ir.ac.kntu.abusafar.model.Trip;
import ir.ac.kntu.abusafar.repository.TicketDAO;
import ir.ac.kntu.abusafar.repository.params.TicketSearchParameters;
import ir.ac.kntu.abusafar.util.constants.enums.AgeRange;
import ir.ac.kntu.abusafar.util.constants.enums.TripType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;


import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class TicketDAOImpl implements TicketDAO {
    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_COLUMNS =
            "tck.age AS tck_age, tck.price AS tck_price, tck.trip_vehicle AS tck_trip_vehicle, " +
                    "tr.trip_id, tr.origin_location_id, tr.destination_location_id, " +
                    "tr.departure_timestamp, tr.arrival_timestamp, " +
                    "tr.stop_count, tr.total_capacity, tr.reserved_capacity, " +
                    "c.company_id AS comp_id, c.name AS comp_name ";

    private static final String FROM_CLAUSE_BASE =
            "tickets tck " +
                    "INNER JOIN trips tr ON tck.trip_id = tr.trip_id " +
                    "INNER JOIN companies c ON tr.company_id = c.company_id ";

    private final RowMapper<Ticket> TICKET_WITH_TRIP_ROW_MAPPER = (rs, rowNum) -> {
        Trip trip = new Trip(
                rs.getLong("trip_id"),
                rs.getLong("origin_location_id"),
                rs.getLong("destination_location_id"),
                rs.getObject("departure_timestamp", OffsetDateTime.class),
                rs.getObject("arrival_timestamp", OffsetDateTime.class),
                rs.getLong("comp_id"),
                rs.getShort("stop_count"),
                rs.getShort("total_capacity"),
                rs.getShort("reserved_capacity")
        );
        Ticket ticket = new Ticket();
        ticket.setAge(AgeRange.valueOf(rs.getString("tck_age").toUpperCase()));
        ticket.setPrice(rs.getBigDecimal("tck_price"));
        ticket.setTripVehicle(TripType.valueOf(rs.getString("tck_trip_vehicle").toUpperCase()));
        ticket.setTrip(trip);

        return ticket;
    };

    private final RowMapper<TicketResultItemDTO> TICKET_RESULT_ITEM_ROW_MAPPER = (rs, rowNum) -> {
        TicketResultItemDTO dto = new TicketResultItemDTO();
        dto.setTripId(rs.getLong("trip_id"));
        dto.setAge(AgeRange.valueOf(rs.getString("tck_age").toUpperCase()));
        dto.setDepartureTimestamp(rs.getObject("departure_timestamp", OffsetDateTime.class));
        dto.setArrivalTimestamp(rs.getObject("arrival_timestamp", OffsetDateTime.class));
        dto.setTripVehicle(TripType.valueOf(rs.getString("tck_trip_vehicle").toUpperCase()));
        dto.setPrice(rs.getBigDecimal("tck_price"));
        dto.setVehicleCompany(rs.getString("comp_name"));

        return dto;
    };

    @Autowired
    public TicketDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public List<Ticket> findTicketsByCriteria(TicketSearchParameters params) {
        List<String> fromParts = new ArrayList<>();
        fromParts.add("FROM " + FROM_CLAUSE_BASE);

        List<String> whereConditions = new ArrayList<>();
        List<Object> queryParams = new ArrayList<>();

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
            addSingleParamCondition.accept("c.name ILIKE ?", "%" + params.getVehicleCompany() + "%");
        }

        if (params.getTripVehicle() != null) {
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

        String selectClause = "SELECT " + SELECT_COLUMNS;
        String fromClauseStr = String.join(" ", fromParts);

        String whereClauseStr = "";
        if (!whereConditions.isEmpty()) {
            whereClauseStr = "WHERE " + String.join(" AND ", whereConditions);
        }

        String orderByClause = "ORDER BY tr.departure_timestamp ASC, tck.price ASC";

        String finalSql = Stream.of(selectClause, fromClauseStr, whereClauseStr, orderByClause)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(" "));

        System.out.println("Executing SQL: " + finalSql);
        System.out.println("With parameters: " + queryParams);

        return jdbcTemplate.query(finalSql, TICKET_WITH_TRIP_ROW_MAPPER, queryParams.toArray());
    }

    @Override
    public Optional<Ticket> findById(Long tripId, AgeRange age) {
        if (tripId == null || age == null) {
            return Optional.empty();
        }
        String sql = "SELECT " +
                "tck.trip_id, " +
                "tck.age AS tck_age, " +
                "tck.price AS tck_price, " +
                "tck.trip_vehicle AS tck_trip_vehicle, " +
                "trp.origin_location_id, trp.destination_location_id, " +
                "trp.departure_timestamp, trp.arrival_timestamp, trp.vehicle_company, " +
                "trp.stop_count, trp.total_capacity, trp.reserved_capacity " +
                "FROM tickets tck " +
                "JOIN trips trp ON tck.trip_id = trp.trip_id " +
                "WHERE tck.trip_id = ? AND tck.age = CAST(? AS age_range)";

        try {
            Ticket ticket = jdbcTemplate.queryForObject(sql, TICKET_WITH_TRIP_ROW_MAPPER, tripId, age.name());
            return Optional.ofNullable(ticket);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
