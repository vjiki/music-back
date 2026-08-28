package com.vjiki.music.mapper;

import java.util.List;

import com.vjiki.music.dto.ChatResponse;
import com.vjiki.music.dto.ParticipantResponse;
import com.vjiki.music.entity.Chat;
import com.vjiki.music.entity.ChatParticipant;

public final class ChatMapper {

    private ChatMapper() {
    }

    public static ChatResponse toResponse(Chat chat) {
        return toResponse(chat, chat.getParticipants());
    }

    /**
     * Maps a chat with a separately loaded participant list, so the entity's own
     * (possibly lazy) collection is left untouched.
     */
    public static ChatResponse toResponse(Chat chat, List<ChatParticipant> chatParticipants) {
        List<ParticipantResponse> participants = chatParticipants == null
                ? List.of()
                : chatParticipants.stream()
                        .map(ChatMapper::toParticipantResponse)
                        .toList();

        return ChatResponse.builder()
                .id(chat.getId())
                .type(chat.getType() == null ? null : chat.getType().name())
                .title(chat.getTitle())
                .description(chat.getDescription())
                .avatarUrl(chat.getAvatarUrl())
                .ownerId(chat.getOwnerId())
                .ownerNickname(chat.getOwner() == null ? null : chat.getOwner().getNickname())
                .isEncrypted(chat.getIsEncrypted())
                .isArchived(chat.getIsArchived())
                .isMuted(chat.getIsMuted())
                .createdAt(chat.getCreatedAt())
                .updatedAt(chat.getUpdatedAt())
                .participants(participants)
                .build();
    }

    private static ParticipantResponse toParticipantResponse(ChatParticipant participant) {
        return ParticipantResponse.builder()
                .userId(participant.getUserId())
                .userEmail(participant.getUser() == null ? "" : participant.getUser().getEmail())
                .userNickname(participant.getUser() == null ? "" : participant.getUser().getNickname())
                .userAvatarUrl(participant.getUser() == null ? null : participant.getUser().getAvatarUrl())
                .role(participant.getRole() == null ? null : participant.getRole().name())
                .joinedAt(participant.getJoinedAt())
                .isMuted(participant.getIsMuted())
                .build();
    }
}
