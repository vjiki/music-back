package com.vjiki.music.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.vjiki.music.dto.SongResponse;
import com.vjiki.music.entity.Song;

class SongMapperTest {

    @Nested
    @DisplayName("SongMapper.toResponse()")
    class ToResponse {

        @Test
        @DisplayName("should map song to response correctly")
        void shouldMapSongToResponseCorrectly() {
            Song song = Song.builder()
                    .id(UUID.randomUUID())
                    .artists(Map.of("default", List.of("Artist Name")))
                    .audioUrls(Map.of("default", "http://audio.com/song.mp3"))
                    .coverUrls(Map.of("default", "http://cover.com/cover.jpg"))
                    .title("Test Song")
                    .active(true)
                    .createdBy("system")
                    .modifiedBy("system")
                    .build();

            SongResponse response = SongMapper.toResponse(song);

            assertThat(response.getId()).isEqualTo(song.getId().toString());
            assertThat(response.getTitle()).isEqualTo("Test Song");
            assertThat(response.getArtist()).isEqualTo("Artist Name");
            assertThat(response.getAudioUrl()).isEqualTo("http://audio.com/song.mp3");
            assertThat(response.getCover()).isEqualTo("http://cover.com/cover.jpg");
        }

        @Test
        @DisplayName("should handle empty artists")
        void shouldHandleEmptyArtists() {
            Song song = Song.builder()
                    .id(UUID.randomUUID())
                    .artists(Map.of())
                    .audioUrls(Map.of("default", "http://audio.com/song.mp3"))
                    .coverUrls(Map.of("default", "http://cover.com/cover.jpg"))
                    .title("Test Song")
                    .active(true)
                    .createdBy("system")
                    .modifiedBy("system")
                    .build();

            SongResponse response = SongMapper.toResponse(song);

            assertThat(response.getArtist()).isNull();
        }

        @Test
        @DisplayName("should handle null artists")
        void shouldHandleNullArtists() {
            Song song = Song.builder()
                    .id(UUID.randomUUID())
                    .artists(null)
                    .audioUrls(Map.of("default", "http://audio.com/song.mp3"))
                    .coverUrls(Map.of("default", "http://cover.com/cover.jpg"))
                    .title("Test Song")
                    .active(true)
                    .createdBy("system")
                    .modifiedBy("system")
                    .build();

            SongResponse response = SongMapper.toResponse(song);

            assertThat(response.getArtist()).isNull();
        }

        @Test
        @DisplayName("should handle missing default in artists")
        void shouldHandleMissingDefaultInArtists() {
            Song song = Song.builder()
                    .id(UUID.randomUUID())
                    .artists(Map.of("other", List.of("Other Artist")))
                    .audioUrls(Map.of("default", "http://audio.com/song.mp3"))
                    .coverUrls(Map.of("default", "http://cover.com/cover.jpg"))
                    .title("Test Song")
                    .active(true)
                    .createdBy("system")
                    .modifiedBy("system")
                    .build();

            SongResponse response = SongMapper.toResponse(song);

            assertThat(response.getArtist()).isNull();
        }

        @Test
        @DisplayName("should handle empty artist list")
        void shouldHandleEmptyArtistList() {
            Song song = Song.builder()
                    .id(UUID.randomUUID())
                    .artists(Map.of("default", List.of()))
                    .audioUrls(Map.of("default", "http://audio.com/song.mp3"))
                    .coverUrls(Map.of("default", "http://cover.com/cover.jpg"))
                    .title("Test Song")
                    .active(true)
                    .createdBy("system")
                    .modifiedBy("system")
                    .build();

            SongResponse response = SongMapper.toResponse(song);

            assertThat(response.getArtist()).isNull();
        }
    }
}
