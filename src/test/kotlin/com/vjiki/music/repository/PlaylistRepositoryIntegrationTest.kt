package com.vjiki.music.repository

import com.vjiki.music.config.TestContainersConfig
import com.vjiki.music.entity.AccessLevel
import com.vjiki.music.entity.AuthProvider
import com.vjiki.music.entity.Playlist
import com.vjiki.music.entity.User
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration
import java.util.*

@DataJpaTest
@ContextConfiguration(classes = [TestContainersConfig::class])
open class PlaylistRepositoryIntegrationTest : DescribeSpec() {

    override fun extensions() = listOf(SpringExtension)

    @Autowired
    lateinit var playlistRepository: PlaylistRepository

    @Autowired
    lateinit var userRepository: UserRepository

    init {
        describe("PlaylistRepository") {
        it("should save and find playlist by id") {
            val user = userRepository.save(User(
                email = "test@example.com",
                nickname = "testuser",
                provider = AuthProvider.LOCAL,
                accessLevel = AccessLevel.USER,
                isActive = true,
                createdBy = "system",
                modifiedBy = "system"
            ))

            val playlist = Playlist(
                user = user,
                name = "My Playlist",
                type = "CUSTOM",
                isPublic = false,
                createdBy = "system",
                modifiedBy = "system"
            )

            val saved = playlistRepository.save(playlist)
            val found = playlistRepository.findById(saved.id)

            found.isPresent shouldBe true
            found.get().name shouldBe "My Playlist"
        }

        it("should find playlists by user id") {
            val user = userRepository.save(User(
                email = "user1@example.com",
                nickname = "user1",
                provider = AuthProvider.LOCAL,
                accessLevel = AccessLevel.USER,
                isActive = true,
                createdBy = "system",
                modifiedBy = "system"
            ))

            val playlist1 = playlistRepository.save(Playlist(
                user = user,
                name = "Playlist 1",
                type = "CUSTOM",
                createdBy = "system",
                modifiedBy = "system"
            ))
            val playlist2 = playlistRepository.save(Playlist(
                user = user,
                name = "Playlist 2",
                type = "CUSTOM",
                createdBy = "system",
                modifiedBy = "system"
            ))

            val playlists = playlistRepository.findByUserIdWithUser(user.id)

            playlists.size shouldBe 2
            playlists.map { it.name } shouldBe listOf("Playlist 1", "Playlist 2")
        }

        it("should find playlist by user id and name") {
            val user = userRepository.save(User(
                email = "user2@example.com",
                nickname = "user2",
                provider = AuthProvider.LOCAL,
                accessLevel = AccessLevel.USER,
                isActive = true,
                createdBy = "system",
                modifiedBy = "system"
            ))

            val playlist = playlistRepository.save(Playlist(
                user = user,
                name = "Unique Playlist",
                type = "CUSTOM",
                createdBy = "system",
                modifiedBy = "system"
            ))

            val found = playlistRepository.findByUserIdAndName(user.id, "Unique Playlist")

            found.isPresent shouldBe true
            found.get().name shouldBe "Unique Playlist"
        }
    }
    }
}
