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
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.transaction.annotation.Transactional
import java.util.*

@SpringBootTest
@Transactional
open class PlaylistServiceIntegrationTest : DescribeSpec() {

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
    lateinit var playlistService: PlaylistServiceImpl

    @Autowired
    lateinit var playlistRepository: PlaylistRepository

    @Autowired
    lateinit var userRepository: UserRepository

    fun createTestUser(): User {
        val user = User(
            email = "playlistuser${UUID.randomUUID()}@example.com",
            nickname = "playlistuser",
            accessLevel = AccessLevel.USER,
            provider = AuthProvider.LOCAL,
            isActive = true,
            isVerified = false,
            createdBy = "system",
            modifiedBy = "system"
        )
        val saved = userRepository.save(user)
        userRepository.flush()
        return saved
    }

    fun createPlaylist(user: User, name: String): Playlist {
        val playlist = Playlist(
            user = user,
            name = name,
            type = "CUSTOM",
            isPublic = false,
            createdBy = "system",
            modifiedBy = "system"
        )
        val saved = playlistRepository.save(playlist)
        playlistRepository.flush()
        return saved
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

