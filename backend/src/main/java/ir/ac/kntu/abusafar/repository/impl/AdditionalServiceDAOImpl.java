package ir.ac.kntu.abusafar.repository.impl;

import ir.ac.kntu.abusafar.repository.AdditionalServiceDAO;
import ir.ac.kntu.abusafar.util.constants.enums.ServiceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class AdditionalServiceDAOImpl implements AdditionalServiceDAO {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AdditionalServiceDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<ServiceType> SERVICE_TYPE_ROW_MAPPER = (rs, rowNum) -> {
        String serviceTypeString = rs.getString("service_type");
        try {
            return ServiceType.getEnumName(serviceTypeString);
        } catch (IllegalArgumentException e) {
            System.err.println("Warning: Unrecognized service_type '" + serviceTypeString + "' from database for trip_id.");
            throw e;
        }
    };


    @Override
    public List<ServiceType> findServiceTypesByTripId(Long tripId) {
        if (tripId == null) {
            return Collections.emptyList();
        }

        String sql = "SELECT service_type FROM additional_services WHERE trip_id = ?";

        try {
            List<ServiceType> services = jdbcTemplate.query(sql, SERVICE_TYPE_ROW_MAPPER, tripId);
            return services;
        } catch (Exception e) {
            System.err.println("Error fetching additional services for trip ID " + tripId + ": " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
