package com.vjiki.music.service

import com.vjiki.music.dto.SongLikeResponse
import com.vjiki.music.entity.*
import com.vjiki.music.repository.*
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.springframework.data.repository.findByIdOrNull
import java.time.OffsetDateTime
import java.util.*

class SongLikeServiceTest : DescribeSpec({

    val likeRepository = mockk<LikeRepository>()
    val dislikeRepository = mockk<DislikeRepository>()
    val songRepository = mockk<SongRepository>()
    val userRepository = mockk<UserRepository>()
    val playlistRepository = mockk<PlaylistRepository>()
    val playlistSongRepository = mockk<PlaylistSongRepository>()

    val songLikeService = SongLikeServiceImpl(
        likeRepository,
        dislikeRepository,
        songRepository,
        userRepository,
        playlistRepository,
        playlistSongRepository
    )

    describe("likeSong") {
        val userId = UUID.randomUUID()
        val songId = UUID.randomUUID()
        val user = User(
            id = userId,
            email = "test@example.com",
            nickname = "testuser",
            provider = AuthProvider.LOCAL,
            accessLevel = AccessLevel.USER,
            isActive = true,
            createdBy = "system",
            modifiedBy = "system"
        )
        val song = Song(
            id = songId,
            artists = mapOf("default" to listOf("Artist")),
            audioUrls = mapOf("default" to "http://audio.com"),
            coverUrls = mapOf("default" to "http://cover.com"),
            title = "Test Song",
            active = true,
            createdBy = "system",
            modifiedBy = "system"
        )

        it("should like a song when not already liked") {
            every { likeRepository.existsByUserIdAndSongIdAndRevokedAtIsNull(userId, songId) } returns false
            every { dislikeRepository.findByUserIdAndSongIdAndRevokedAtIsNull(userId, songId) } returns Optional.empty()
            every { userRepository.findById(userId) } returns Optional.of(user)
            every { songRepository.findById(songId) } returns Optional.of(song)
            every { likeRepository.save(any()) } returns mockk()
            every { playlistRepository.findByUserIdAndName(userId, "DEFAULT_LIKES") } returns Optional.empty()
            every { playlistRepository.save(any()) } returns mockk()
            every { playlistSongRepository.findByPlaylistIdAndSongId(any(), songId) } returns Optional.empty()
            every { playlistSongRepository.findByPlaylistIdWithSong(any()) } returns emptyList()
            every { playlistSongRepository.save(any()) } returns mockk()
            every { likeRepository.countBySongIdAndRevokedAtIsNull(songId) } returns 1L
            every { dislikeRepository.countBySongIdAndRevokedAtIsNull(songId) } returns 0L
            every { songRepository.save(any()) } returns song

            songLikeService.likeSong(userId, songId)

            verify(exactly = 1) { likeRepository.save(any()) }
            verify(exactly = 1) { songRepository.save(any()) }
        }

        it("should not like if already liked") {
            every { likeRepository.existsByUserIdAndSongIdAndRevokedAtIsNull(userId, songId) } returns true

            songLikeService.likeSong(userId, songId)

            verify(exactly = 0) { likeRepository.save(any()) }
        }

        it("should revoke dislike when liking") {
            val dislike = Dislike(
                user = user,
                song = song,
                createdBy = "system"
            )

            every { likeRepository.existsByUserIdAndSongIdAndRevokedAtIsNull(userId, songId) } returns false
            every { dislikeRepository.findByUserIdAndSongIdAndRevokedAtIsNull(userId, songId) } returns Optional.of(dislike)
            every { dislikeRepository.save(any()) } returns dislike
            every { playlistRepository.findByUserIdAndName(userId, "DEFAULT_DISLIKES") } returns Optional.of(mockk())
            every { playlistSongRepository.deleteByPlaylistIdAndSongId(any(), songId) } just Runs
            every { userRepository.findById(userId) } returns Optional.of(user)
            every { songRepository.findById(songId) } returns Optional.of(song)
            every { likeRepository.save(any()) } returns mockk()
            every { playlistRepository.findByUserIdAndName(userId, "DEFAULT_LIKES") } returns Optional.empty()
            every { playlistRepository.save(any()) } returns mockk()
            every { playlistSongRepository.findByPlaylistIdAndSongId(any(), songId) } returns Optional.empty()
            every { playlistSongRepository.findByPlaylistIdWithSong(any()) } returns emptyList()
            every { playlistSongRepository.save(any()) } returns mockk()
            every { likeRepository.countBySongIdAndRevokedAtIsNull(songId) } returns 1L
            every { dislikeRepository.countBySongIdAndRevokedAtIsNull(songId) } returns 0L
            every { songRepository.save(any()) } returns song

            songLikeService.likeSong(userId, songId)

            verify(exactly = 1) { dislikeRepository.save(any()) }
            verify(exactly = 1) { likeRepository.save(any()) }
        }
    }

    describe("dislikeSong") {
        val userId = UUID.randomUUID()
        val songId = UUID.randomUUID()
        val user = User(
            id = userId,
            email = "test@example.com",
            nickname = "testuser",
            provider = AuthProvider.LOCAL,
            accessLevel = AccessLevel.USER,
            isActive = true,
            createdBy = "system",
            modifiedBy = "system"
        )
        val song = Song(
            id = songId,
            artists = mapOf("default" to listOf("Artist")),
            audioUrls = mapOf("default" to "http://audio.com"),
            coverUrls = mapOf("default" to "http://cover.com"),
            title = "Test Song",
            active = true,
            createdBy = "system",
            modifiedBy = "system"
        )

        it("should dislike a song when not already disliked") {
            every { dislikeRepository.existsByUserIdAndSongIdAndRevokedAtIsNull(userId, songId) } returns false
            every { likeRepository.findByUserIdAndSongIdAndRevokedAtIsNull(userId, songId) } returns Optional.empty()
            every { userRepository.findById(userId) } returns Optional.of(user)
            every { songRepository.findById(songId) } returns Optional.of(song)
            every { dislikeRepository.save(any()) } returns mockk()
            every { playlistRepository.findByUserIdAndName(userId, "DEFAULT_DISLIKES") } returns Optional.empty()
            every { playlistRepository.save(any()) } returns mockk()
            every { playlistSongRepository.findByPlaylistIdAndSongId(any(), songId) } returns Optional.empty()
            every { playlistSongRepository.findByPlaylistIdWithSong(any()) } returns emptyList()
            every { playlistSongRepository.save(any()) } returns mockk()
            every { likeRepository.countBySongIdAndRevokedAtIsNull(songId) } returns 0L
            every { dislikeRepository.countBySongIdAndRevokedAtIsNull(songId) } returns 1L
            every { songRepository.save(any()) } returns song

            songLikeService.dislikeSong(userId, songId)

            verify(exactly = 1) { dislikeRepository.save(any()) }
            verify(exactly = 1) { songRepository.save(any()) }
        }

        it("should not dislike if already disliked") {
            every { dislikeRepository.existsByUserIdAndSongIdAndRevokedAtIsNull(userId, songId) } returns true

            songLikeService.dislikeSong(userId, songId)

            verify(exactly = 0) { dislikeRepository.save(any()) }
        }
    }

    describe("getLikeDislikeInfo") {
        val userId = UUID.randomUUID()
        val songId = UUID.randomUUID()

        it("should return correct like/dislike info") {
            every { likeRepository.existsByUserIdAndSongIdAndRevokedAtIsNull(userId, songId) } returns true
            every { dislikeRepository.existsByUserIdAndSongIdAndRevokedAtIsNull(userId, songId) } returns false
            every { likeRepository.countBySongIdAndRevokedAtIsNull(songId) } returns 10L
            every { dislikeRepository.countBySongIdAndRevokedAtIsNull(songId) } returns 2L

            val result = songLikeService.getLikeDislikeInfo(userId, songId)

            result.isLiked shouldBe true
            result.isDisliked shouldBe false
            result.likesCount shouldBe 10L
            result.dislikesCount shouldBe 2L
        }
    }
})

