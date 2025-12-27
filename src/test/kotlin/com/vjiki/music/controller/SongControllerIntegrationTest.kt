package com.vjiki.music.controller

import com.vjiki.music.config.TestContainersConfig
import com.vjiki.music.entity.Song
import com.vjiki.music.repository.SongRepository
import com.vjiki.music.service.SongService
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = [TestContainersConfig::class])
@Transactional
open class SongControllerIntegrationTest : DescribeSpec() {

    override fun extensions() = listOf(SpringExtension)

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var songRepository: SongRepository

    @Autowired
    lateinit var songService: SongService

    init {
        describe("GET /api/v1/songs/{userId}") {
        it("should return list of active songs") {
                val song1 = songRepository.save(
                    Song(
                artists = mapOf("default" to listOf("Artist 1")),
                audioUrls = mapOf("default" to "http://audio.com/song1.mp3"),
                coverUrls = mapOf("default" to "http://cover.com/cover1.jpg"),
                title = "Song 1",
                active = true,
                createdBy = "system",
                modifiedBy = "system"
                    )
                )
                songRepository.save(
                    Song(
                artists = mapOf("default" to listOf("Artist 2")),
                audioUrls = mapOf("default" to "http://audio.com/song2.mp3"),
                coverUrls = mapOf("default" to "http://cover.com/cover2.jpg"),
                title = "Song 2",
                active = true,
                createdBy = "system",
                modifiedBy = "system"
                    )
                )
                songRepository.save(
                    Song(
                artists = mapOf("default" to listOf("Artist 3")),
                audioUrls = mapOf("default" to "http://audio.com/song3.mp3"),
                coverUrls = mapOf("default" to "http://cover.com/cover3.jpg"),
                title = "Song 3",
                active = false,
                createdBy = "system",
                modifiedBy = "system"
                    )
                )

            val userId = java.util.UUID.randomUUID()

            mockMvc.perform(get("/api/v1/songs/{userId}", userId))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$").isArray)
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").exists())
                .andExpect(jsonPath("$[1].title").exists())
        }

        it("should return empty list when no active songs exist") {
            val userId = java.util.UUID.randomUUID()

            mockMvc.perform(get("/api/v1/songs/{userId}", userId))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$").isArray)
                .andExpect(jsonPath("$.length()").value(0))
        }
    }

        describe("GET /api/v1/songs/{userId}/page") {
            it("should return cursor-paginated songs and exclude non-SONG types") {
                // 3 active SONG + 1 active SHORT + 1 inactive SONG
                repeat(3) { idx ->
                    songRepository.save(
                        Song(
                            artists = mapOf("default" to listOf("SongArtist ${idx + 1}")),
                            audioUrls = mapOf("default" to "http://audio.com/s${idx + 1}.mp3"),
                            coverUrls = mapOf("default" to "http://cover.com/s${idx + 1}.jpg"),
                            title = "Song ${idx + 1}",
                            active = true,
                            createdBy = "system",
                            modifiedBy = "system",
                            type = "SONG"
                        )
                    )
                }

                songRepository.save(
                    Song(
                        artists = mapOf("default" to listOf("ShortArtist")),
                        audioUrls = mapOf("default" to "http://audio.com/short.mp3"),
                        coverUrls = mapOf("default" to "http://cover.com/short.jpg"),
                        title = "Short 1",
                        active = true,
                        createdBy = "system",
                        modifiedBy = "system",
                        type = "SHORT"
                    )
                )

                songRepository.save(
                    Song(
                        artists = mapOf("default" to listOf("InactiveArtist")),
                        audioUrls = mapOf("default" to "http://audio.com/inactive.mp3"),
                        coverUrls = mapOf("default" to "http://cover.com/inactive.jpg"),
                        title = "Inactive Song",
                        active = false,
                        createdBy = "system",
                        modifiedBy = "system",
                        type = "SONG"
                    )
                )

                val userId = java.util.UUID.randomUUID()

                // First page
                val first = mockMvc.perform(get("/api/v1/songs/{userId}/page", userId).param("limit", "2"))
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.items").isArray)
                    .andExpect(jsonPath("$.items.length()").value(2))
                    .andExpect(jsonPath("$.hasNext").value(true))
                    .andExpect(jsonPath("$.nextCursor").isString)
                    .andReturn()

                val firstJson = first.response.contentAsString
                val cursorRegex = """"nextCursor"\s*:\s*"([^"]+)"""".toRegex()
                val cursor = cursorRegex.find(firstJson)?.groupValues?.get(1)
                cursor shouldNotBe null
                cursor!!.isNotBlank() shouldBe true

                // Second page
                mockMvc.perform(
                    get("/api/v1/songs/{userId}/page", userId)
                        .param("limit", "2")
                        .param("cursor", cursor)
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.items").isArray)
                    .andExpect(jsonPath("$.items.length()").value(1))
                    .andExpect(jsonPath("$.hasNext").value(false))
                    .andExpect(jsonPath("$.nextCursor").doesNotExist())
            }
        }
    }
}

