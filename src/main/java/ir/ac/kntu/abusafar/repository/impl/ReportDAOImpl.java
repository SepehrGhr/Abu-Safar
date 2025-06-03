package ir.ac.kntu.abusafar.repository.impl;

import ir.ac.kntu.abusafar.model.Report;
import ir.ac.kntu.abusafar.repository.ReportDAO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Map;

@Repository
public class ReportDAOImpl implements ReportDAO {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_REPORT_SQL = "INSERT INTO reports (user_id, link_type, link_id, topic, content, report_status) VALUES (?, CAST(? AS report_link), ?, ?, ?, CAST(? AS report_status))";

    public ReportDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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
}