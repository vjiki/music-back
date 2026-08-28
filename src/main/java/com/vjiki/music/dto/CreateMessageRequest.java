package com.vjiki.music.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMessageRequest {
    private UUID chatId;
    private UUID senderId;
    private String content;
    private UUID replyToId;
    private String messageType = "TEXT";
    private UUID songId;
    private Integer attachmentCount = 0;
}
