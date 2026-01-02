package com.vjiki.music.service

import com.vjiki.music.dto.BandResponse
import com.vjiki.music.dto.SongResponse
import com.vjiki.music.pagination.LocalDateTimeIdCursorCodec
import com.vjiki.music.pagination.CursorPageResponse
import com.vjiki.music.repository.ArtistRepository
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@Service
class BandService(
    private val artistRepository: ArtistRepository,
    private val searchService: SearchService
) {
    fun getBands(limit: Int): List<BandResponse> {
        val safeLimit = limit.coerceIn(1, 200)
        return artistRepository.findBands(PageRequest.of(0, safeLimit)).map { it.toBandResponse() }
    }

    fun getBandsPage(userId: UUID, name: String, limit: Int, cursor: String?): CursorPageResponse<BandResponse> {
        val q = name.trim()
        if (q.isBlank()) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "name is required")

        val safeLimit = limit.coerceIn(1, 200)
        val pageable = PageRequest.of(0, safeLimit + 1)

        val decoded = LocalDateTimeIdCursorCodec.decodeOrBadRequest(cursor)
        val bands = if (decoded == null) {
            artistRepository.findBandsPageFirst(q = q, pageable = pageable)
        } else {
            artistRepository.findBandsPageAfter(
                q = q,
                cursorCreatedAt = decoded.createdAt,
                cursorId = decoded.id,
                pageable = pageable
            )
        }

        val hasNext = bands.size > safeLimit
        val slice = if (hasNext) bands.take(safeLimit) else bands
        val items = slice.map { band ->
            val songs = fetchAllSongsForBand(userId = userId, bandName = band.name)
            band.toBandResponse(songs = songs)
        }

        val last = slice.lastOrNull()
        val nextCursor = if (hasNext && last?.createdAt != null) {
            LocalDateTimeIdCursorCodec.encode(last.createdAt, last.id)
        } else {
            null
        }

        return CursorPageResponse(items = items, nextCursor = nextCursor, hasNext = hasNext)
    }

    fun getBand(id: UUID): BandResponse {
        val artist = artistRepository.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Band not found: $id") }
        if (!artist.isBand) throw ResponseStatusException(HttpStatus.NOT_FOUND, "Band not found: $id")
        return artist.toBandResponse()
    }

    private fun fetchAllSongsForBand(userId: UUID, bandName: String): List<SongResponse> {
        val q = bandName.trim()
        if (q.isBlank()) return emptyList()

        val out = mutableListOf<SongResponse>()
        var cursor: String? = null
        var guard = 0
        do {
            val page = searchService.searchSongs(userId = userId, q = q, limit = 100, cursor = cursor)
            out.addAll(page.items)
            cursor = page.nextCursor
            guard++
            if (guard > 50) break
        } while (cursor != null)
        return out
    }
}

private fun com.vjiki.music.entity.Artist.toBandResponse(songs: List<SongResponse> = emptyList()): BandResponse =
    BandResponse(
        id = id,
        name = name,
        sortName = sortName,
        countryCode = countryCode,
        isBand = isBand,
        debutYear = debutYear,
        popularity = popularity,
        createdAt = createdAt,
        updatedAt = updatedAt,
        coverUrl = coverUrl["default"],
        songs = songs
    )


