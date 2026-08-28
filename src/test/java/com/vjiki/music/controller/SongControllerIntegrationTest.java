package com.vjiki.music.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.vjiki.music.entity.Song;
import com.vjiki.music.repository.SongRepository;

@DisplayName("SongController integration tests")
class SongControllerIntegrationTest extends AbstractControllerIntegrationTest {

    @Autowired
    private SongRepository songRepository;

    private Song saveSong(String artist, String slug, String title, boolean active, String type) {
        return songRepository.saveAndFlush(Song.builder()
                .artists(Map.of("default", List.of(artist)))
                .audioUrls(Map.of("default", "http://audio.com/" + slug + ".mp3"))
                .coverUrls(Map.of("default", "http://cover.com/" + slug + ".jpg"))
                .title(title)
                .active(active)
                .createdBy("system")
                .modifiedBy("system")
                .type(type)
                .build());
    }

    @Nested
    @DisplayName("GET /api/v1/songs/{userId}")
    class GetSongs {

        @Test
        @DisplayName("should return list of active songs")
        void shouldReturnListOfActiveSongs() throws Exception {
            saveSong("Artist 1", "song1", "Song 1", true, "SONG");
            saveSong("Artist 2", "song2", "Song 2", true, "SONG");
            saveSong("Artist 3", "song3", "Song 3", false, "SONG");

            UUID userId = UUID.randomUUID();

            mockMvc.perform(get("/api/v1/songs/{userId}", userId).with(user("test").roles("USER")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].title").exists())
                    .andExpect(jsonPath("$[1].title").exists());
        }

        @Test
        @DisplayName("should return empty list when no active songs exist")
        void shouldReturnEmptyListWhenNoActiveSongsExist() throws Exception {
            UUID userId = UUID.randomUUID();

            mockMvc.perform(get("/api/v1/songs/{userId}", userId).with(user("test").roles("USER")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/songs/{userId}/page")
    class GetSongsPage {

        @Test
        @DisplayName("should return cursor-paginated songs and exclude non-SONG types")
        void shouldReturnCursorPaginatedSongs() throws Exception {
            // 3 active SONG + 1 active SHORT + 1 inactive SONG
            for (int idx = 1; idx <= 3; idx++) {
                saveSong("SongArtist " + idx, "s" + idx, "Song " + idx, true, "SONG");
            }
            saveSong("ShortArtist", "short", "Short 1", true, "SHORT");
            saveSong("InactiveArtist", "inactive", "Inactive Song", false, "SONG");

            UUID userId = UUID.randomUUID();

            MvcResult first = mockMvc.perform(get("/api/v1/songs/{userId}/page", userId)
                            .param("limit", "2")
                            .with(user("test").roles("USER")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items").isArray())
                    .andExpect(jsonPath("$.items.length()").value(2))
                    .andExpect(jsonPath("$.hasNext").value(true))
                    .andExpect(jsonPath("$.nextCursor").isString())
                    .andReturn();

            JsonNode nextCursor = objectMapper.readTree(first.getResponse().getContentAsString())
                    .get("nextCursor");
            assertThat(nextCursor).isNotNull();
            String cursor = nextCursor.asText();
            assertThat(cursor).isNotBlank();

            mockMvc.perform(get("/api/v1/songs/{userId}/page", userId)
                            .param("limit", "2")
                            .param("cursor", cursor)
                            .with(user("test").roles("USER")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items").isArray())
                    .andExpect(jsonPath("$.items.length()").value(1))
                    .andExpect(jsonPath("$.hasNext").value(false))
                    .andExpect(jsonPath("$.nextCursor").doesNotExist());
        }
    }
}
