package com.vjiki.music.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.vjiki.music.dto.ChatListItemResponse;
import com.vjiki.music.dto.ParticipantSummaryResponse;
import com.vjiki.music.entity.Chat;
import com.vjiki.music.entity.ChatParticipant;
import com.vjiki.music.entity.ChatType;
import com.vjiki.music.entity.Message;
import com.vjiki.music.repository.ChatParticipantRepository;
import com.vjiki.music.repository.ChatRepository;
import com.vjiki.music.repository.MessageRepository;

@Service
public class ChatListServiceImpl implements ChatListService {

    private final ChatRepository chatRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final MessageRepository messageRepository;

    public ChatListServiceImpl(ChatRepository chatRepository,
                               ChatParticipantRepository chatParticipantRepository,
                               MessageRepository messageRepository) {
        this.chatRepository = chatRepository;
        this.chatParticipantRepository = chatParticipantRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public List<ChatListItemResponse> getChatListForUser(UUID userId) {
        List<Chat> chats = chatRepository.findChatsByUserId(userId);
        List<ChatListItemResponse> items = new ArrayList<>(chats.size());
        for (Chat chat : chats) {
            items.add(mapToChatListItem(chat, userId));
        }
        return items;
    }

    private ChatListItemResponse mapToChatListItem(Chat chat, UUID currentUserId) {
        Message lastMessage = messageRepository.findMessagesByChatId(chat.getId()).stream()
                .filter(message -> !Boolean.TRUE.equals(message.getIsDeleted()))
                .findFirst()
                .orElse(null);

        List<ChatParticipant> participants = chatParticipantRepository.findByChatId(chat.getId());

        List<ParticipantSummaryResponse> participantSummaries = participants.stream()
                .filter(participant -> !participant.getUserId().equals(currentUserId))
                .map(participant -> ParticipantSummaryResponse.builder()
                        .userId(participant.getUserId())
                        .userNickname(participant.getUser() == null ? null : participant.getUser().getNickname())
                        .userAvatarUrl(participant.getUser() == null ? null : participant.getUser().getAvatarUrl())
                        .build())
                .toList();

        ChatParticipant currentUserParticipant = participants.stream()
                .filter(participant -> participant.getUserId().equals(currentUserId))
                .findFirst()
                .orElse(null);

        String displayTitle = chat.getTitle();
        if (chat.getType() == ChatType.DIRECT && displayTitle == null) {
            displayTitle = participantSummaries.isEmpty() || participantSummaries.getFirst().getUserNickname() == null
                    ? "Direct Message"
                    : participantSummaries.getFirst().getUserNickname();
        }

        return ChatListItemResponse.builder()
                .chatId(chat.getId())
                .chatType(chat.getType() == null ? null : chat.getType().name())
                .title(displayTitle)
                .avatarUrl(chat.getAvatarUrl())
                .lastMessagePreview(lastMessage == null ? null : lastMessage.getContent())
                .lastMessageAt(lastMessage == null ? chat.getUpdatedAt() : lastMessage.getCreatedAt())
                .lastMessageSenderId(lastMessage == null ? null : lastMessage.getSenderId())
                .lastMessageSenderName(lastMessage == null || lastMessage.getSender() == null
                        ? null
                        : lastMessage.getSender().getNickname())
                .unreadCount(0)
                .isMuted(currentUserParticipant != null && Boolean.TRUE.equals(currentUserParticipant.getIsMuted()))
                .updatedAt(chat.getUpdatedAt())
                .participants(participantSummaries)
                .build();
    }
}
