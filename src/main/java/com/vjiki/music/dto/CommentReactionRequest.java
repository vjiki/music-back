package com.vjiki.music.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentReactionRequest {
    private UUID commentId;
    private UUID userId;
    private String reaction = "LIKE";
}
