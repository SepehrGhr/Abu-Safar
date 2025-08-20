package ir.ac.kntu.abusafar.repository.impl;

import ir.ac.kntu.abusafar.model.Company;
import ir.ac.kntu.abusafar.repository.CompanyDAO;
import ir.ac.kntu.abusafar.util.constants.enums.TripType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CompanyDAOImpl implements CompanyDAO {

    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_COMPANY_BY_ID_SQL = "SELECT * FROM companies WHERE company_id = ?";
    private static final String SELECT_COMPANY_BY_NAME_SQL = "SELECT * FROM companies WHERE name ILIKE ?";
    private static final String SELECT_COMPANIES_BY_VEHICLE_TYPE_SQL = "SELECT * FROM companies WHERE vehicle_type = CAST(? AS trip_type)";

    @Autowired
    public CompanyDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Company> companyRowMapper = (rs, rowNum) -> new Company(
            rs.getLong("company_id"),
            rs.getString("name"),
            TripType.valueOf(rs.getString("vehicle_type").toUpperCase()),
            rs.getBigDecimal("cancellation_penalty_rate"),
            rs.getString("logo_picture_url"),
            rs.getString("description"),
            rs.getBoolean("is_active")
    );

    @Override
    public Optional<Company> findById(Long id) {
        try {
            Company company = jdbcTemplate.queryForObject(SELECT_COMPANY_BY_ID_SQL, companyRowMapper, id);
            return Optional.ofNullable(company);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Company> findByName(String name) {
        try {
            Company company = jdbcTemplate.queryForObject(SELECT_COMPANY_BY_NAME_SQL, companyRowMapper, name);
            return Optional.ofNullable(company);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Company> findByVehicleType(String vehicleType) {
        return jdbcTemplate.query(SELECT_COMPANIES_BY_VEHICLE_TYPE_SQL, companyRowMapper, vehicleType);
    }
}