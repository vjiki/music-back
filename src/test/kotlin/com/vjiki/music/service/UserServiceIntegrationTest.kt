package com.vjiki.music.service

import com.vjiki.music.config.TestContainersConfig
import com.vjiki.music.dto.AuthRequest
import com.vjiki.music.entity.AccessLevel
import com.vjiki.music.entity.AuthProvider
import com.vjiki.music.entity.User
import com.vjiki.music.repository.UserRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import java.util.*

@SpringBootTest
@ContextConfiguration(classes = [TestContainersConfig::class])
@Transactional
open class UserServiceIntegrationTest : DescribeSpec() {

    override fun extensions() = listOf(SpringExtension)

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    fun createTestUser(email: String, nickname: String): User {
        return userRepository.save(
            User(
                email = email,
                nickname = nickname,
                accessLevel = AccessLevel.USER,
                provider = AuthProvider.LOCAL,
                isActive = true,
                isVerified = false,
                createdBy = "system",
                modifiedBy = "system"
            )
        )
    }

    fun createTestUserWithPassword(email: String, nickname: String, password: String): User {
        val user = User(
            email = email,
            nickname = nickname,
            passwordHash = passwordEncoder.encode(password),
            accessLevel = AccessLevel.USER,
            provider = AuthProvider.LOCAL,
            isActive = true,
            isVerified = false,
            createdBy = "system",
            modifiedBy = "system"
        )
        return userRepository.save(user)
    }

    init {
        describe("UserService Integration Tests") {
        it("should get user by id") {
            val user = createTestUser("getuser@example.com", "getuser")
            val result = userService.getUserById(user.id)

            result.id shouldBe user.id
            result.email shouldBe "getuser@example.com"
            result.nickname shouldBe "getuser"
        }

        it("should throw exception when user not found") {
            val exception = kotlin.runCatching {
                userService.getUserById(UUID.randomUUID())
            }.exceptionOrNull()

            exception shouldNotBe null
            exception?.message?.contains("not found") shouldBe true
        }

        it("should authenticate user with valid credentials") {
            val password = "password123"
            val user = createTestUserWithPassword("auth@example.com", "authuser", password)

            val result = userService.authenticate(AuthRequest("auth@example.com", password))

            result.authenticated shouldBe true
            result.userId shouldBe user.id
            result.message shouldBe "Authentication successful"
        }

        it("should fail authentication with invalid password") {
            val user = createTestUserWithPassword("invalid@example.com", "invaliduser", "correctpassword")

            val result = userService.authenticate(AuthRequest("invalid@example.com", "wrongpassword"))

            result.authenticated shouldBe false
            result.message shouldBe "Invalid email or password"
        }

        it("should fail authentication for inactive user") {
            val password = "password123"
            val user = createTestUserWithPassword("inactive@example.com", "inactiveuser", password)
            user.isActive = false
            userRepository.save(user)

            val result = userService.authenticate(AuthRequest("inactive@example.com", password))

            result.authenticated shouldBe false
            result.message shouldBe "User account is inactive"
        }

        it("should fail authentication for non-existent user") {
            val result = userService.authenticate(AuthRequest("nonexistent@example.com", "password"))

            result.authenticated shouldBe false
            result.message shouldBe "Invalid email or password"
        }
    }
    }
}

