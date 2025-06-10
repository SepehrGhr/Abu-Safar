package ir.ac.kntu.abusafar.repository;

import ir.ac.kntu.abusafar.model.User;
import ir.ac.kntu.abusafar.model.UserContact;
import ir.ac.kntu.abusafar.util.constants.enums.ContactType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface UserDAO {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    List<User> findAll();

    int update(User user);

    int deleteById(Long id);

    void saveContact(UserContact userContact);

    List<UserContact> findContactByUserId(Long userId);

    Optional<UserContact> findContactByUserIdAndType(Long userId, ContactType contactType);

    int deleteContact(Long userId, ContactType contactType);

    int updateWalletBalance(Long userId, BigDecimal newBalance);

}
