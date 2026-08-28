package com.vjiki.music.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageReactionRequest {
    private UUID messageId;
    private UUID userId;
    private String emoji;
}
