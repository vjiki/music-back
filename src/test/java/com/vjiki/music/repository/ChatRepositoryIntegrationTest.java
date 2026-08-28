package com.vjiki.music.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import com.vjiki.music.MusicApplication;
import com.vjiki.music.config.TestContainersConfig;
import com.vjiki.music.entity.AccessLevel;
import com.vjiki.music.entity.AuthProvider;
import com.vjiki.music.entity.Chat;
import com.vjiki.music.entity.ChatParticipant;
import com.vjiki.music.entity.ChatType;
import com.vjiki.music.entity.ParticipantRole;
import com.vjiki.music.entity.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {MusicApplication.class, TestContainersConfig.class})
class ChatRepositoryIntegrationTest {

    @DynamicPropertySource
    static void registerDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", TestContainersConfig.POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", TestContainersConfig.POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", TestContainersConfig.POSTGRES_CONTAINER::getPassword);
        registry.add("spring.datasource.hikari.data-source-properties.ssl", () -> "false");
        registry.add("spring.datasource.hikari.data-source-properties.sslmode", () -> "disable");
        // "update" rather than "create-drop": this slice shares one container with the
        // @SpringBootTest contexts, and dropping tables would break whichever context is alive.
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatParticipantRepository chatParticipantRepository;

    private User saveUser(String email, String nickname) {
        return userRepository.save(User.builder()
                .email(email)
                .nickname(nickname)
                .provider(AuthProvider.LOCAL)
                .accessLevel(AccessLevel.USER)
                .isActive(true)
                .createdBy("system")
                .modifiedBy("system")
                .build());
    }

    @Nested
    @DisplayName("ChatRepository")
    class ChatRepositoryTests {

        @Test
        @DisplayName("should save and find chat by id")
        void shouldSaveAndFindChatById() {
            Chat chat = Chat.builder()
                    .type(ChatType.DIRECT)
                    .build();

            Chat saved = chatRepository.save(chat);
            Optional<Chat> found = chatRepository.findById(saved.getId());

            assertThat(found).isPresent();
            assertThat(found.get().getType()).isEqualTo(ChatType.DIRECT);
        }

        @Test
        @DisplayName("should find chats by user id")
        void shouldFindChatsByUserId() {
            User user1 = saveUser("user1@example.com", "user1");
            saveUser("user2@example.com", "user2");

            Chat chat1 = chatRepository.save(Chat.builder()
                    .type(ChatType.DIRECT)
                    .build());
            Chat chat2 = chatRepository.save(Chat.builder()
                    .type(ChatType.GROUP)
                    .build());

            chatParticipantRepository.save(ChatParticipant.builder()
                    .chatId(chat1.getId())
                    .userId(user1.getId())
                    .chat(chat1)
                    .user(user1)
                    .role(ParticipantRole.MEMBER)
                    .isMuted(false)
                    .build());
            chatParticipantRepository.save(ChatParticipant.builder()
                    .chatId(chat2.getId())
                    .userId(user1.getId())
                    .chat(chat2)
                    .user(user1)
                    .role(ParticipantRole.MEMBER)
                    .isMuted(false)
                    .build());

            List<Chat> chats = chatRepository.findChatsByUserId(user1.getId());

            assertThat(chats).hasSize(2);
        }
    }
}
