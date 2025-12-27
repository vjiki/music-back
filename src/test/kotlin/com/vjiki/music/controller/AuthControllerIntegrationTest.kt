package com.vjiki.music.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.vjiki.music.config.TestContainersConfig
import com.vjiki.music.dto.AuthRequest
import com.vjiki.music.entity.AccessLevel
import com.vjiki.music.entity.AuthProvider
import com.vjiki.music.entity.User
import com.vjiki.music.entity.UserRoleId
import com.vjiki.music.repository.PlaylistRepository
import com.vjiki.music.repository.UserRepository
import com.vjiki.music.repository.UserRoleRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
open class AuthControllerIntegrationTest : DescribeSpec() {

    companion object {
        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            val container = TestContainersConfig.postgresContainer
            registry.add("spring.datasource.url") { container.jdbcUrl }
            registry.add("spring.datasource.username") { container.username }
            registry.add("spring.datasource.password") { container.password }
            registry.add("spring.datasource.driver-class-name") { "org.postgresql.Driver" }
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
            registry.add("spring.jpa.properties.hibernate.default_schema") { "music" }
            registry.add("spring.jpa.show-sql") { "false" }
            registry.add("spring.jpa.properties.hibernate.dialect") { "org.hibernate.dialect.PostgreSQLDialect" }
            registry.add("spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation") { "true" }
        }
    }

    override fun extensions() = listOf(SpringExtension)

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var userRoleRepository: UserRoleRepository

    @Autowired
    lateinit var playlistRepository: PlaylistRepository

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    init {
        describe("POST /api/v1/auth/authenticate") {
            it("should authenticate user with valid credentials") {
                val email = "authcontroller@example.com"
                val password = "password123"

                userRepository.save(
                    User(
                        email = email,
                        nickname = "testuser",
                        passwordHash = passwordEncoder.encode(password),
                        accessLevel = AccessLevel.USER,
                        provider = AuthProvider.LOCAL,
                        isActive = true,
                        isVerified = false,
                        createdBy = "system",
                        modifiedBy = "system"
                    )
                )
                userRepository.flush()

                val authRequest = AuthRequest(email, password)

                mockMvc.perform(
                    post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest))
                )
                    .andExpect(status().isOk)
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.authenticated").value(true))
                    .andExpect(jsonPath("$.userId").exists())
                    .andExpect(jsonPath("$.message").value("Authentication successful"))
            }

            it("should return 401 with invalid credentials") {
                val email = "invalid@example.com"

                userRepository.save(
                    User(
                        email = email,
                        nickname = "testuser",
                        passwordHash = passwordEncoder.encode("correctpassword"),
                        accessLevel = AccessLevel.USER,
                        provider = AuthProvider.LOCAL,
                        isActive = true,
                        isVerified = false,
                        createdBy = "system",
                        modifiedBy = "system"
                    )
                )
                userRepository.flush()

                val authRequest = AuthRequest(email, "wrongpassword")

                mockMvc.perform(
                    post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest))
                )
                    .andExpect(status().isUnauthorized)
                    .andExpect(jsonPath("$.authenticated").value(false))
            }
        }

        describe("POST /api/v1/auth/register") {
            it("should create user if not exists, create default USER role + default playlists, and be idempotent") {
                val email = "new-user@example.com"
                val json = """
                    {
                      "email": "$email",
                      "password": "secret123",
                      "nickname": "New User"
                    }
                """.trimIndent()

                val first = mockMvc.perform(
                    post("/api/v1/auth/register")
                        .contentType("application/json")
                        .content(json)
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.authenticated").value(true))
                    .andExpect(jsonPath("$.userId").exists())
                    .andReturn()

                val idRegex = """"userId"\s*:\s*"([^"]+)"""".toRegex()
                val userId1 = idRegex.find(first.response.contentAsString)?.groupValues?.get(1)
                userId1 shouldNotBe null

                val userUuid = UUID.fromString(userId1!!)
                userRoleRepository.existsById(UserRoleId(userUuid, "USER")) shouldBe true

                val playlists = playlistRepository.findByUserId(userUuid)
                playlists.map { it.name }.toSet() shouldBe setOf("DEFAULT_LIKES", "DEFAULT_DISLIKES")
                playlists.all { it.type == "DEFAULT" } shouldBe true
                playlists.all { it.isPublic } shouldBe true

                // Second call should return same userId
                val second = mockMvc.perform(
                    post("/api/v1/auth/register")
                        .contentType("application/json")
                        .content(json)
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.authenticated").value(true))
                    .andExpect(jsonPath("$.userId").exists())
                    .andReturn()

                val userId2 = idRegex.find(second.response.contentAsString)?.groupValues?.get(1)
                userId2 shouldBe userId1
            }

            it("should allow registering without password") {
                val email = "nopass@example.com"
                val json = """{ "email": "$email", "nickname": "No Pass" }"""

                mockMvc.perform(
                    post("/api/v1/auth/register")
                        .contentType("application/json")
                        .content(json)
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.authenticated").value(true))
                    .andExpect(jsonPath("$.userId").exists())

                val user = userRepository.findByEmail(email).orElseThrow()
                user.passwordHash shouldBe null
            }
        }

        describe("GET /api/v1/auth/exists") {
            it("should return exists=false for unknown email") {
                mockMvc.perform(get("/api/v1/auth/exists").param("email", "unknown@example.com"))
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.exists").value(false))
                    .andExpect(jsonPath("$.userId").doesNotExist())
            }
        }
    }
}


