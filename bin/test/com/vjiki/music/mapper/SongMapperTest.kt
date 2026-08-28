package com.vjiki.music.mapper

import com.vjiki.music.entity.Song
import com.vjiki.music.mapper.SongMapper.toResponse
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.util.*

class SongMapperTest : DescribeSpec({

    describe("Song.toResponse()") {
        it("should map song to response correctly") {
            val song = Song(
                id = UUID.randomUUID(),
                artists = mapOf("default" to listOf("Artist Name")),
                audioUrls = mapOf("default" to "http://audio.com/song.mp3"),
                coverUrls = mapOf("default" to "http://cover.com/cover.jpg"),
                title = "Test Song",
                active = true,
                createdBy = "system",
                modifiedBy = "system"
            )

            val response = song.toResponse()

            response.id shouldBe song.id.toString()
            response.title shouldBe "Test Song"
            response.artist shouldBe "Artist Name"
            response.audioUrl shouldBe "http://audio.com/song.mp3"
            response.cover shouldBe "http://cover.com/cover.jpg"
        }

        it("should handle null artists") {
            val song = Song(
                id = UUID.randomUUID(),
                artists = emptyMap(),
                audioUrls = mapOf("default" to "http://audio.com/song.mp3"),
                coverUrls = mapOf("default" to "http://cover.com/cover.jpg"),
                title = "Test Song",
                active = true,
                createdBy = "system",
                modifiedBy = "system"
            )

            val response = song.toResponse()

            response.artist shouldBe null
        }

        it("should handle missing default in artists") {
            val song = Song(
                id = UUID.randomUUID(),
                artists = mapOf("other" to listOf("Other Artist")),
                audioUrls = mapOf("default" to "http://audio.com/song.mp3"),
                coverUrls = mapOf("default" to "http://cover.com/cover.jpg"),
                title = "Test Song",
                active = true,
                createdBy = "system",
                modifiedBy = "system"
            )

            val response = song.toResponse()

            response.artist shouldBe null
        }

        it("should handle empty artist list") {
            val song = Song(
                id = UUID.randomUUID(),
                artists = mapOf("default" to emptyList()),
                audioUrls = mapOf("default" to "http://audio.com/song.mp3"),
                coverUrls = mapOf("default" to "http://cover.com/cover.jpg"),
                title = "Test Song",
                active = true,
                createdBy = "system",
                modifiedBy = "system"
            )

            val response = song.toResponse()

            response.artist shouldBe null
        }
    }
})

