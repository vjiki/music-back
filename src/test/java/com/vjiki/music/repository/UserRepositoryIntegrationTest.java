package com.vjiki.music.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import com.vjiki.music.MusicApplication;
import com.vjiki.music.config.TestContainersConfig;
import com.vjiki.music.entity.AccessLevel;
import com.vjiki.music.entity.AuthProvider;
import com.vjiki.music.entity.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {MusicApplication.class, TestContainersConfig.class})
class UserRepositoryIntegrationTest {

    @DynamicPropertySource
    static void registerDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", TestContainersConfig.POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", TestContainersConfig.POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", TestContainersConfig.POSTGRES_CONTAINER::getPassword);
        registry.add("spring.datasource.hikari.data-source-properties.ssl", () -> "false");
        registry.add("spring.datasource.hikari.data-source-properties.sslmode", () -> "disable");
        // "update" rather than "create-drop": this slice shares one container with the
        // @SpringBootTest contexts, and dropping tables would break whichever context is alive.
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @Autowired
    private UserRepository userRepository;

    @Nested
    @DisplayName("UserRepository")
    class UserRepositoryTests {

        @Test
        @DisplayName("should save and find user by id")
        void shouldSaveAndFindUserById() {
            User user = User.builder()
                    .email("test@example.com")
                    .nickname("testuser")
                    .accessLevel(AccessLevel.USER)
                    .provider(AuthProvider.LOCAL)
                    .isActive(true)
                    .isVerified(false)
                    .createdBy("system")
                    .modifiedBy("system")
                    .build();

            User saved = userRepository.save(user);
            Optional<User> found = userRepository.findById(saved.getId());

            assertThat(found).isPresent();
            assertThat(found.get().getEmail()).isEqualTo("test@example.com");
            assertThat(found.get().getNickname()).isEqualTo("testuser");
        }

        @Test
        @DisplayName("should find user by email")
        void shouldFindUserByEmail() {
            User user = User.builder()
                    .email("findme@example.com")
                    .nickname("findme")
                    .accessLevel(AccessLevel.USER)
                    .provider(AuthProvider.LOCAL)
                    .isActive(true)
                    .isVerified(false)
                    .createdBy("system")
                    .modifiedBy("system")
                    .build();

            userRepository.save(user);
            Optional<User> found = userRepository.findByEmail("findme@example.com");

            assertThat(found).isPresent();
            assertThat(found.get().getEmail()).isEqualTo("findme@example.com");
        }

        @Test
        @DisplayName("should return empty when user not found by email")
        void shouldReturnEmptyWhenUserNotFoundByEmail() {
            Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

            assertThat(found).isNotPresent();
        }

        @Test
        @DisplayName("should update user")
        void shouldUpdateUser() {
            User user = User.builder()
                    .email("update@example.com")
                    .nickname("original")
                    .accessLevel(AccessLevel.USER)
                    .provider(AuthProvider.LOCAL)
                    .isActive(true)
                    .isVerified(false)
                    .createdBy("system")
                    .modifiedBy("system")
                    .build();

            User saved = userRepository.save(user);
            saved.setAvatarUrl("https://example.com/avatar.jpg");
            saved.setIsActive(false);
            User updated = userRepository.save(saved);

            assertThat(updated.getAvatarUrl()).isEqualTo("https://example.com/avatar.jpg");
            assertThat(updated.getIsActive()).isFalse();
        }

        @Test
        @DisplayName("should delete user")
        void shouldDeleteUser() {
            User user = User.builder()
                    .email("delete@example.com")
                    .nickname("delete")
                    .accessLevel(AccessLevel.USER)
                    .provider(AuthProvider.LOCAL)
                    .isActive(true)
                    .isVerified(false)
                    .createdBy("system")
                    .modifiedBy("system")
                    .build();

            User saved = userRepository.save(user);
            userRepository.deleteById(saved.getId());

            Optional<User> found = userRepository.findById(saved.getId());

            assertThat(found).isNotPresent();
        }
    }
}
