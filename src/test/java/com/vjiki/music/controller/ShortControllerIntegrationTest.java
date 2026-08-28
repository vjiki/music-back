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

@DisplayName("ShortController integration tests")
class ShortControllerIntegrationTest extends AbstractControllerIntegrationTest {

    @Autowired
    private SongRepository songRepository;

    private Song saveItem(String artist, String slug, String title, boolean active, String type) {
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
    @DisplayName("GET /api/v1/shorts/{userId}/page")
    class GetShortsPage {

        @Test
        @DisplayName("should return cursor-paginated active items (songs + shorts), with type field present")
        void shouldReturnCursorPaginatedActiveItems() throws Exception {
            // 2 active SONG + 2 active SHORT + 1 inactive
            for (int idx = 1; idx <= 2; idx++) {
                saveItem("SongArtist " + idx, "s" + idx, "Song " + idx, true, "SONG");
            }
            for (int idx = 1; idx <= 2; idx++) {
                saveItem("ShortArtist " + idx, "sh" + idx, "Short " + idx, true, "SHORT");
            }
            saveItem("InactiveArtist", "inactive", "Inactive", false, "SHORT");

            UUID userId = UUID.randomUUID();

            MvcResult first = mockMvc.perform(get("/api/v1/shorts/{userId}/page", userId)
                            .param("limit", "3")
                            .with(user("test").roles("USER")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items").isArray())
                    .andExpect(jsonPath("$.items.length()").value(3))
                    .andExpect(jsonPath("$.items[0].type").exists())
                    .andExpect(jsonPath("$.hasNext").value(true))
                    .andExpect(jsonPath("$.nextCursor").isString())
                    .andReturn();

            JsonNode nextCursor = objectMapper.readTree(first.getResponse().getContentAsString())
                    .get("nextCursor");
            assertThat(nextCursor).isNotNull();
            String cursor = nextCursor.asText();
            assertThat(cursor).isNotBlank();

            mockMvc.perform(get("/api/v1/shorts/{userId}/page", userId)
                            .param("limit", "3")
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
