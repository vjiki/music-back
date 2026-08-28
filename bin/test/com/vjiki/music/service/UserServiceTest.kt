package com.vjiki.music.service

import com.vjiki.music.dto.AuthRequest
import com.vjiki.music.dto.AuthResponse
import com.vjiki.music.dto.UserResponse
import com.vjiki.music.entity.AccessLevel
import com.vjiki.music.entity.AuthProvider
import com.vjiki.music.entity.User
import com.vjiki.music.repository.UserRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.OffsetDateTime
import java.util.*

class UserServiceTest : DescribeSpec({

    val userRepository = mockk<UserRepository>()
    val passwordEncoder = mockk<PasswordEncoder>()
    val userService = UserServiceImpl(userRepository, passwordEncoder)

    describe("getUserById") {
        it("should return user when found") {
            val userId = UUID.randomUUID()
            val user = User(
                id = userId,
                email = "test@example.com",
                passwordHash = "hashed",
                provider = AuthProvider.LOCAL,
                nickname = "testuser",
                accessLevel = AccessLevel.USER,
                isActive = true,
                isVerified = false,
                createdBy = "system",
                modifiedBy = "system"
            )

            every { userRepository.findById(userId) } returns Optional.of(user)

            val result = userService.getUserById(userId)

            result.id shouldBe userId
            result.email shouldBe "test@example.com"
            result.nickname shouldBe "testuser"
            verify { userRepository.findById(userId) }
        }

        it("should throw exception when user not found") {
            val userId = UUID.randomUUID()
            every { userRepository.findById(userId) } returns Optional.empty()

            val exception = kotlin.runCatching { userService.getUserById(userId) }
                .exceptionOrNull()

            exception shouldNotBe null
            exception?.message shouldBe "User not found with id: $userId"
        }
    }

    describe("authenticate") {
        it("should return success for valid credentials") {
            val email = "test@example.com"
            val password = "password123"
            val hashedPassword = "hashedPassword"
            val userId = UUID.randomUUID()

            val user = User(
                id = userId,
                email = email,
                passwordHash = hashedPassword,
                provider = AuthProvider.LOCAL,
                nickname = "testuser",
                accessLevel = AccessLevel.USER,
                isActive = true,
                isVerified = false,
                createdBy = "system",
                modifiedBy = "system"
            )

            every { userRepository.findByEmail(email) } returns Optional.of(user)
            every { passwordEncoder.matches(password, hashedPassword) } returns true

            val result = userService.authenticate(AuthRequest(email, password))

            result.authenticated shouldBe true
            result.userId shouldBe userId
            result.message shouldBe "Authentication successful"
        }

        it("should return failure for invalid password") {
            val email = "test@example.com"
            val password = "wrongPassword"
            val hashedPassword = "hashedPassword"
            val userId = UUID.randomUUID()

            val user = User(
                id = userId,
                email = email,
                passwordHash = hashedPassword,
                provider = AuthProvider.LOCAL,
                nickname = "testuser",
                accessLevel = AccessLevel.USER,
                isActive = true,
                isVerified = false,
                createdBy = "system",
                modifiedBy = "system"
            )

            every { userRepository.findByEmail(email) } returns Optional.of(user)
            every { passwordEncoder.matches(password, hashedPassword) } returns false

            val result = userService.authenticate(AuthRequest(email, password))

            result.authenticated shouldBe false
            result.userId shouldBe null
            result.message shouldBe "Invalid email or password"
        }

        it("should return failure for inactive user") {
            val email = "test@example.com"
            val password = "password123"
            val userId = UUID.randomUUID()

            val user = User(
                id = userId,
                email = email,
                passwordHash = "hashed",
                provider = AuthProvider.LOCAL,
                nickname = "testuser",
                accessLevel = AccessLevel.USER,
                isActive = false,
                isVerified = false,
                createdBy = "system",
                modifiedBy = "system"
            )

            every { userRepository.findByEmail(email) } returns Optional.of(user)

            val result = userService.authenticate(AuthRequest(email, password))

            result.authenticated shouldBe false
            result.message shouldBe "User account is inactive"
        }

        it("should return failure for non-existent user") {
            val email = "nonexistent@example.com"
            val password = "password123"

            every { userRepository.findByEmail(email) } returns Optional.empty()

            val result = userService.authenticate(AuthRequest(email, password))

            result.authenticated shouldBe false
            result.message shouldBe "Invalid email or password"
        }
    }
})

