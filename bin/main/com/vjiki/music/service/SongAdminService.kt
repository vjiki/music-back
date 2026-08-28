package com.vjiki.music.service

import com.vjiki.music.dto.CreateSongRequest
import com.vjiki.music.dto.SongResponse
import com.vjiki.music.entity.Song
import com.vjiki.music.entity.Tag
import com.vjiki.music.mapper.SongMapper.toResponse
import com.vjiki.music.repository.SongRepository
import com.vjiki.music.repository.TagRepository
import com.vjiki.music.repository.TrackTagRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@Service
class SongAdminService(
    private val songRepository: SongRepository,
    private val tagRepository: TagRepository,
    private val trackTagRepository: TrackTagRepository,
    private val tagLookupService: TagLookupService
) {

    @Transactional
    fun createSong(request: CreateSongRequest, createdBy: String = "system"): SongResponse {
        val title = request.title.trim()
        if (title.isBlank()) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "title is required")
        if (request.artists.isEmpty()) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "artists is required")

        val artists = request.artists.map { it.trim() }.filter { it.isNotBlank() }
        if (artists.isEmpty()) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "artists is required")

        val audioUrl = request.audioUrl.trim()
        if (audioUrl.isBlank()) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "audio_url is required")

        val type = request.type.trim().ifBlank { "SONG" }

        val song = songRepository.save(
            Song(
                artists = mapOf("default" to artists),
                audioUrls = mapOf("default" to audioUrl),
                coverUrls = request.cover?.trim()?.takeIf { it.isNotBlank() }?.let { mapOf("default" to it) } ?: emptyMap(),
                videoUrls = request.videoUrl?.trim()?.takeIf { it.isNotBlank() }?.let { mapOf("default" to it) } ?: emptyMap(),
                title = title,
                active = request.active,
                createdBy = createdBy,
                modifiedBy = createdBy,
                type = type
            )
        )

        // Upsert tags + assign to track
        request.tags.forEach { t ->
            val name = t.name.trim()
            if (name.isBlank()) return@forEach
            val tagType = t.type.trim().ifBlank { "DEFAULT" }
            val source = t.source.trim().ifBlank { "MANUAL" }

            val existing = tagRepository.findOneByNameAndType(name, tagType)
            val tag = existing ?: tagRepository.save(
                Tag(
                    id = UUID.randomUUID(),
                    name = name,
                    type = tagType
                )
            )

            trackTagRepository.upsertTrackTag(
                trackId = song.id,
                tagId = tag.id,
                weight = t.weight,
                source = source
            )
        }

        val tagsById = tagLookupService.getTagsByTrackIds(listOf(song.id))
        return song.toResponse().copy(tags = tagsById[song.id] ?: emptyList())
    }
}


