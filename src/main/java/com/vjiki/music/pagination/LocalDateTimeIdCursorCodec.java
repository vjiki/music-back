package com.vjiki.music.pagination;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Opaque cursor for keyset pagination: (createdAt, id).
 *
 * Encoded as base64url (no padding) of: "&lt;localDateTime&gt;|&lt;uuid&gt;"
 * where localDateTime is LocalDateTime.toString() (ISO-8601, may include nanos).
 */
public final class LocalDateTimeIdCursorCodec {

    private LocalDateTimeIdCursorCodec() {
    }

    public record Cursor(LocalDateTime createdAt, UUID id) {
    }

    public static String encode(LocalDateTime createdAt, UUID id) {
        String raw = createdAt + "|" + id;
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    public static Cursor decodeOrNull(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return null;
        }
        try {
            String decoded = new String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
            String[] parts = decoded.split("\\|", 2);
            if (parts.length != 2) {
                return null;
            }
            LocalDateTime createdAt = LocalDateTime.parse(parts[0]);
            UUID id = UUID.fromString(parts[1]);
            return new Cursor(createdAt, id);
        } catch (Exception e) {
            return null;
        }
    }

    public static Cursor decodeOrBadRequest(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return null;
        }
        Cursor parsed = decodeOrNull(cursor);
        if (parsed == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid cursor");
        }
        return parsed;
    }
}
