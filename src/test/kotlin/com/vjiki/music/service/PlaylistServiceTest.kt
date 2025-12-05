package com.vjiki.music.service

import com.vjiki.music.dto.PlaylistResponse
import com.vjiki.music.entity.Playlist
import com.vjiki.music.entity.User
import com.vjiki.music.repository.PlaylistRepository
import com.vjiki.music.repository.PlaylistSongRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.OffsetDateTime
import java.util.*

class PlaylistServiceTest : DescribeSpec({

    val playlistRepository = mockk<PlaylistRepository>()
    val playlistSongRepository = mockk<PlaylistSongRepository>()
    val playlistService = PlaylistServiceImpl(playlistRepository, playlistSongRepository)

    describe("getPlaylistsByUserId") {
        it("should return playlists for user") {
            val userId = UUID.randomUUID()
            val user = User(
                id = userId,
                email = "test@example.com",
                nickname = "testuser",
                createdBy = "system",
                modifiedBy = "system"
            )

            val playlist = Playlist(
                id = UUID.randomUUID(),
                user = user,
                name = "My Playlist",
                description = "Test playlist",
                type = "CUSTOM",
                isPublic = false,
                createdBy = "system",
                modifiedBy = "system"
            )

            every { playlistRepository.findByUserIdWithUser(userId) } returns listOf(playlist)

            val result = playlistService.getPlaylistsByUserId(userId)

            result.size shouldBe 1
            result[0].name shouldBe "My Playlist"
            result[0].userId shouldBe userId
            verify { playlistRepository.findByUserIdWithUser(userId) }
        }

        it("should return empty list when no playlists found") {
            val userId = UUID.randomUUID()
            every { playlistRepository.findByUserIdWithUser(userId) } returns emptyList()

            val result = playlistService.getPlaylistsByUserId(userId)

            result shouldBe emptyList()
        }
    }

    describe("getPlaylistWithSongs") {
        it("should return playlist with songs when found") {
            val playlistId = UUID.randomUUID()
            val userId = UUID.randomUUID()
            val user = User(
                id = userId,
                email = "test@example.com",
                nickname = "testuser",
                createdBy = "system",
                modifiedBy = "system"
            )

            val playlist = Playlist(
                id = playlistId,
                user = user,
                name = "My Playlist",
                description = "Test playlist",
                type = "CUSTOM",
                isPublic = false,
                createdBy = "system",
                modifiedBy = "system"
            )

            every { playlistRepository.findById(playlistId) } returns Optional.of(playlist)
            every { playlistSongRepository.findByPlaylistIdWithSong(playlistId) } returns emptyList()

            val result = playlistService.getPlaylistWithSongs(playlistId)

            result.id shouldBe playlistId
            result.name shouldBe "My Playlist"
            result.songs shouldBe emptyList()
        }

        it("should throw exception when playlist not found") {
            val playlistId = UUID.randomUUID()
            every { playlistRepository.findById(playlistId) } returns Optional.empty()

            val exception = kotlin.runCatching { playlistService.getPlaylistWithSongs(playlistId) }
                .exceptionOrNull()

            exception shouldNotBe null
            exception?.message shouldBe "Playlist not found: $playlistId"
        }
    }
})

