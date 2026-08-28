package com.vjiki.music.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthExistsResponse {
    private Boolean exists;
    private UUID userId;
    private String provider;

    public AuthExistsResponse(Boolean exists) {
        this.exists = exists;
    }
}
