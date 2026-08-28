package com.vjiki.music.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vjiki.music.dto.MessageResponse;
import com.vjiki.music.entity.AccessLevel;
import com.vjiki.music.entity.AuthProvider;
import com.vjiki.music.entity.Message;
import com.vjiki.music.entity.MessageType;
import com.vjiki.music.entity.User;
import com.vjiki.music.repository.ChatParticipantRepository;
import com.vjiki.music.repository.ChatRepository;
import com.vjiki.music.repository.MessageReactionRepository;
import com.vjiki.music.repository.MessageReadRepository;
import com.vjiki.music.repository.MessageRepository;
import com.vjiki.music.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MessageReactionRepository messageReactionRepository;

    @Mock
    private MessageReadRepository messageReadRepository;

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private ChatParticipantRepository chatParticipantRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MessageServiceImpl messageService;

    @Nested
    @DisplayName("getMessagesByChatId")
    class GetMessagesByChatId {

        @Test
        @DisplayName("should return messages for chat")
        void shouldReturnMessagesForChat() {
            UUID chatId = UUID.randomUUID();
            UUID userId1 = UUID.randomUUID();
            UUID userId2 = UUID.randomUUID();
            UUID messageId = UUID.randomUUID();

            User sender = User.builder()
                    .id(userId1)
                    .email("sender@example.com")
                    .nickname("sender")
                    .provider(AuthProvider.LOCAL)
                    .accessLevel(AccessLevel.USER)
                    .isActive(true)
                    .createdBy("system")
                    .modifiedBy("system")
                    .build();

            Message message = Message.builder()
                    .id(messageId)
                    .chatId(chatId)
                    .senderId(userId1)
                    .sender(sender)
                    .content("Hello")
                    .messageType(MessageType.TEXT)
                    .isDeleted(false)
                    .build();

            when(messageRepository.findMessagesByChatIdAndUsers(chatId, userId1, userId2))
                    .thenReturn(List.of(message));

            List<MessageResponse> result = messageService.getMessagesByChatId(chatId, userId1, userId2);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getId()).isEqualTo(messageId);
            assertThat(result.getFirst().getContent()).isEqualTo("Hello");
            assertThat(result.getFirst().getMessageType()).isEqualTo(MessageType.TEXT.name());
            assertThat(result.getFirst().getSenderNickname()).isEqualTo("sender");
            assertThat(result.getFirst().getIsDeleted()).isFalse();
            verify(messageRepository).findMessagesByChatIdAndUsers(chatId, userId1, userId2);
        }

        @Test
        @DisplayName("should return empty list when no messages found")
        void shouldReturnEmptyListWhenNoMessagesFound() {
            UUID chatId = UUID.randomUUID();
            UUID userId1 = UUID.randomUUID();
            UUID userId2 = UUID.randomUUID();

            when(messageRepository.findMessagesByChatIdAndUsers(chatId, userId1, userId2))
                    .thenReturn(List.of());

            List<MessageResponse> result = messageService.getMessagesByChatId(chatId, userId1, userId2);

            assertThat(result).isEmpty();
        }
    }
}
