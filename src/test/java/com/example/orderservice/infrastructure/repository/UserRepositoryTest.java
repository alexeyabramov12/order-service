package com.example.orderservice.infrastructure.repository;

import com.example.orderservice.domain.auth.User;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static com.example.orderservice.util.TestContainersUtil.configurePostgres;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    private final SoftAssertions softly = new SoftAssertions();

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        configurePostgres(registry);
    }

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should find user by email from preloaded data")
    void findByEmail_PreloadedUserExists_ReturnsUser() {
        Optional<User> result = userRepository.findByEmail("admin1@example.com");

        softly.assertThat(result).isPresent();
        softly.assertThat(result.get().getFirstName()).isEqualTo("John");
        softly.assertThat(result.get().getLastName()).isEqualTo("Doe");
        softly.assertThat(result.get().getRoles()).isNotEmpty();
    }

    @Test
    @DisplayName("Should return all users from preloaded data")
    void findAll_PreloadedUsersExist_ReturnsAllUsers() {
        List<User> users = userRepository.findAll();

        softly. assertThat(users).isNotNull();
        softly. assertThat(users.size()).isEqualTo(4); // Based on your changelog
        softly.assertThat(users)
                .extracting("email")
                .containsExactlyInAnyOrder(
                        "admin1@example.com",
                        "admin2@example.com",
                        "user1@example.com",
                        "user2@example.com");
    }

    @Test
    @DisplayName("Should return empty Optional when user email does not exist")
    void findByEmail_UserDoesNotExist_ReturnsEmpty() {
        Optional<User> result = userRepository.findByEmail("nonexistent@example.com");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should find user by email with roles")
    void findByEmail_UserWithRoles_ReturnsUserWithRoles() {
        Optional<User> result = userRepository.findByEmail("user1@example.com");

        softly.assertThat(result).isPresent();
        softly.assertThat(result.get().getFirstName()).isEqualTo("Alice");
        softly.assertThat(result.get().getRoles()).isNotEmpty();
        softly.assertThat(result.get().getRoles().get(0).getName()).isEqualTo("User");
    }

    @Test
    @DisplayName("Should not find user by email if case does not match")
    void findByEmail_EmailCaseMismatch_ReturnsEmpty() {
        Optional<User> result = userRepository.findByEmail("Admin1@Example.Com");
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should not find user by email if email contains spaces")
    void findByEmail_EmailWithSpaces_ReturnsEmpty() {
        Optional<User> result = userRepository.findByEmail(" admin1@example.com ");
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return empty Optional when email is null")
    void findByEmail_NullEmail_ReturnsEmpty() {
        Optional<User> result = userRepository.findByEmail(null);
        assertThat(result).isEmpty();
    }
}
