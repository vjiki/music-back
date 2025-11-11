package com.vjiki.music.mapper;

import com.vjiki.music.dto.MessageResponse;
import com.vjiki.music.entity.Message;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MessageMapperImpl implements MessageMapper {

    @Override
    public MessageResponse toResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getChatId(),
                message.getSenderId(),
                Optional.ofNullable(message.getSender())
                        .map(sender -> sender.getEmail())
                        .orElse(null),
                Optional.ofNullable(message.getSender())
                        .map(sender -> sender.getNickname())
                        .orElse(null),
                Optional.ofNullable(message.getSender())
                        .map(sender -> sender.getAvatarUrl())
                        .orElse(null),
                message.getReplyToId(),
                message.getMessageType() != null ? message.getMessageType().name() : null,
                message.getContent(),
                message.getSongId(),
                message.getAttachmentCount(),
                message.getIsEdited(),
                message.getIsDeleted(),
                message.getCreatedAt(),
                message.getUpdatedAt()
        );
    }
}

