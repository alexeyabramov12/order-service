package com.example.orderservice.presentation.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for user authentication requests.
 * Contains fields for email and password along with validation constraints.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticateDto {

    /**
     * The email of the user attempting to authenticate.
     * Must be a valid email format and cannot be blank.
     */
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    /**
     * The password of the user attempting to authenticate.
     * Cannot be blank.
     */
    @NotBlank(message = "Password is required")
    private String password;
}
