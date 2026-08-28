package com.vjiki.music.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String email;
    private String password;

    /**
     * LOCAL (default), GOOGLE, APPLE
     */
    private String provider;

    /**
     * Optional external provider id (for OAuth users)
     */
    private String providerId;

    private String nickname;
    private String avatarUrl;
}
