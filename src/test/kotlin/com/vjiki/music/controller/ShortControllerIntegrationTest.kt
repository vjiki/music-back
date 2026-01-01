package com.vjiki.music.controller

import com.vjiki.music.config.TestContainersConfig
import com.vjiki.music.entity.Song
import com.vjiki.music.repository.SongRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = [TestContainersConfig::class])
@Transactional
open class ShortControllerIntegrationTest : DescribeSpec() {

    override fun extensions() = listOf(SpringExtension)

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var songRepository: SongRepository

    init {
        describe("GET /api/v1/shorts/{userId}/page") {
            it("should return cursor-paginated active items (songs + shorts), with type field present") {
                // 2 active SONG + 2 active SHORT + 1 inactive
                repeat(2) { idx ->
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

                repeat(2) { idx ->
                    songRepository.save(
                        Song(
                            artists = mapOf("default" to listOf("ShortArtist ${idx + 1}")),
                            audioUrls = mapOf("default" to "http://audio.com/sh${idx + 1}.mp3"),
                            coverUrls = mapOf("default" to "http://cover.com/sh${idx + 1}.jpg"),
                            title = "Short ${idx + 1}",
                            active = true,
                            createdBy = "system",
                            modifiedBy = "system",
                            type = "SHORT"
                        )
                    )
                }

                songRepository.save(
                    Song(
                        artists = mapOf("default" to listOf("InactiveArtist")),
                        audioUrls = mapOf("default" to "http://audio.com/inactive.mp3"),
                        coverUrls = mapOf("default" to "http://cover.com/inactive.jpg"),
                        title = "Inactive",
                        active = false,
                        createdBy = "system",
                        modifiedBy = "system",
                        type = "SHORT"
                    )
                )

                val userId = java.util.UUID.randomUUID()

                val first = mockMvc.perform(
                    get("/api/v1/shorts/{userId}/page", userId)
                        .param("limit", "3")
                        .with(user("test").roles("USER"))
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.items").isArray)
                    .andExpect(jsonPath("$.items.length()").value(3))
                    .andExpect(jsonPath("$.items[0].type").exists())
                    .andExpect(jsonPath("$.hasNext").value(true))
                    .andExpect(jsonPath("$.nextCursor").isString)
                    .andReturn()

                val firstJson = first.response.contentAsString
                val cursorRegex = """"nextCursor"\s*:\s*"([^"]+)"""".toRegex()
                val cursor = cursorRegex.find(firstJson)?.groupValues?.get(1)
                cursor shouldNotBe null
                cursor!!.isNotBlank() shouldBe true

                mockMvc.perform(
                    get("/api/v1/shorts/{userId}/page", userId)
                        .param("limit", "3")
                        .param("cursor", cursor!!)
                        .with(user("test").roles("USER"))
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


