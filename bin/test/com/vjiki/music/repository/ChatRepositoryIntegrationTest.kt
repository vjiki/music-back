package com.vjiki.music.repository

import com.vjiki.music.config.TestContainersConfig
import com.vjiki.music.entity.*
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration
import java.util.*

@DataJpaTest
@ContextConfiguration(classes = [TestContainersConfig::class])
open class ChatRepositoryIntegrationTest : DescribeSpec() {

    override fun extensions() = listOf(SpringExtension)

    @Autowired
    lateinit var chatRepository: ChatRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var chatParticipantRepository: ChatParticipantRepository

    init {
        describe("ChatRepository") {
        it("should save and find chat by id") {
            val chat = Chat(
                type = ChatType.DIRECT
            )

            val saved = chatRepository.save(chat)
            val found = chatRepository.findById(saved.id)

            found.isPresent shouldBe true
            found.get().type shouldBe ChatType.DIRECT
        }

        it("should find chats by user id") {
            val user1 = userRepository.save(User(
                email = "user1@example.com",
                nickname = "user1",
                provider = AuthProvider.LOCAL,
                accessLevel = AccessLevel.USER,
                isActive = true,
                createdBy = "system",
                modifiedBy = "system"
            ))
            val user2 = userRepository.save(User(
                email = "user2@example.com",
                nickname = "user2",
                provider = AuthProvider.LOCAL,
                accessLevel = AccessLevel.USER,
                isActive = true,
                createdBy = "system",
                modifiedBy = "system"
            ))

            val chat1 = chatRepository.save(Chat(
                type = ChatType.DIRECT
            ))
            val chat2 = chatRepository.save(Chat(
                type = ChatType.GROUP
            ))

            chatParticipantRepository.save(ChatParticipant(
                chatId = chat1.id,
                userId = user1.id,
                chat = chat1,
                user = user1,
                role = ParticipantRole.MEMBER,
                isMuted = false
            ))
            chatParticipantRepository.save(ChatParticipant(
                chatId = chat2.id,
                userId = user1.id,
                chat = chat2,
                user = user1,
                role = ParticipantRole.MEMBER,
                isMuted = false
            ))

            val chats = chatRepository.findChatsByUserId(user1.id)

            chats.size shouldBe 2
        }
    }
    }
}

