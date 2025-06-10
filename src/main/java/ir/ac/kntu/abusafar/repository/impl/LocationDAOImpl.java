package ir.ac.kntu.abusafar.repository.impl;

import ir.ac.kntu.abusafar.model.Location;
import ir.ac.kntu.abusafar.repository.LocationDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;


@Repository
public class LocationDAOImpl implements LocationDAO {

    private final JdbcTemplate jdbcTemplate;
    private static final String SELECT_LOCATION_BY_ID_SQL =
            "SELECT location_id, country, province, city FROM location_details WHERE location_id = ?;";
    private static final String SELECT_CITIES_BY_PROVINCE_SQL =
            "SELECT DISTINCT city FROM location_details WHERE province ILIKE ? ORDER BY city;";
    private static final String SELECT_PROVINCES_BY_COUNTRY_SQL =
            "SELECT DISTINCT province FROM location_details WHERE country ILIKE ? ORDER BY province;";
    private static final String SELECT_LOCATIONS_BY_CITY_SQL =
            "SELECT location_id, country, province, city FROM location_details WHERE city ILIKE ? ORDER BY country, province;";
    private static final String SELECT_LOCATIONS_BY_PROVINCE_SQL =
            "SELECT location_id, country, province, city FROM location_details WHERE province ILIKE ? ORDER BY country, city;";
    private static final String SELECT_LOCATIONS_BY_COUNTRY_SQL =
            "SELECT location_id, country, province, city FROM location_details WHERE country ILIKE ? ORDER BY province, city;";
    private static final String SELECT_ALL_COUNTRIES_SQL =
            "SELECT DISTINCT country FROM location_details ORDER BY country;";
    private static final String SELECT_ALL_PROVINCES_SQL =
            "SELECT DISTINCT province FROM location_details ORDER BY province;";
    private static final String SELECT_ALL_CITIES_SQL =
            "SELECT DISTINCT city FROM location_details ORDER BY city;";

    @Autowired
    public LocationDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Location> LOCATION_ROW_MAPPER = (rs, rowNum) -> new Location(
            rs.getLong("location_id"),
            rs.getString("country"),
            rs.getString("province"),
            rs.getString("city"));

    @Override
    public Optional<Location> findById(Long locationId) {
        try {
            Location location = jdbcTemplate.queryForObject(SELECT_LOCATION_BY_ID_SQL, LOCATION_ROW_MAPPER, locationId);
            return Optional.ofNullable(location);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<String> findCitiesByProvince(String provinceName) {
        return jdbcTemplate.queryForList(SELECT_CITIES_BY_PROVINCE_SQL, String.class, provinceName);
    }

    @Override
    public List<String> findProvincesByCountry(String countryName) {
        return jdbcTemplate.queryForList(SELECT_PROVINCES_BY_COUNTRY_SQL, String.class, countryName);
    }

    @Override
    public List<Location> findByCity(String cityName) {
        return jdbcTemplate.query(SELECT_LOCATIONS_BY_CITY_SQL, LOCATION_ROW_MAPPER, cityName);
    }

    @Override
    public List<Location> findByProvince(String provinceName) {
        return jdbcTemplate.query(SELECT_LOCATIONS_BY_PROVINCE_SQL, LOCATION_ROW_MAPPER, provinceName);
    }

    @Override
    public List<Location> findByCountry(String countryName) {
        return jdbcTemplate.query(SELECT_LOCATIONS_BY_COUNTRY_SQL, LOCATION_ROW_MAPPER, countryName);
    }

    @Override
    public List<String> findAllCountries() {
        return jdbcTemplate.queryForList(SELECT_ALL_COUNTRIES_SQL, String.class);
    }

    @Override
    public List<String> findAllProvinces() {
        return jdbcTemplate.queryForList(SELECT_ALL_PROVINCES_SQL, String.class);
    }

    @Override
    public List<String> findAllCities() {
        return jdbcTemplate.queryForList(SELECT_ALL_CITIES_SQL, String.class);
    }

    public List<Long> findLocationIdByDetails(String city, String province, String country) {
        StringBuilder sqlWhereClause = new StringBuilder();
        List<Object> params = new ArrayList<>();

        BiConsumer<StringBuilder, String> addCondition = (sb, conditionToken) -> {
            if (sb.isEmpty()) {
                sb.append("WHERE ");
            } else {
                sb.append("AND ");
            }
            sb.append(conditionToken).append(" ILIKE ? ");
        };

        if (StringUtils.hasText(city)) {
            addCondition.accept(sqlWhereClause, "city");
            params.add(city);
        }
        if (StringUtils.hasText(province)) {
            addCondition.accept(sqlWhereClause, "province");
            params.add(province);
        }
        if (StringUtils.hasText(country)) {
            addCondition.accept(sqlWhereClause, "country");
            params.add(country);
        }
        if (params.isEmpty()) {
            return Collections.emptyList();
        }

        String finalSql = "SELECT location_id FROM location_details " + sqlWhereClause + "ORDER BY location_id";

        return jdbcTemplate.queryForList(finalSql, Long.class, params.toArray());
    }

}
