package com.vjiki.music.service

import com.vjiki.music.dto.TagResponse
import com.vjiki.music.repository.TrackTagRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class TagLookupService(
    private val trackTagRepository: TrackTagRepository
) {
    fun getTagsByTrackIds(trackIds: Collection<UUID>): Map<UUID, List<TagResponse>> {
        if (trackIds.isEmpty()) return emptyMap()

        val rows = trackTagRepository.findTagsByTrackIds(trackIds)
        return rows.groupBy { it.trackId }.mapValues { (_, list) ->
            list.map { TagResponse(name = it.name, weight = it.weight) }
        }
    }
}


