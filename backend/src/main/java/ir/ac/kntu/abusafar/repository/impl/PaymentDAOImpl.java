package ir.ac.kntu.abusafar.repository.impl;

import ir.ac.kntu.abusafar.model.Payment;
import ir.ac.kntu.abusafar.repository.PaymentDAO;
import ir.ac.kntu.abusafar.util.constants.enums.PaymentMeans;
import ir.ac.kntu.abusafar.util.constants.enums.PaymentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.List;

@Repository
public class PaymentDAOImpl implements PaymentDAO {

    private final JdbcTemplate jdbcTemplate;

    private static final String FIND_PENDING_PAYMENT_BY_RESERVATION_ID =
            "SELECT * FROM payments WHERE reservation_id = ? AND payment_status = 'PENDING'";
    private static final String UPDATE_PAYMENT_STATUS_SQL =
            "UPDATE payments SET payment_status = CAST(? AS payment_status) WHERE payment_id = ?";
    private static final String FIND_PAYMENT_BY_ID_SQL =
            "SELECT * FROM payments WHERE payment_id = ?";

    private final RowMapper<Payment> paymentRowMapper = (rs, rowNum) -> new Payment(
            rs.getLong("payment_id"),
            rs.getLong("reservation_id"),
            rs.getLong("user_id"),
            PaymentStatus.valueOf(rs.getString("payment_status").toUpperCase()),
            PaymentMeans.valueOf(rs.getString("payment_type").toUpperCase()),
            rs.getObject("payment_timestamp", OffsetDateTime.class),
            rs.getBigDecimal("price")
    );

    @Override
    public Optional<Payment> findById(Long paymentId) {
        try {
            Payment payment = jdbcTemplate.queryForObject(FIND_PAYMENT_BY_ID_SQL, paymentRowMapper, paymentId);
            return Optional.ofNullable(payment);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Autowired
    public PaymentDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Payment> findPendingPayment(Long reservationId) {
        try {
            Payment payment = jdbcTemplate.queryForObject(FIND_PENDING_PAYMENT_BY_RESERVATION_ID, paymentRowMapper, reservationId);
            return Optional.ofNullable(payment);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public int updatePaymentStatus(Long paymentId, PaymentStatus status) {
        return jdbcTemplate.update(UPDATE_PAYMENT_STATUS_SQL, status.name(), paymentId);
    }

    @Override
    public List<Payment> findByUserId(Long userId) {
        String sql = "SELECT * FROM payments WHERE user_id = ?";
        return jdbcTemplate.query(sql, paymentRowMapper, userId);
    }
}
