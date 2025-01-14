package com.example.orderservice.infrastructure.config.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Implementation of {@link UserDetails} that represents a user authenticated via JWT.
 * <p>
 * This class is used by Spring Security to provide the necessary information about
 * the user, such as username (email), password, and granted authorities (roles).
 * It also provides default values for account status methods.
 */
@Getter
@Setter
@AllArgsConstructor
public class JwtUser implements UserDetails {

    /**
     * The unique identifier of the user.
     */
    private Long id;

    /**
     * The first name of the user.
     */
    private String firstName;

    /**
     * The last name of the user.
     */
    private String lastName;

    /**
     * The email of the user, used as the username in the authentication process.
     */
    private String email;

    /**
     * The password of the user, used for authentication.
     */
    private String password;

    /**
     * The authorities (roles) granted to the user.
     */
    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * Returns the authorities granted to the user.
     *
     * @return a collection of {@link GrantedAuthority}
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * Returns the password used to authenticate the user.
     * This value is ignored during JSON serialization.
     *
     * @return the password
     */
    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Returns the username (email) used to authenticate the user.
     *
     * @return the email
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Indicates whether the user's account has expired.
     * This implementation always returns {@code true}, meaning the account is not expired.
     *
     * @return {@code true} if the account is not expired
     */
    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked.
     * This implementation always returns {@code true}, meaning the account is not locked.
     *
     * @return {@code true} if the account is not locked
     */
    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials (password) have expired.
     * This implementation always returns {@code true}, meaning the credentials are valid.
     *
     * @return {@code true} if the credentials are not expired
     */
    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user's account is enabled.
     * This implementation always returns {@code true}, meaning the account is enabled.
     *
     * @return {@code true} if the account is enabled
     */
    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }
}
