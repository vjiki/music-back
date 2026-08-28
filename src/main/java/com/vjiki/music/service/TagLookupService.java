package com.vjiki.music.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vjiki.music.dto.TagResponse;
import com.vjiki.music.repository.TrackTagRepository;

@Service
public class TagLookupService {

    private static final TypeReference<List<TagResponse>> TAG_LIST_TYPE = new TypeReference<>() {
    };

    private final TrackTagRepository trackTagRepository;
    private final ObjectMapper objectMapper;

    public TagLookupService(TrackTagRepository trackTagRepository, ObjectMapper objectMapper) {
        this.trackTagRepository = trackTagRepository;
        this.objectMapper = objectMapper;
    }

    public Map<UUID, List<TagResponse>> getTagsByTrackIds(Collection<UUID> trackIds) {
        if (trackIds == null || trackIds.isEmpty()) {
            return Map.of();
        }

        // Faster: one row per track_id (JSON aggregated in DB), instead of many rows.
        List<TrackTagRepository.TrackTagsJsonProjection> rows = trackTagRepository.findTagsJsonByTrackIds(trackIds);

        Map<UUID, List<TagResponse>> result = new HashMap<>();
        for (TrackTagRepository.TrackTagsJsonProjection row : rows) {
            List<TagResponse> tags;
            try {
                tags = objectMapper.readValue(row.getTags(), TAG_LIST_TYPE);
            } catch (Exception e) {
                tags = List.of();
            }
            result.put(row.getTrackId(), tags);
        }
        return result;
    }
}
