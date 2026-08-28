package com.vjiki.music.controller;

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

import com.vjiki.music.entity.Song;
import com.vjiki.music.entity.Tag;
import com.vjiki.music.entity.TrackTag;
import com.vjiki.music.repository.SongRepository;
import com.vjiki.music.repository.TagRepository;
import com.vjiki.music.repository.TrackTagRepository;

@DisplayName("SearchController integration tests")
class SearchControllerIntegrationTest extends AbstractControllerIntegrationTest {

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TrackTagRepository trackTagRepository;

    private Song saveSong(String artist, String slug, String title) {
        return songRepository.saveAndFlush(Song.builder()
                .artists(Map.of("default", List.of(artist)))
                .audioUrls(Map.of("default", "http://audio.com/" + slug + ".mp3"))
                .coverUrls(Map.of("default", "http://cover.com/" + slug + ".jpg"))
                .title(title)
                .active(true)
                .createdBy("system")
                .modifiedBy("system")
                .type("SONG")
                .build());
    }

    @Nested
    @DisplayName("GET /api/v1/search/songs/{userId}")
    class SearchSongs {

        @Test
        @DisplayName("should find by title and by tag name")
        void shouldFindByTitleAndByTagName() throws Exception {
            Song song1 = saveSong("Some Artist", "a", "Very Chill Track");
            Song song2 = saveSong("Other Artist", "b", "Random");

            Tag tag = tagRepository.saveAndFlush(Tag.builder()
                    .id(UUID.randomUUID())
                    .name("Electronic")
                    .type("GENRE")
                    .build());

            trackTagRepository.saveAndFlush(TrackTag.builder()
                    .trackId(song2.getId())
                    .tagId(tag.getId())
                    .weight(0.8)
                    .source("MANUAL")
                    .build());

            UUID userId = UUID.randomUUID();

            mockMvc.perform(get("/api/v1/search/songs/{userId}", userId)
                            .param("q", "chill")
                            .with(user("test").roles("USER")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items").isArray())
                    .andExpect(jsonPath("$.items.length()").value(1))
                    .andExpect(jsonPath("$.items[0].id").value(song1.getId().toString()));

            mockMvc.perform(get("/api/v1/search/songs/{userId}", userId)
                            .param("q", "elect")
                            .with(user("test").roles("USER")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items").isArray())
                    .andExpect(jsonPath("$.items.length()").value(1))
                    .andExpect(jsonPath("$.items[0].id").value(song2.getId().toString()))
                    .andExpect(jsonPath("$.items[0].tags").isArray());
        }
    }
}
