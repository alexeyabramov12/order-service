package com.example.orderservice.infrastructure.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Helper class for retrieving user-related context information in the current security context.
 * <p>
 * This class provides utility methods to check user roles and retrieve user-specific information,
 * such as the username or email, from the {@link SecurityContextHolder}.
 * </p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 *     <li>Determines if the currently authenticated user has a specific role (e.g., Admin).</li>
 *     <li>Provides the username or email of the currently authenticated user.</li>
 * </ul>
 *
 * <p>
 * Typical usage involves injecting this component into services or other beans that need to
 * access user-related information during application runtime.
 * </p>
 */
@Component
public class UserContextHelper {

    /**
     * Checks if the currently authenticated user has the "Admin" role.
     * <p>
     * This method retrieves the authentication details from the {@link SecurityContextHolder}
     * and checks if the user's authorities contain the "ROLE_Admin".
     * </p>
     *
     * @return {@code true} if the current user has the "Admin" role, {@code false} otherwise.
     */
    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_Admin"));
    }

    /**
     * Retrieves the email of the currently authenticated user.
     * <p>
     * This method fetches the authentication details from the {@link SecurityContextHolder}
     * and extracts the username (email) from the {@link UserDetails}.
     * </p>
     *
     * @return the email of the currently authenticated user.
     */
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
}
