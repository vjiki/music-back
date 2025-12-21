package com.vjiki.music.controller

import com.vjiki.music.config.TestContainersConfig
import com.vjiki.music.dto.AuthRequest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import com.vjiki.music.entity.AccessLevel
import com.vjiki.music.entity.AuthProvider
import com.vjiki.music.entity.User
import com.vjiki.music.repository.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import java.util.*

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
    lateinit var passwordEncoder: PasswordEncoder

    init {
    describe("AuthController Integration Tests") {
        it("should authenticate user with valid credentials") {
            val email = "authcontroller@example.com"
            val password = "password123"
            
            val user = User(
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
            userRepository.save(user)
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
            val password = "wrongpassword"
            
            val user = User(
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
            userRepository.save(user)
            userRepository.flush()

            val authRequest = AuthRequest(email, password)

            mockMvc.perform(
                post("/api/v1/auth/authenticate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(authRequest))
            )
                .andExpect(status().isUnauthorized)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.authenticated").value(false))
                .andExpect(jsonPath("$.message").value("Invalid email or password"))
        }

        it("should return 401 for non-existent user") {
            val authRequest = AuthRequest("nonexistent@example.com", "password")

            mockMvc.perform(
                post("/api/v1/auth/authenticate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(authRequest))
            )
                .andExpect(status().isUnauthorized)
                .andExpect(jsonPath("$.authenticated").value(false))
        }

        it("should return 401 for inactive user") {
            val email = "inactive@example.com"
            val password = "password123"
            
            val user = User(
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
            val savedUser = userRepository.save(user)
            userRepository.flush()
            savedUser.isActive = false
            userRepository.save(savedUser)
            userRepository.flush()

            val authRequest = AuthRequest(email, password)

            mockMvc.perform(
                post("/api/v1/auth/authenticate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(authRequest))
            )
                .andExpect(status().isUnauthorized)
                .andExpect(jsonPath("$.authenticated").value(false))
                .andExpect(jsonPath("$.message").value("User account is inactive"))
        }
    }
    }
}
