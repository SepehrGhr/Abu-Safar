package ir.ac.kntu.abusafar.security.service;

import ir.ac.kntu.abusafar.model.User;
import ir.ac.kntu.abusafar.repository.UserDAO;
import ir.ac.kntu.abusafar.util.constants.enums.AccountStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private final UserDAO userDAO;

    public UserDetailsServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String userIdString) throws UsernameNotFoundException {
        Long userId;
        try {
            userId = Long.parseLong(userIdString);
        } catch (NumberFormatException e) {
            LOGGER.error("Could not parse userIdString to Long: {}", userIdString, e);
            throw new UsernameNotFoundException("Invalid user identifier format: " + userIdString);
        }
        Optional<User> userOptional = userDAO.findById(userId);
        User applicationUser = userOptional.orElseThrow(() ->
                new UsernameNotFoundException("User not found with ID: " + userId)
        );
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + applicationUser.getUserType().name());
        Collection<GrantedAuthority> authorities = Collections.singletonList(authority);

        boolean enabled = applicationUser.getAccountStatus() == AccountStatus.ACTIVE;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;

        return new org.springframework.security.core.userdetails.User(
                userIdString,
                applicationUser.getHashedPassword(),
                enabled,
                accountNonExpired,
                credentialsNonExpired,
                accountNonLocked,
                authorities
        );
    }
}