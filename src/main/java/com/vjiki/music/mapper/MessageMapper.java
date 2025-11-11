package com.vjiki.music.mapper;

import com.vjiki.music.dto.MessageResponse;
import com.vjiki.music.entity.Message;

public interface MessageMapper {
    MessageResponse toResponse(Message message);
}

