package com.example.orderservice.infrastructure.config.security;

import com.example.orderservice.domain.auth.Role;
import com.example.orderservice.domain.auth.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Factory class for creating {@link JwtUser} instances from {@link User} domain entities.
 * <p>
 * This class maps the application's domain-specific {@link User} and {@link Role} objects
 * to the Spring Security-compatible {@link JwtUser} and {@link GrantedAuthority} objects.
 */
@Component
public class JwtUserFactory {

    /**
     * Creates a {@link JwtUser} from a {@link User}.
     *
     * @param user the {@link User} entity to convert
     * @return a {@link JwtUser} instance containing user details and authorities
     */
    public JwtUser create(User user) {
        return new JwtUser(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword(),
                mapToGrantedAuthorities(new ArrayList<>(user.getRoles()))
        );
    }

    /**
     * Maps a list of {@link Role} entities to a list of {@link GrantedAuthority}.
     * <p>
     * This method converts each {@link Role} name into a {@link SimpleGrantedAuthority},
     * adding the `ROLE_` prefix to ensure compatibility with Spring Security role checks.
     *
     * @param userRoles the list of {@link Role} entities
     * @return a list of {@link GrantedAuthority} corresponding to the user roles
     */
    private List<GrantedAuthority> mapToGrantedAuthorities(List<Role> userRoles) {
        return userRoles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());
    }
}
