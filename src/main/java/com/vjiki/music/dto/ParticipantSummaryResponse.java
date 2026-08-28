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
public class ParticipantSummaryResponse {
    private UUID userId;
    private String userNickname;
    private String userAvatarUrl;
}
