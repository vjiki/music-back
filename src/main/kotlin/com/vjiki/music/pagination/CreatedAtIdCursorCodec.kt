package com.vjiki.music.pagination

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.Base64
import java.util.UUID

/**
 * Opaque cursor for keyset pagination: (createdAtEpochMillis, id).
 *
 * Encoded as base64url (no padding) of: "<epochMillis>:<uuid>"
 */
object CreatedAtIdCursorCodec {
    data class Cursor(
        val createdAt: OffsetDateTime,
        val id: UUID
    )

    fun encode(createdAt: OffsetDateTime, id: UUID): String {
        val raw = "${createdAt.toInstant().toEpochMilli()}:$id"
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.toByteArray(StandardCharsets.UTF_8))
    }

    fun decodeOrNull(cursor: String?): Cursor? {
        if (cursor.isNullOrBlank()) return null
        return try {
            val decoded = String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8)
            val parts = decoded.split(":", limit = 2)
            if (parts.size != 2) return null
            val epochMillis = parts[0].toLong()
            val id = UUID.fromString(parts[1])
            val createdAt = OffsetDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneOffset.UTC)
            Cursor(createdAt = createdAt, id = id)
        } catch (_: Exception) {
            null
        }
    }

    fun decodeOrBadRequest(cursor: String?): Cursor? {
        val parsed = decodeOrNull(cursor)
        if (cursor.isNullOrBlank()) return null
        if (parsed == null) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid cursor")
        }
        return parsed
    }
}


