package com.vjiki.music.repository

import com.vjiki.music.config.TestContainersConfig
import com.vjiki.music.entity.AccessLevel
import com.vjiki.music.entity.AuthProvider
import com.vjiki.music.entity.User
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration
import java.util.*

@DataJpaTest
@ContextConfiguration(classes = [TestContainersConfig::class])
open class UserRepositoryIntegrationTest : DescribeSpec() {

    override fun extensions() = listOf(SpringExtension)

    @Autowired
    lateinit var userRepository: UserRepository

    init {
        describe("UserRepository") {
        it("should save and find user by id") {
            val user = User(
                email = "test@example.com",
                nickname = "testuser",
                accessLevel = AccessLevel.USER,
                provider = AuthProvider.LOCAL,
                isActive = true,
                isVerified = false,
                createdBy = "system",
                modifiedBy = "system"
            )

            val saved = userRepository.save(user)
            val found = userRepository.findById(saved.id)

            found.isPresent shouldBe true
            found.get().email shouldBe "test@example.com"
            found.get().nickname shouldBe "testuser"
        }

        it("should find user by email") {
            val user = User(
                email = "findme@example.com",
                nickname = "findme",
                accessLevel = AccessLevel.USER,
                provider = AuthProvider.LOCAL,
                isActive = true,
                isVerified = false,
                createdBy = "system",
                modifiedBy = "system"
            )

            userRepository.save(user)
            val found = userRepository.findByEmail("findme@example.com")

            found.isPresent shouldBe true
            found.get().email shouldBe "findme@example.com"
        }

        it("should return empty when user not found by email") {
            val found = userRepository.findByEmail("nonexistent@example.com")
            found.isPresent shouldBe false
        }

        it("should update user") {
            val user = User(
                email = "update@example.com",
                nickname = "original",
                accessLevel = AccessLevel.USER,
                provider = AuthProvider.LOCAL,
                isActive = true,
                isVerified = false,
                createdBy = "system",
                modifiedBy = "system"
            )

            val saved = userRepository.save(user)
            saved.avatarUrl = "https://example.com/avatar.jpg"
            saved.isActive = false
            val updated = userRepository.save(saved)

            updated.avatarUrl shouldBe "https://example.com/avatar.jpg"
            updated.isActive shouldBe false
        }

        it("should delete user") {
            val user = User(
                email = "delete@example.com",
                nickname = "delete",
                accessLevel = AccessLevel.USER,
                provider = AuthProvider.LOCAL,
                isActive = true,
                isVerified = false,
                createdBy = "system",
                modifiedBy = "system"
            )

            val saved = userRepository.save(user)
            userRepository.deleteById(saved.id)

            val found = userRepository.findById(saved.id)
            found.isPresent shouldBe false
        }
    }
    }
}

