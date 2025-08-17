package ir.ac.kntu.abusafar.repository.impl;

import ir.ac.kntu.abusafar.dto.ticket.TicketResultItemDTO;
import ir.ac.kntu.abusafar.model.Ticket;
import ir.ac.kntu.abusafar.model.Trip;
import ir.ac.kntu.abusafar.repository.TicketDAO;
import ir.ac.kntu.abusafar.repository.params.TicketSearchParameters;
import ir.ac.kntu.abusafar.util.constants.enums.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;


import java.math.BigDecimal;
import java.sql.Array;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class TicketDAOImpl implements TicketDAO {
    private final JdbcTemplate jdbcTemplate;

    public record DenormalizedTicketData(
            Long tripId, AgeRange age, BigDecimal price, TripType tripVehicle,
            OffsetDateTime departureTimestamp, OffsetDateTime arrivalTimestamp, Short stopCount,
            Integer totalCapacity, Integer reservedCapacity,
            String companyName, String companyLogo,
            String originCity, String originProvince, String originCountry,
            String destCity, String destProvince, String destCountry,
            Short trainStars, TrainRoomType trainRoomType,
            FlightClass flightClass, String departureAirport, String arrivalAirport,
            BusClass busClass, BusChairCountType chairType,
            List<ServiceType> services
    ) {
    }

    private static final String FULLY_JOINED_SELECT_SQL = """
            SELECT
                tck.age, tck.price, tck.trip_vehicle,
                tr.trip_id, tr.departure_timestamp, tr.arrival_timestamp, tr.stop_count, tr.total_capacity, tr.reserved_capacity,
                c.name AS company_name, c.logo_picture_url AS company_logo,
                ol.city AS origin_city, ol.province AS origin_province, ol.country AS origin_country,
                dl.city AS dest_city, dl.province AS dest_province, dl.country AS dest_country,
                tn.stars AS train_stars, tn.room_type AS train_room_type,
                f.class AS flight_class, f.departure_airport, f.arrival_airport,
                b.class AS bus_class, b.chair_type,
                ARRAY_AGG(ads.service_type) FILTER (WHERE ads.service_type IS NOT NULL) AS services
            FROM tickets tck
            INNER JOIN trips tr ON tck.trip_id = tr.trip_id
            INNER JOIN companies c ON tr.company_id = c.company_id
            INNER JOIN location_details ol ON tr.origin_location_id = ol.location_id
            INNER JOIN location_details dl ON tr.destination_location_id = dl.location_id
            LEFT JOIN trains tn ON tr.trip_id = tn.trip_id
            LEFT JOIN flights f ON tr.trip_id = f.trip_id
            LEFT JOIN buses b ON tr.trip_id = b.trip_id
            LEFT JOIN additional_services ads ON tr.trip_id = ads.trip_id
            """;

    private final RowMapper<DenormalizedTicketData> DENORMALIZED_TICKET_ROW_MAPPER = (rs, rowNum) -> new DenormalizedTicketData(
            rs.getLong("trip_id"),
            AgeRange.valueOf(rs.getString("age").toUpperCase()),
            rs.getBigDecimal("price"),
            TripType.valueOf(rs.getString("trip_vehicle").toUpperCase()),
            rs.getObject("departure_timestamp", OffsetDateTime.class),
            rs.getObject("arrival_timestamp", OffsetDateTime.class),
            rs.getShort("stop_count"),
            rs.getInt("total_capacity"),
            rs.getInt("reserved_capacity"),
            rs.getString("company_name"),
            rs.getString("company_logo"),
            rs.getString("origin_city"),
            rs.getString("origin_province"),
            rs.getString("origin_country"),
            rs.getString("dest_city"),
            rs.getString("dest_province"),
            rs.getString("dest_country"),
            rs.getObject("train_stars", Short.class),
            rs.getString("train_room_type") != null ? TrainRoomType.fromString(rs.getString("train_room_type")) : null,            rs.getString("flight_class") != null ? FlightClass.fromString(rs.getString("flight_class")) : null,
            rs.getString("departure_airport"),
            rs.getString("arrival_airport"),
            rs.getString("bus_class") != null ? BusClass.fromString(rs.getString("bus_class")) : null,
            rs.getString("chair_type") != null ? BusChairCountType.getEnumValue(rs.getString("chair_type")) : null,
            convertSqlArrayToList(rs.getArray("services"), ServiceType::getEnumName)
    );

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
        return new TicketResultItemDTO(
                rs.getLong("trip_id"),
                AgeRange.valueOf(rs.getString("tck_age").toUpperCase()),
                null,
                null,
                rs.getObject("departure_timestamp", OffsetDateTime.class),
                rs.getObject("arrival_timestamp", OffsetDateTime.class),
                TripType.valueOf(rs.getString("tck_trip_vehicle").toUpperCase()),
                rs.getBigDecimal("tck_price"),
                rs.getString("comp_name")
        );
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
                        addSingleParamCondition.accept("b.class = CAST(? AS bus_class)", params.getBusClass().getDbValue());
                    }
                    break;
                case FLIGHT:
                    if (params.getFlightClass() != null) {
                        fromParts.add("INNER JOIN flights f ON tr.trip_id = f.trip_id");
                        addSingleParamCondition.accept("f.class = CAST(? AS flight_class)", params.getFlightClass().getDbValue());
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
    public Optional<DenormalizedTicketData> findFullyJoinedTicket(Long tripId, AgeRange age) {
        if (tripId == null || age == null) {
            return Optional.empty();
        }
        String sql = FULLY_JOINED_SELECT_SQL
                + "WHERE tck.trip_id = ? AND tck.age = CAST(? AS age_range) "
                + "GROUP BY tck.trip_id, tck.age, tr.trip_id, c.company_id, ol.location_id, dl.location_id, tn.trip_id, f.trip_id, b.trip_id";

        try {
            DenormalizedTicketData data = jdbcTemplate.queryForObject(sql, DENORMALIZED_TICKET_ROW_MAPPER, tripId, age.name());
            return Optional.ofNullable(data);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Ticket> findById(Long tripId, AgeRange age) {
        if (tripId == null || age == null) {
            return Optional.empty();
        }
        String sql = "SELECT " + SELECT_COLUMNS +
                "FROM " + FROM_CLAUSE_BASE +
                "WHERE tck.trip_id = ? AND tck.age = CAST(? AS age_range)";

        try {
            Ticket ticket = jdbcTemplate.queryForObject(sql, TICKET_WITH_TRIP_ROW_MAPPER, tripId, age.name());
            return Optional.ofNullable(ticket);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private <T extends Enum<T>> List<T> convertSqlArrayToList(Array sqlArray, Function< String, T > enumConverter) throws SQLException {
        if (sqlArray == null) {
            return Collections.emptyList();
        }
        String[] stringArray = (String[]) sqlArray.getArray();
        return Arrays.stream(stringArray).map(enumConverter).collect(Collectors.toList());
    }
}
