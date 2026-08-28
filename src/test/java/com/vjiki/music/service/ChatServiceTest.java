package com.vjiki.music.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vjiki.music.dto.ChatResponse;
import com.vjiki.music.entity.AccessLevel;
import com.vjiki.music.entity.AuthProvider;
import com.vjiki.music.entity.Chat;
import com.vjiki.music.entity.ChatParticipant;
import com.vjiki.music.entity.ChatType;
import com.vjiki.music.entity.ParticipantRole;
import com.vjiki.music.entity.User;
import com.vjiki.music.repository.ChatParticipantRepository;
import com.vjiki.music.repository.ChatRepository;
import com.vjiki.music.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private ChatParticipantRepository chatParticipantRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ChatServiceImpl chatService;

    private User testUser(String email, String nickname) {
        return User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .nickname(nickname)
                .accessLevel(AccessLevel.USER)
                .provider(AuthProvider.LOCAL)
                .isActive(true)
                .isVerified(false)
                .createdBy("system")
                .modifiedBy("system")
                .build();
    }

    @Nested
    @DisplayName("getChatsByUserId")
    class GetChatsByUserId {

        @Test
        @DisplayName("should return chats for user")
        void shouldReturnChatsForUser() {
            UUID userId = UUID.randomUUID();
            UUID chatId = UUID.randomUUID();
            Chat chat = Chat.builder()
                    .id(chatId)
                    .type(ChatType.DIRECT)
                    .build();
            User user = testUser("test@example.com", "testuser");
            ChatParticipant participant = ChatParticipant.builder()
                    .chatId(chatId)
                    .userId(userId)
                    .chat(chat)
                    .user(user)
                    .role(ParticipantRole.MEMBER)
                    .isMuted(false)
                    .build();

            when(chatRepository.findChatsByUserId(userId)).thenReturn(List.of(chat));
            when(chatParticipantRepository.findByChatId(chatId)).thenReturn(List.of(participant));

            List<ChatResponse> result = chatService.getChatsByUserId(userId);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getId()).isEqualTo(chatId);
            assertThat(result.getFirst().getType()).isEqualTo(ChatType.DIRECT.name());
            assertThat(result.getFirst().getParticipants()).hasSize(1);
            assertThat(result.getFirst().getParticipants().getFirst().getUserNickname()).isEqualTo("testuser");
            verify(chatRepository).findChatsByUserId(userId);
            verify(chatParticipantRepository).findByChatId(chatId);
        }

        @Test
        @DisplayName("should return empty list when user has no chats")
        void shouldReturnEmptyListWhenUserHasNoChats() {
            UUID userId = UUID.randomUUID();
            when(chatRepository.findChatsByUserId(userId)).thenReturn(List.of());

            List<ChatResponse> result = chatService.getChatsByUserId(userId);

            assertThat(result).isEmpty();
            verify(chatParticipantRepository, never()).findByChatId(ArgumentMatchers.any());
        }
    }
}
