package com.vjiki.music.service;

import com.vjiki.music.dto.ChatListItemResponse;
import com.vjiki.music.dto.ParticipantSummaryResponse;
import com.vjiki.music.entity.Chat;
import com.vjiki.music.entity.ChatParticipant;
import com.vjiki.music.entity.Message;
import com.vjiki.music.repository.ChatParticipantRepository;
import com.vjiki.music.repository.ChatRepository;
import com.vjiki.music.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
        
        return chats.stream()
                .map(chat -> mapToChatListItem(chat, userId))
                .toList();
    }

    private ChatListItemResponse mapToChatListItem(Chat chat, UUID currentUserId) {
        // Get last message for preview
        List<Message> lastMessages = messageRepository.findMessagesByChatId(chat.getId());
        Optional<Message> lastMessageOpt = lastMessages.stream()
                .filter(msg -> !Boolean.TRUE.equals(msg.getIsDeleted()))
                .findFirst(); // Get first (most recent) message since we order DESC
        
        // Get participants (excluding current user for display)
        List<ChatParticipant> participants = chatParticipantRepository.findByChatId(chat.getId());
        
        // Load user data for participants
        List<ParticipantSummaryResponse> participantSummaries = participants.stream()
                .filter(p -> !p.getUserId().equals(currentUserId))
                .map(p -> new ParticipantSummaryResponse(
                        p.getUserId(),
                        Optional.ofNullable(p.getUser())
                                .map(user -> user.getNickname())
                                .orElse(null),
                        Optional.ofNullable(p.getUser())
                                .map(user -> user.getAvatarUrl())
                                .orElse(null)
                ))
                .toList();
        
        // Get current user's participant info for muted status
        Optional<ChatParticipant> currentUserParticipant = participants.stream()
                .filter(p -> p.getUserId().equals(currentUserId))
                .findFirst();
        
        // Determine chat title (for DIRECT chats, use other participant's name)
        String displayTitle = chat.getTitle();
        if (chat.getType() == com.vjiki.music.entity.ChatType.DIRECT && displayTitle == null) {
            displayTitle = participantSummaries.stream()
                    .findFirst()
                    .map(ParticipantSummaryResponse::getUserNickname)
                    .orElse("Direct Message");
        }
        
        return new ChatListItemResponse(
                chat.getId(),
                chat.getType() != null ? chat.getType().name() : null,
                displayTitle,
                chat.getAvatarUrl(),
                lastMessageOpt.map(Message::getContent).orElse(null),
                lastMessageOpt.map(Message::getCreatedAt).orElse(chat.getUpdatedAt()),
                lastMessageOpt.map(Message::getSenderId).orElse(null),
                lastMessageOpt.flatMap(msg -> Optional.ofNullable(msg.getSender())
                        .map(sender -> sender.getNickname())).orElse(null),
                0, // TODO: Implement unread count logic
                currentUserParticipant.map(ChatParticipant::getIsMuted).orElse(false),
                chat.getUpdatedAt(),
                participantSummaries
        );
    }
}

