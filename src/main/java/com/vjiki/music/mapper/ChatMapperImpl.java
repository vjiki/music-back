package com.vjiki.music.mapper;

import com.vjiki.music.dto.ChatResponse;
import com.vjiki.music.dto.ParticipantResponse;
import com.vjiki.music.entity.Chat;
import com.vjiki.music.entity.ChatParticipant;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ChatMapperImpl implements ChatMapper {

    @Override
    public ChatResponse toResponse(Chat chat) {
        List<ParticipantResponse> participants = Optional.ofNullable(chat.getParticipants())
                .orElse(List.of())
                .stream()
                .map(this::mapParticipant)
                .toList();

        return new ChatResponse(
                chat.getId(),
                chat.getType() != null ? chat.getType().name() : null,
                chat.getTitle(),
                chat.getDescription(),
                chat.getAvatarUrl(),
                chat.getOwnerId(),
                Optional.ofNullable(chat.getOwner())
                        .map(owner -> owner.getNickname())
                        .orElse(null),
                chat.getIsEncrypted(),
                chat.getIsArchived(),
                chat.getIsMuted(),
                chat.getCreatedAt(),
                chat.getUpdatedAt(),
                participants
        );
    }

    private ParticipantResponse mapParticipant(ChatParticipant participant) {
        if (participant.getUser() == null) {
            return new ParticipantResponse(
                    participant.getUserId(),
                    null,
                    null,
                    null,
                    participant.getRole() != null ? participant.getRole().name() : null,
                    participant.getJoinedAt(),
                    participant.getIsMuted()
            );
        }

        return new ParticipantResponse(
                participant.getUserId(),
                participant.getUser().getEmail(),
                participant.getUser().getNickname(),
                participant.getUser().getAvatarUrl(),
                participant.getRole() != null ? participant.getRole().name() : null,
                participant.getJoinedAt(),
                participant.getIsMuted()
        );
    }
}

