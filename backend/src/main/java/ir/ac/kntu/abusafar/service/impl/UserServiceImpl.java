package ir.ac.kntu.abusafar.service.impl;

import ir.ac.kntu.abusafar.dto.user.UserUpdateRequestDTO;
import ir.ac.kntu.abusafar.dto.user.SignUpRequestDTO;
import ir.ac.kntu.abusafar.dto.user.UserInfoDTO;
import ir.ac.kntu.abusafar.exception.DuplicateContactInfoException;
import ir.ac.kntu.abusafar.exception.InsufficientBalanceException;
import ir.ac.kntu.abusafar.exception.UserNotFoundException;
import ir.ac.kntu.abusafar.mapper.UserMapper;
import ir.ac.kntu.abusafar.model.User;
import ir.ac.kntu.abusafar.model.UserContact;
import ir.ac.kntu.abusafar.repository.UserDAO;
import ir.ac.kntu.abusafar.service.UserCacheService;
import ir.ac.kntu.abusafar.service.UserService;
import ir.ac.kntu.abusafar.util.constants.enums.AccountStatus;
import ir.ac.kntu.abusafar.util.constants.enums.ContactType;
import ir.ac.kntu.abusafar.util.constants.enums.UserType;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;
    private final UserCacheService userCacheService;

    public UserServiceImpl(UserDAO userDAO, PasswordEncoder passwordEncoder, UserCacheService userCacheService) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
        this.userCacheService = userCacheService;
    }

    @Override
    public UserInfoDTO getUserInfoDTO(User user) {
        return UserMapper.INSTANCE.toDTO(user);
    }

    @Override
    public User createUserFromDTO(UserInfoDTO dto) {
        return UserMapper.INSTANCE.toEntity(dto);
    }

    @Override
    @Transactional
    @CachePut(value = "users", key = "#result.id")
    public UserInfoDTO signUp(SignUpRequestDTO signUpRequest) {
        if (signUpRequest.getEmail() != null && !signUpRequest.getEmail().isEmpty()) {
            if (userDAO.findByEmail(signUpRequest.getEmail()).isPresent()) {
                throw new DuplicateContactInfoException("Email address already in use: " + signUpRequest.getEmail());
            }
        }
        if (signUpRequest.getPhoneNumber() != null && !signUpRequest.getPhoneNumber().isEmpty()) {
            if (userDAO.findByPhoneNumber(signUpRequest.getPhoneNumber()).isPresent()) {
                throw new DuplicateContactInfoException("Phone number already in use: " + signUpRequest.getPhoneNumber());
            }
        }

        User user = new User(signUpRequest.getFirstName(), signUpRequest.getLastName(), UserType.USER,
                AccountStatus.ACTIVE, signUpRequest.getCity(), passwordEncoder.encode(signUpRequest.getPassword()),
                LocalDate.now());
        User savedUser = userDAO.save(user);
        if (savedUser.getId() == null) {
            throw new RuntimeException("Failed to save user and retrieve ID.");
        }

        if (signUpRequest.getEmail() != null && !signUpRequest.getEmail().isEmpty()) {
            UserContact emailContact = new UserContact(savedUser.getId(), ContactType.EMAIL, signUpRequest.getEmail());
            userDAO.saveContact(emailContact);
        }

        if (signUpRequest.getPhoneNumber() != null && !signUpRequest.getPhoneNumber().isEmpty()) {
            UserContact phoneContact = new UserContact(savedUser.getId(), ContactType.PHONE_NUMBER, signUpRequest.getPhoneNumber());
            userDAO.saveContact(phoneContact);
        }

        System.out.println("--- User created, putting in cache ---");
        return getUserInfoDTO(savedUser);
    }

    @Override
    @Cacheable(value = "users", key = "#userId")
    public UserInfoDTO findUserById(Long userId) {
        System.out.println("--- DB HIT: Finding user by id: " + userId + " ---");
        return userDAO.findById(userId)
                .map(UserMapper.INSTANCE::toDTO)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
    }

    @Override
    @Cacheable(value = "usersByEmail", key = "#email")
    public Optional<UserInfoDTO> findByEmail(String email) {
        System.out.println("--- DB HIT: Finding user by email: " + email + " ---");
        return userDAO.findByEmail(email)
                .map(UserMapper.INSTANCE::toDTO);
    }

    @Override
    @Cacheable(value = "usersByPhoneNumber", key = "#phoneNumber")
    public Optional<UserInfoDTO> findByPhoneNumber(String phoneNumber) {
        System.out.println("--- DB HIT: Finding user by phone: " + phoneNumber + " ---");
        return userDAO.findByPhoneNumber(phoneNumber)
                .map(UserMapper.INSTANCE::toDTO);
    }

    @Override
    @Transactional
    public UserInfoDTO updateUserInfo(Long userId, UserUpdateRequestDTO updatedInfo) {
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        List<UserContact> contacts = userDAO.findContactByUserId(userId);
        Optional<String> emailOpt = contacts.stream()
                .filter(c -> c.getContactType() == ContactType.EMAIL)
                .map(UserContact::getContactInfo).findFirst();
        Optional<String> phoneOpt = contacts.stream()
                .filter(c -> c.getContactType() == ContactType.PHONE_NUMBER)
                .map(UserContact::getContactInfo).findFirst();

        if (updatedInfo.getFirstName() != null) {
            user.setFirstName(updatedInfo.getFirstName());
        }
        if (updatedInfo.getLastName() != null) {
            user.setLastName(updatedInfo.getLastName());
        }
        if (updatedInfo.getCity() != null) {
            user.setCity(updatedInfo.getCity());
        }
        if (updatedInfo.getBirthdayDate() != null) {
            user.setBirthdayDate(updatedInfo.getBirthdayDate());
        }
        userDAO.update(user);

        if (updatedInfo.getEmail() != null) {
            userDAO.findByEmail(updatedInfo.getEmail()).ifPresent(existingUser -> {
                if (!existingUser.getId().equals(userId)) {
                    throw new DuplicateContactInfoException("Email is already in use by another account.");
                }
            });
            userDAO.deleteContact(userId, ContactType.EMAIL);
            userDAO.saveContact(new UserContact(userId, ContactType.EMAIL, updatedInfo.getEmail()));
        }

        if (updatedInfo.getPhoneNumber() != null) {
            userDAO.findByPhoneNumber(updatedInfo.getPhoneNumber()).ifPresent(existingUser -> {
                if (!existingUser.getId().equals(userId)) {
                    throw new DuplicateContactInfoException("Phone number is already in use by another account.");
                }
            });
            userDAO.deleteContact(userId, ContactType.PHONE_NUMBER);
            userDAO.saveContact(new UserContact(userId, ContactType.PHONE_NUMBER, updatedInfo.getPhoneNumber()));
        }

        System.out.println("--- Evicting user " + userId + " | " + emailOpt.orElse(null) + " | " + phoneOpt.orElse(null) + " ---");
        userCacheService.evictUserCaches(userId, emailOpt.orElse(null), phoneOpt.orElse(null));

        User updatedUserEntity = userDAO.findById(userId).get();
        return UserMapper.INSTANCE.toDTO(updatedUserEntity);
    }


    @Override
    @Transactional
    public void deleteUser(Long userId) {
        System.out.println("--- Deleting user, preparing to evict from all caches: " + userId + " ---");

        List<UserContact> contacts = userDAO.findContactByUserId(userId);
        Optional<String> emailOpt = contacts.stream()
                .filter(c -> c.getContactType() == ContactType.EMAIL)
                .map(UserContact::getContactInfo).findFirst();
        Optional<String> phoneOpt = contacts.stream()
                .filter(c -> c.getContactType() == ContactType.PHONE_NUMBER)
                .map(UserContact::getContactInfo).findFirst();

        int deletedRows = userDAO.deleteById(userId);
        if (deletedRows == 0) {
            return;
        }

        System.out.println("--- Evicting user " + userId + " | " + emailOpt.orElse(null) + " | " + phoneOpt.orElse(null) + " ---");
        userCacheService.evictUserCaches(userId, emailOpt.orElse(null), phoneOpt.orElse(null));
    }

    @Override
    @Transactional
    public void debitFromWallet(Long userId, BigDecimal amountToDebit) {
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        if (user.getWalletBalance().compareTo(amountToDebit) < 0) {
            throw new InsufficientBalanceException("Insufficient wallet balance. Required: " + amountToDebit + ", Available: " + user.getWalletBalance());
        }

        BigDecimal newBalance = user.getWalletBalance().subtract(amountToDebit);
        userDAO.updateWalletBalance(userId, newBalance);

        List<UserContact> contacts = userDAO.findContactByUserId(userId);
        Optional<String> emailOpt = contacts.stream().filter(c -> c.getContactType() == ContactType.EMAIL).map(UserContact::getContactInfo).findFirst();
        Optional<String> phoneOpt = contacts.stream().filter(c -> c.getContactType() == ContactType.PHONE_NUMBER).map(UserContact::getContactInfo).findFirst();
        userCacheService.evictUserCaches(userId, emailOpt.orElse(null), phoneOpt.orElse(null));
    }

    @Override
    @Transactional
    public UserInfoDTO chargeWallet(Long userId, BigDecimal amount) {
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        BigDecimal currentBalance = user.getWalletBalance();
        BigDecimal newBalance = currentBalance.add(amount);

        userDAO.updateWalletBalance(user.getId(), newBalance);
        user.setWalletBalance(newBalance);

        return getUserInfoDTO(user);
    }

}
