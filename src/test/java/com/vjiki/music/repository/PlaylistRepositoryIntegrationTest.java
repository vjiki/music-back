package com.vjiki.music.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
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
import com.vjiki.music.entity.Playlist;
import com.vjiki.music.entity.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {MusicApplication.class, TestContainersConfig.class})
class PlaylistRepositoryIntegrationTest {

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
    private PlaylistRepository playlistRepository;

    @Autowired
    private UserRepository userRepository;

    private User saveUser(String email, String nickname) {
        return userRepository.save(User.builder()
                .email(email)
                .nickname(nickname)
                .provider(AuthProvider.LOCAL)
                .accessLevel(AccessLevel.USER)
                .isActive(true)
                .createdBy("system")
                .modifiedBy("system")
                .build());
    }

    @Nested
    @DisplayName("PlaylistRepository")
    class PlaylistRepositoryTests {

        @Test
        @DisplayName("should save and find playlist by id")
        void shouldSaveAndFindPlaylistById() {
            User user = saveUser("test@example.com", "testuser");

            Playlist playlist = Playlist.builder()
                    .user(user)
                    .name("My Playlist")
                    .type("CUSTOM")
                    .isPublic(false)
                    .createdBy("system")
                    .modifiedBy("system")
                    .build();

            Playlist saved = playlistRepository.save(playlist);
            Optional<Playlist> found = playlistRepository.findById(saved.getId());

            assertThat(found).isPresent();
            assertThat(found.get().getName()).isEqualTo("My Playlist");
        }

        @Test
        @DisplayName("should find playlists by user id")
        void shouldFindPlaylistsByUserId() {
            User user = saveUser("user1@example.com", "user1");

            playlistRepository.save(Playlist.builder()
                    .user(user)
                    .name("Playlist 1")
                    .type("CUSTOM")
                    .createdBy("system")
                    .modifiedBy("system")
                    .build());
            playlistRepository.save(Playlist.builder()
                    .user(user)
                    .name("Playlist 2")
                    .type("CUSTOM")
                    .createdBy("system")
                    .modifiedBy("system")
                    .build());

            List<Playlist> playlists = playlistRepository.findByUserIdWithUser(user.getId());

            assertThat(playlists).hasSize(2);
            assertThat(playlists)
                    .extracting(Playlist::getName)
                    .containsExactlyInAnyOrder("Playlist 1", "Playlist 2");
        }

        @Test
        @DisplayName("should find playlist by user id and name")
        void shouldFindPlaylistByUserIdAndName() {
            User user = saveUser("user2@example.com", "user2");

            playlistRepository.save(Playlist.builder()
                    .user(user)
                    .name("Unique Playlist")
                    .type("CUSTOM")
                    .createdBy("system")
                    .modifiedBy("system")
                    .build());

            Optional<Playlist> found = playlistRepository.findByUserIdAndName(user.getId(), "Unique Playlist");

            assertThat(found).isPresent();
            assertThat(found.get().getName()).isEqualTo("Unique Playlist");
        }
    }
}
