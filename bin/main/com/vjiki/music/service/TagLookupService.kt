package com.vjiki.music.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.vjiki.music.dto.TagResponse
import com.vjiki.music.repository.TrackTagRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class TagLookupService(
    private val trackTagRepository: TrackTagRepository,
    private val objectMapper: ObjectMapper
) {
    fun getTagsByTrackIds(trackIds: Collection<UUID>): Map<UUID, List<TagResponse>> {
        if (trackIds.isEmpty()) return emptyMap()

        // Faster: one row per track_id (JSON aggregated in DB), instead of many rows.
        val rows = trackTagRepository.findTagsJsonByTrackIds(trackIds)
        val typeRef = object : TypeReference<List<TagResponse>>() {}

        return rows.associate { row ->
            val tags = runCatching { objectMapper.readValue(row.tags, typeRef) }.getOrDefault(emptyList())
            row.trackId to tags
        }
    }
}


