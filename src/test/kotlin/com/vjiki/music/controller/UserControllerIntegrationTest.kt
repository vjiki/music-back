package com.vjiki.music.controller

import com.vjiki.music.dto.UserResponse
import com.vjiki.music.service.UserService
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.OffsetDateTime
import java.util.*

@WebMvcTest(UserController::class)
class UserControllerIntegrationTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val objectMapper: ObjectMapper
) : DescribeSpec({

    val userService = mockk<UserService>()

    describe("GET /api/v1/users/{userId}") {
        it("should return user when found") {
            val userId = UUID.randomUUID()
            val userResponse = UserResponse(
                id = userId,
                email = "test@example.com",
                nickname = "testuser",
                avatarUrl = null,
                accessLevel = "USER",
                isActive = true,
                isVerified = false,
                lastLoginAt = null,
                createdAt = OffsetDateTime.now()
            )

            every { userService.getUserById(userId) } returns userResponse

            mockMvc.perform(get("/api/v1/users/$userId"))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.nickname").value("testuser"))
        }

        it("should return 500 when user not found") {
            val userId = UUID.randomUUID()
            every { userService.getUserById(userId) } throws RuntimeException("User not found")

            mockMvc.perform(get("/api/v1/users/$userId"))
                .andExpect(status().is5xxServerError)
        }
    }
})

