package com.vjiki.music.service

import com.vjiki.music.dto.MessageResponse
import com.vjiki.music.entity.Message
import com.vjiki.music.entity.MessageType
import com.vjiki.music.repository.MessageRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.OffsetDateTime
import java.util.*

class MessageServiceTest : DescribeSpec({

    val messageRepository = mockk<MessageRepository>()
    val messageService = MessageServiceImpl(messageRepository)

    describe("getMessagesByChatId") {
        it("should return messages for chat") {
            val chatId = UUID.randomUUID()
            val userId1 = UUID.randomUUID()
            val userId2 = UUID.randomUUID()

            val message = Message(
                id = UUID.randomUUID(),
                chatId = chatId,
                senderId = userId1,
                content = "Hello",
                messageType = MessageType.TEXT,
                isDeleted = false
            )

            every {
                messageRepository.findMessagesByChatIdAndUsers(chatId, userId1, userId2)
            } returns listOf(message)

            val result = messageService.getMessagesByChatId(chatId, userId1, userId2)

            result.size shouldBe 1
            result[0].content shouldBe "Hello"
            verify { messageRepository.findMessagesByChatIdAndUsers(chatId, userId1, userId2) }
        }

        it("should return empty list when no messages found") {
            val chatId = UUID.randomUUID()
            val userId1 = UUID.randomUUID()
            val userId2 = UUID.randomUUID()

            every {
                messageRepository.findMessagesByChatIdAndUsers(chatId, userId1, userId2)
            } returns emptyList()

            val result = messageService.getMessagesByChatId(chatId, userId1, userId2)

            result shouldBe emptyList()
        }
    }
})

