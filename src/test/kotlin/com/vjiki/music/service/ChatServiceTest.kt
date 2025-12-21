package com.vjiki.music.service

import com.vjiki.music.dto.ChatResponse
import com.vjiki.music.entity.*
import com.vjiki.music.repository.ChatParticipantRepository
import com.vjiki.music.repository.ChatRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*

class ChatServiceTest : DescribeSpec({

    val chatRepository = mockk<ChatRepository>()
    val chatParticipantRepository = mockk<ChatParticipantRepository>()
    val chatService = ChatServiceImpl(chatRepository, chatParticipantRepository)

    describe("getChatsByUserId") {
        it("should return chats for user") {
            val userId = UUID.randomUUID()
            val chatId = UUID.randomUUID()
            val chat = Chat(
                id = chatId,
                type = ChatType.DIRECT
            )
            val participant = ChatParticipant(
                chatId = chatId,
                userId = userId,
                chat = chat,
                user = mockk(),
                role = ParticipantRole.MEMBER,
                isMuted = false
            )

            every { chatRepository.findChatsByUserId(userId) } returns listOf(chat)
            every { chatParticipantRepository.findByChatId(chat.id) } returns listOf(participant)

            val result = chatService.getChatsByUserId(userId)

            result.size shouldBe 1
            result[0].id shouldBe chat.id
            verify { chatRepository.findChatsByUserId(userId) }
            verify { chatParticipantRepository.findByChatId(chat.id) }
        }

        it("should return empty list when user has no chats") {
            val userId = UUID.randomUUID()
            every { chatRepository.findChatsByUserId(userId) } returns emptyList()

            val result = chatService.getChatsByUserId(userId)

            result shouldBe emptyList()
        }
    }
})

