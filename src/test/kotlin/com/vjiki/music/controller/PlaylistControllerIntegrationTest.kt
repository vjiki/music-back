package com.vjiki.music.controller

import com.vjiki.music.config.TestContainersConfig
import com.vjiki.music.entity.AccessLevel
import com.vjiki.music.entity.AuthProvider
import com.vjiki.music.entity.Playlist
import com.vjiki.music.entity.User
import com.vjiki.music.repository.PlaylistRepository
import com.vjiki.music.repository.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = [TestContainersConfig::class])
@Transactional
open class PlaylistControllerIntegrationTest : DescribeSpec() {

    override fun extensions() = listOf(SpringExtension)

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var playlistRepository: PlaylistRepository

    @Autowired
    lateinit var userRepository: UserRepository

    fun createTestUser(): User {
        return userRepository.save(
            User(
                email = "controlleruser${UUID.randomUUID()}@example.com",
                nickname = "controlleruser",
                accessLevel = AccessLevel.USER,
                provider = AuthProvider.LOCAL,
                isActive = true,
                isVerified = false,
                createdBy = "system",
                modifiedBy = "system"
            )
        )
    }

    fun createPlaylist(user: User, name: String): Playlist {
        return playlistRepository.save(
            Playlist(
                user = user,
                name = name,
                type = "CUSTOM",
                isPublic = false,
                createdBy = "system",
                modifiedBy = "system"
            )
        )
    }

    init {
        describe("PlaylistController Integration Tests") {
        it("should get playlists by user id") {
            val user = createTestUser()
            val playlist = createPlaylist(user, "Test Playlist")

            mockMvc.perform(get("/api/v1/playlists/user/${user.id}").with(user("test").roles("USER")))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(playlist.id.toString()))
                .andExpect(jsonPath("$[0].name").value("Test Playlist"))
                .andExpect(jsonPath("$[0].userId").value(user.id.toString()))
        }

        it("should return empty list when user has no playlists") {
            val user = createTestUser()

            mockMvc.perform(get("/api/v1/playlists/user/${user.id}").with(user("test").roles("USER")))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty)
        }

        it("should get playlist with songs by playlist id") {
            val user = createTestUser()
            val playlist = createPlaylist(user, "My Playlist")

            mockMvc.perform(get("/api/v1/playlists/${playlist.id}").with(user("test").roles("USER")))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(playlist.id.toString()))
                .andExpect(jsonPath("$.name").value("My Playlist"))
                .andExpect(jsonPath("$.songs").isArray)
        }

        it("should return 500 when playlist not found") {
            val nonExistentId = UUID.randomUUID()

            mockMvc.perform(get("/api/v1/playlists/$nonExistentId").with(user("test").roles("USER")))
                .andExpect(status().is5xxServerError)
        }
    }
    }
}

