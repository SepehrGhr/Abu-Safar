package ir.ac.kntu.abusafar.repository.impl;

import ir.ac.kntu.abusafar.model.User;
import ir.ac.kntu.abusafar.model.UserContact;
import ir.ac.kntu.abusafar.repository.UserDAO;
import ir.ac.kntu.abusafar.util.constants.enums.AccountStatus;
import ir.ac.kntu.abusafar.util.constants.enums.ContactType;
import ir.ac.kntu.abusafar.util.constants.enums.UserType;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDAOImpl implements UserDAO {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_USER_SQL = "INSERT INTO users (first_name, last_name, user_role, account_status, city, hashed_password, profile_picture) VALUES (?, ?, CAST(? AS user_type), CAST(? AS account_status), ?, ?, ?)";
    private static final String SELECT_USER_BY_ID_SQL = "SELECT * FROM users WHERE user_id = ?";
    private static final String SELECT_USER_BY_CONTACT_INFO_SQL = "SELECT u.* " +
            "FROM users u JOIN user_contact uc ON u.user_id = uc.user_id " +
            "WHERE uc.contact_info = ? AND uc.contact_type = CAST(? AS contact_type)";
    private static final String SELECT_ALL_USERS_SQL = "SELECT * FROM users";
    private static final String UPDATE_USER_SQL = "UPDATE users SET first_name = ?, last_name = ?, user_role = CAST(? AS user_type), account_status = CAST(? AS account_status), city = ?, hashed_password = ?, profile_picture = ? WHERE user_id = ?";
    private static final String DELETE_USER_BY_ID_SQL = "DELETE FROM users WHERE user_id = ?";
    private static final String INSERT_USER_CONTACT_SQL = "INSERT INTO user_contact (user_id, contact_type, contact_info) VALUES (?, CAST(? AS contact_type), ?)";
    private static final String SELECT_CONTACTS_BY_USER_ID_SQL = "SELECT user_id, contact_type, contact_info FROM user_contact WHERE user_id = ?";
    private static final String SELECT_CONTACT_BY_USER_ID_AND_TYPE_SQL = "SELECT user_id, contact_type, contact_info FROM user_contact WHERE user_id = ? AND contact_type = CAST(? AS contact_type)";
    private static final String DELETE_CONTACT_SQL = "DELETE FROM user_contact WHERE user_id = ? AND contact_type = CAST(? AS contact_type)";

    public UserDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<User> userRowMapper = (rs, rowNum) -> new User(
            rs.getLong("user_id"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            UserType.valueOf(rs.getString("user_role").toUpperCase()),
            AccountStatus.valueOf(rs.getString("account_status").toUpperCase()),
            rs.getString("city"),
            rs.getString("hashed_password"),
            rs.getObject("sign_up_date", OffsetDateTime.class).toLocalDate(),
            rs.getString("profile_picture")
    );

    private RowMapper<UserContact> userContactRowMapper = (rs, rowNum) -> new UserContact(
            rs.getLong("user_id"),
            ContactType.valueOf(rs.getString("contact_type").toUpperCase()),
            rs.getString("contact_info")
    );

    @Override
    public User save(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_USER_SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getUserType().name());
            ps.setString(4, user.getAccountStatus().name());
            ps.setString(5, user.getCity());
            ps.setString(6, user.getHashedPassword());
            ps.setString(7, user.getProfilePicture());
            return ps;
        }, keyHolder);

        keyHolder.getKeys();
        if (keyHolder.getKeys().containsKey("user_id")) {
            user.setId((Long) keyHolder.getKeys().get("user_id"));
        } else {
            keyHolder.getKey();
            user.setId(keyHolder.getKey().longValue());
        }

        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        try {
            User user = jdbcTemplate.queryForObject(SELECT_USER_BY_ID_SQL, userRowMapper, id);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try {
            User user = jdbcTemplate.queryForObject(SELECT_USER_BY_CONTACT_INFO_SQL, userRowMapper, email, ContactType.EMAIL.name());
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        try {
            User user = jdbcTemplate.queryForObject(SELECT_USER_BY_CONTACT_INFO_SQL, userRowMapper, phoneNumber, ContactType.PHONE_NUMBER.name());
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query(SELECT_ALL_USERS_SQL, userRowMapper);
    }

    @Override
    public int update(User user) {
        return jdbcTemplate.update(UPDATE_USER_SQL,
                user.getFirstName(),
                user.getLastName(),
                user.getUserType().name(),
                user.getAccountStatus().name(),
                user.getCity(),
                user.getHashedPassword(),
                user.getProfilePicture(),
                user.getId());
    }

    @Override
    public int deleteById(Long id) {
        return jdbcTemplate.update(DELETE_USER_BY_ID_SQL, id);
    }

    @Override
    public void saveContact(UserContact userContact) {
        jdbcTemplate.update(INSERT_USER_CONTACT_SQL,
                userContact.getUserId(),
                userContact.getContactType().name(),
                userContact.getContactInfo());
    }

    @Override
    public List<UserContact> findContactByUserId(Long userId) {
        return jdbcTemplate.query(SELECT_CONTACTS_BY_USER_ID_SQL, userContactRowMapper, userId);
    }

    @Override
    public Optional<UserContact> findContactByUserIdAndType(Long userId, ContactType contactType) {
        try {
            UserContact contact = jdbcTemplate.queryForObject(SELECT_CONTACT_BY_USER_ID_AND_TYPE_SQL, userContactRowMapper, userId, contactType.name());
            return Optional.ofNullable(contact);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public int deleteContact(Long userId, ContactType contactType) {
        return jdbcTemplate.update(DELETE_CONTACT_SQL, userId, contactType.name());
    }
}
