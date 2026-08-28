package com.vjiki.music.security;

import java.util.UUID;

public record UserPrincipal(UUID userId, String email, String firebaseUid) {
}
