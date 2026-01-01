package com.vjiki.music.controller

import com.vjiki.music.config.TestContainersConfig
import com.vjiki.music.dto.SongLikeRequest
import com.vjiki.music.entity.*
import com.vjiki.music.repository.*
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import com.fasterxml.jackson.databind.ObjectMapper

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = [TestContainersConfig::class])
@Transactional
open class SongLikeControllerIntegrationTest : DescribeSpec() {

    override fun extensions() = listOf(SpringExtension)

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var songRepository: SongRepository

    @Autowired
    lateinit var likeRepository: LikeRepository

    @Autowired
    lateinit var dislikeRepository: DislikeRepository

    init {
        describe("POST /api/v1/songs/{songId}/like") {
        it("should like a song") {
            val user = userRepository.save(User(
                email = "test@example.com",
                nickname = "testuser",
                provider = AuthProvider.LOCAL,
                accessLevel = AccessLevel.USER,
                isActive = true,
                createdBy = "system",
                modifiedBy = "system"
            ))
            val song = songRepository.save(Song(
                artists = mapOf("default" to listOf("Artist")),
                audioUrls = mapOf("default" to "http://audio.com/song.mp3"),
                coverUrls = mapOf("default" to "http://cover.com/cover.jpg"),
                title = "Test Song",
                active = true,
                createdBy = "system",
                modifiedBy = "system"
            ))

            val request = SongLikeRequest(user.id, song.id)

            mockMvc.perform(
                post("/api/v1/songs/{songId}/like", song.id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(user("test").roles("USER"))
            )
                .andExpect(status().isOk)

            val isLiked = likeRepository.existsByUserIdAndSongIdAndRevokedAtIsNull(user.id, song.id)
            isLiked shouldBe true
        }
    }

    describe("POST /api/v1/songs/{songId}/dislike") {
        it("should dislike a song") {
            val user = userRepository.save(User(
                email = "test2@example.com",
                nickname = "testuser2",
                provider = AuthProvider.LOCAL,
                accessLevel = AccessLevel.USER,
                isActive = true,
                createdBy = "system",
                modifiedBy = "system"
            ))
            val song = songRepository.save(Song(
                artists = mapOf("default" to listOf("Artist")),
                audioUrls = mapOf("default" to "http://audio.com/song.mp3"),
                coverUrls = mapOf("default" to "http://cover.com/cover.jpg"),
                title = "Test Song",
                active = true,
                createdBy = "system",
                modifiedBy = "system"
            ))

            val request = SongLikeRequest(user.id, song.id)

            mockMvc.perform(
                post("/api/v1/songs/{songId}/dislike", song.id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(user("test").roles("USER"))
            )
                .andExpect(status().isOk)

            val isDisliked = dislikeRepository.existsByUserIdAndSongIdAndRevokedAtIsNull(user.id, song.id)
            isDisliked shouldBe true
        }
    }

    describe("GET /api/v1/songs/{songId}/like-info") {
        it("should return like/dislike info") {
            val user = userRepository.save(User(
                email = "test3@example.com",
                nickname = "testuser3",
                provider = AuthProvider.LOCAL,
                accessLevel = AccessLevel.USER,
                isActive = true,
                createdBy = "system",
                modifiedBy = "system"
            ))
            val song = songRepository.save(Song(
                artists = mapOf("default" to listOf("Artist")),
                audioUrls = mapOf("default" to "http://audio.com/song.mp3"),
                coverUrls = mapOf("default" to "http://cover.com/cover.jpg"),
                title = "Test Song",
                active = true,
                createdBy = "system",
                modifiedBy = "system"
            ))

            likeRepository.save(Like(
                user = user,
                song = song,
                createdBy = "system"
            ))

            mockMvc.perform(
                get("/api/v1/songs/{songId}/like-info", song.id)
                    .param("userId", user.id.toString())
                    .with(user("test").roles("USER"))
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.isLiked").value(true))
                .andExpect(jsonPath("$.isDisliked").value(false))
                .andExpect(jsonPath("$.likesCount").exists())
                .andExpect(jsonPath("$.dislikesCount").exists())
        }
    }
    }
}

