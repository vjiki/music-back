package com.vjiki.music.service

import com.vjiki.music.config.TestContainersConfig
import com.vjiki.music.entity.AccessLevel
import com.vjiki.music.entity.AuthProvider
import com.vjiki.music.entity.Playlist
import com.vjiki.music.entity.User
import com.vjiki.music.repository.PlaylistRepository
import com.vjiki.music.repository.UserRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import java.util.*

@SpringBootTest
@ContextConfiguration(classes = [TestContainersConfig::class])
@Transactional
open class PlaylistServiceIntegrationTest : DescribeSpec() {

    override fun extensions() = listOf(SpringExtension)

    @Autowired
    lateinit var playlistService: PlaylistService

    @Autowired
    lateinit var playlistRepository: PlaylistRepository

    @Autowired
    lateinit var userRepository: UserRepository

    fun createTestUser(): User {
        return userRepository.save(
            User(
                email = "playlistuser${UUID.randomUUID()}@example.com",
                nickname = "playlistuser",
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
        describe("PlaylistService Integration Tests") {
        it("should get playlists by user id") {
            val user = createTestUser()
            val playlist1 = createPlaylist(user, "Playlist 1")
            val playlist2 = createPlaylist(user, "Playlist 2")

            val result = playlistService.getPlaylistsByUserId(user.id)

            result.size shouldBe 2
            result.map { it.name } shouldBe listOf("Playlist 2", "Playlist 1")
        }

        it("should return empty list when user has no playlists") {
            val user = createTestUser()

            val result = playlistService.getPlaylistsByUserId(user.id)

            result shouldBe emptyList()
        }

        it("should get playlist with songs") {
            val user = createTestUser()
            val playlist = createPlaylist(user, "My Playlist")

            val result = playlistService.getPlaylistWithSongs(playlist.id)

            result.id shouldBe playlist.id
            result.name shouldBe "My Playlist"
            result.userId shouldBe user.id
            result.songs shouldBe emptyList()
        }

        it("should throw exception when playlist not found") {
            val exception = kotlin.runCatching {
                playlistService.getPlaylistWithSongs(UUID.randomUUID())
            }.exceptionOrNull()

            exception shouldNotBe null
            exception?.message?.contains("not found") shouldBe true
        }
    }
    }
}

