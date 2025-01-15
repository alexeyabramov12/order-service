package com.example.orderservice.infrastructure.repository;

import com.example.orderservice.domain.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for accessing and managing {@link User} entities in the database.
 * Extends {@link JpaRepository} to provide basic CRUD operations and custom queries.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address.
     *
     * @param email the email address of the user to retrieve.
     * @return an {@link Optional} containing the user if found, otherwise empty.
     */
    Optional<User> findByEmail(String email);
}
