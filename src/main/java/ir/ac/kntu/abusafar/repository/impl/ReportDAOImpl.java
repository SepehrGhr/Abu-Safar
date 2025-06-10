package ir.ac.kntu.abusafar.repository.impl;

import ir.ac.kntu.abusafar.model.Report;
import ir.ac.kntu.abusafar.repository.ReportDAO;
import ir.ac.kntu.abusafar.util.constants.enums.ReportLink;
import ir.ac.kntu.abusafar.util.constants.enums.ReportStatus;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ReportDAOImpl implements ReportDAO {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_REPORT_SQL = "INSERT INTO reports (user_id, link_type, link_id, topic, content, report_status) VALUES (?, CAST(? AS report_link), ?, ?, ?, CAST(? AS report_status))";
    private static final String SELECT_REPORT_BY_ID_SQL = "SELECT * FROM reports WHERE report_id = ?";
    private static final String SELECT_ALL_REPORTS_SQL = "SELECT * FROM reports ORDER BY report_id DESC";
    private static final String SELECT_REPORTS_BY_USER_ID_SQL = "SELECT * FROM reports WHERE user_id = ? ORDER BY report_id DESC";

    public ReportDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Report> reportRowMapper = (rs, rowNum) -> new Report(
            rs.getLong("report_id"),
            rs.getLong("user_id"),
            ReportLink.valueOf(rs.getString("link_type").toUpperCase()),
            rs.getLong("link_id"),
            rs.getString("topic"),
            rs.getString("content"),
            ReportStatus.valueOf(rs.getString("report_status").toUpperCase())
    );

    @Override
    public Report save(Report report) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_REPORT_SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, report.getUserId());
            ps.setString(2, report.getLinkType().name());
            ps.setLong(3, report.getLinkId());
            ps.setString(4, report.getTopic());
            ps.setString(5, report.getContent());
            ps.setString(6, report.getReportStatus().name());
            return ps;
        }, keyHolder);
        Map<String, Object> keys = keyHolder.getKeys();
        if (keys != null && keys.containsKey("report_id")) {
            report.setReportId(((Number) keys.get("report_id")).longValue());
        } else if (keyHolder.getKey() != null) {
            report.setReportId(keyHolder.getKey().longValue());
        } else {
            throw new IllegalStateException("Could not retrieve generated key for report.");
        }

        return report;
    }

    @Override
    public Optional<Report> findById(Long reportId) {
        try {
            Report report = jdbcTemplate.queryForObject(SELECT_REPORT_BY_ID_SQL, reportRowMapper, reportId);
            return Optional.ofNullable(report);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Report> findAll() {
        return jdbcTemplate.query(SELECT_ALL_REPORTS_SQL, reportRowMapper);
    }

    @Override
    public List<Report> findAllByUserId(Long userId) {
        return jdbcTemplate.query(SELECT_REPORTS_BY_USER_ID_SQL, reportRowMapper, userId);
    }
}