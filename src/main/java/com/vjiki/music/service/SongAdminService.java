package com.vjiki.music.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.vjiki.music.dto.CreateSongRequest;
import com.vjiki.music.dto.CreateSongTagRequest;
import com.vjiki.music.dto.SongResponse;
import com.vjiki.music.dto.TagResponse;
import com.vjiki.music.entity.Song;
import com.vjiki.music.entity.Tag;
import com.vjiki.music.mapper.SongMapper;
import com.vjiki.music.repository.SongRepository;
import com.vjiki.music.repository.TagRepository;
import com.vjiki.music.repository.TrackTagRepository;

@Service
public class SongAdminService {

    private final SongRepository songRepository;
    private final TagRepository tagRepository;
    private final TrackTagRepository trackTagRepository;
    private final TagLookupService tagLookupService;

    public SongAdminService(SongRepository songRepository,
                            TagRepository tagRepository,
                            TrackTagRepository trackTagRepository,
                            TagLookupService tagLookupService) {
        this.songRepository = songRepository;
        this.tagRepository = tagRepository;
        this.trackTagRepository = trackTagRepository;
        this.tagLookupService = tagLookupService;
    }

    @Transactional
    public SongResponse createSong(CreateSongRequest request) {
        return createSong(request, "system");
    }

    @Transactional
    public SongResponse createSong(CreateSongRequest request, String createdBy) {
        String title = request.getTitle() == null ? "" : request.getTitle().trim();
        if (title.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "title is required");
        }
        if (request.getArtists() == null || request.getArtists().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "artists is required");
        }

        List<String> artists = new ArrayList<>();
        for (String artist : request.getArtists()) {
            if (artist != null && !artist.trim().isBlank()) {
                artists.add(artist.trim());
            }
        }
        if (artists.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "artists is required");
        }

        String audioUrl = request.getAudioUrl() == null ? "" : request.getAudioUrl().trim();
        if (audioUrl.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "audio_url is required");
        }

        String type = blankToDefault(request.getType(), "SONG");

        Song song = songRepository.save(Song.builder()
                .artists(Map.of("default", artists))
                .audioUrls(Map.of("default", audioUrl))
                .coverUrls(singleUrlMap(request.getCover()))
                .videoUrls(singleUrlMap(request.getVideoUrl()))
                .title(title)
                .active(request.getActive() != null && request.getActive())
                .createdBy(createdBy)
                .modifiedBy(createdBy)
                .type(type)
                .build());

        // Upsert tags + assign to track
        if (request.getTags() != null) {
            for (CreateSongTagRequest tagRequest : request.getTags()) {
                String name = tagRequest.getName() == null ? "" : tagRequest.getName().trim();
                if (name.isBlank()) {
                    continue;
                }
                String tagType = blankToDefault(tagRequest.getType(), "DEFAULT");
                String source = blankToDefault(tagRequest.getSource(), "MANUAL");

                Tag tag = tagRepository.findOneByNameAndType(name, tagType);
                if (tag == null) {
                    tag = tagRepository.save(Tag.builder()
                            .id(UUID.randomUUID())
                            .name(name)
                            .type(tagType)
                            .build());
                }

                double weight = tagRequest.getWeight() == null ? 1.0 : tagRequest.getWeight();
                trackTagRepository.upsertTrackTag(song.getId(), tag.getId(), weight, source);
            }
        }

        Map<UUID, List<TagResponse>> tagsById = tagLookupService.getTagsByTrackIds(List.of(song.getId()));
        SongResponse response = SongMapper.toResponse(song);
        response.setTags(tagsById.getOrDefault(song.getId(), List.of()));
        return response;
    }

    private static Map<String, String> singleUrlMap(String url) {
        if (url == null || url.trim().isBlank()) {
            return Map.of();
        }
        return Map.of("default", url.trim());
    }

    private static String blankToDefault(String value, String fallback) {
        if (value == null) {
            return fallback;
        }
        String trimmed = value.trim();
        return trimmed.isBlank() ? fallback : trimmed;
    }
}
