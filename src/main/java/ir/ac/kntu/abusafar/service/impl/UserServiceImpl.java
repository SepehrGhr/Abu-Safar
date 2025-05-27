package ir.ac.kntu.abusafar.service.impl;

import ir.ac.kntu.abusafar.dto.User.SignUpRequestDTO;
import ir.ac.kntu.abusafar.exception.DuplicateContactInfoException;
import ir.ac.kntu.abusafar.model.User;
import ir.ac.kntu.abusafar.model.UserContact;
import ir.ac.kntu.abusafar.repository.UserDAO;
import ir.ac.kntu.abusafar.service.UserService;
import ir.ac.kntu.abusafar.util.constants.enums.AccountStatus;
import ir.ac.kntu.abusafar.util.constants.enums.ContactType;
import ir.ac.kntu.abusafar.util.constants.enums.UserType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserDAO userDAO, PasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User signUp(SignUpRequestDTO signUpRequest) {
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
        return savedUser;
    }
}
