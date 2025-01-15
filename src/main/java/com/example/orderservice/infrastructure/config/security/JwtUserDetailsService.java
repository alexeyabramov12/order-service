package com.example.orderservice.infrastructure.config.security;

import com.example.orderservice.domain.auth.User;
import com.example.orderservice.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link UserDetailsService} that integrates with the application's domain model.
 * <p>
 * This service is responsible for loading user details from the database and converting
 * them into a format compatible with Spring Security using {@link JwtUserFactory}.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    /**
     * Repository for interacting with the user database.
     */
    private final UserRepository repository;

    /**
     * Factory for creating {@link JwtUser} instances from domain-specific {@link User} objects.
     */
    private final JwtUserFactory jwtUserFactory;

    /**
     * Loads a user's details based on their email.
     * <p>
     * This method fetches a {@link User} entity from the database using the provided email.
     * If the user is found, it is converted into a {@link JwtUser} using the {@link JwtUserFactory}.
     * If the user is not found, a {@link UsernameNotFoundException} is thrown.
     *
     * @param email the email of the user to load
     * @return a {@link UserDetails} object containing the user's information
     * @throws UsernameNotFoundException if no user is found with the specified email
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Fetch user from the repository
        User user = repository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", email);
                    return new UsernameNotFoundException("User not found");
                });

        // Convert User to JwtUser using JwtUserFactory
        log.info("Successfully loaded user: {}", user.getEmail());
        return jwtUserFactory.create(user);
    }
}
