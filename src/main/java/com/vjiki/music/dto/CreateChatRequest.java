package com.vjiki.music.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateChatRequest {
    private String type = "DIRECT";
    private String title;
    private String description;
    private String avatarUrl;
    private UUID ownerId;
    private List<UUID> participantIds = new ArrayList<>();
    private Boolean isEncrypted = false;
}
