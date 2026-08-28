package com.vjiki.music.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
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
import com.vjiki.music.entity.Song;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {MusicApplication.class, TestContainersConfig.class})
class SongRepositoryIntegrationTest {

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
    private SongRepository songRepository;

    @Nested
    @DisplayName("SongRepository")
    class SongRepositoryTests {

        @Test
        @DisplayName("should save and find song by id")
        void shouldSaveAndFindSongById() {
            Song song = Song.builder()
                    .artists(Map.of("default", List.of("Artist Name")))
                    .audioUrls(Map.of("default", "http://audio.com/song.mp3"))
                    .coverUrls(Map.of("default", "http://cover.com/cover.jpg"))
                    .title("Test Song")
                    .active(true)
                    .createdBy("system")
                    .modifiedBy("system")
                    .build();

            Song saved = songRepository.save(song);
            Optional<Song> found = songRepository.findById(saved.getId());

            assertThat(found).isPresent();
            assertThat(found.get().getTitle()).isEqualTo("Test Song");
            assertThat(found.get().getActive()).isTrue();
        }

        @Test
        @DisplayName("should find only active songs")
        void shouldFindOnlyActiveSongs() {
            Song activeSong = Song.builder()
                    .artists(Map.of("default", List.of("Active Artist")))
                    .audioUrls(Map.of("default", "http://audio.com/active.mp3"))
                    .coverUrls(Map.of("default", "http://cover.com/active.jpg"))
                    .title("Active Song")
                    .active(true)
                    .createdBy("system")
                    .modifiedBy("system")
                    .build();
            Song inactiveSong = Song.builder()
                    .artists(Map.of("default", List.of("Inactive Artist")))
                    .audioUrls(Map.of("default", "http://audio.com/inactive.mp3"))
                    .coverUrls(Map.of("default", "http://cover.com/inactive.jpg"))
                    .title("Inactive Song")
                    .active(false)
                    .createdBy("system")
                    .modifiedBy("system")
                    .build();

            songRepository.save(activeSong);
            songRepository.save(inactiveSong);

            List<Song> activeSongs = songRepository.findAllActive();

            assertThat(activeSongs).hasSize(1);
            assertThat(activeSongs.getFirst().getTitle()).isEqualTo("Active Song");
            assertThat(activeSongs.getFirst().getActive()).isTrue();
        }

        @Test
        @DisplayName("should update song")
        void shouldUpdateSong() {
            Song song = Song.builder()
                    .artists(Map.of("default", List.of("Artist")))
                    .audioUrls(Map.of("default", "http://audio.com/song.mp3"))
                    .coverUrls(Map.of("default", "http://cover.com/cover.jpg"))
                    .title("Original Title")
                    .active(true)
                    .createdBy("system")
                    .modifiedBy("system")
                    .build();

            Song saved = songRepository.save(song);
            saved.setLikesCount(10L);
            saved.setActive(false);
            Song updated = songRepository.save(saved);

            assertThat(updated.getLikesCount()).isEqualTo(10L);
            assertThat(updated.getActive()).isFalse();
        }

        @Test
        @DisplayName("should delete song")
        void shouldDeleteSong() {
            Song song = Song.builder()
                    .artists(Map.of("default", List.of("Artist")))
                    .audioUrls(Map.of("default", "http://audio.com/song.mp3"))
                    .coverUrls(Map.of("default", "http://cover.com/cover.jpg"))
                    .title("To Delete")
                    .active(true)
                    .createdBy("system")
                    .modifiedBy("system")
                    .build();

            Song saved = songRepository.save(song);
            songRepository.deleteById(saved.getId());

            Optional<Song> found = songRepository.findById(saved.getId());

            assertThat(found).isNotPresent();
        }
    }
}
