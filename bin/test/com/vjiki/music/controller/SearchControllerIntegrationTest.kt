package com.vjiki.music.controller

import com.vjiki.music.config.TestContainersConfig
import com.vjiki.music.entity.Song
import com.vjiki.music.entity.Tag
import com.vjiki.music.entity.TrackTag
import com.vjiki.music.repository.SongRepository
import com.vjiki.music.repository.TagRepository
import com.vjiki.music.repository.TrackTagRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = [TestContainersConfig::class])
@Transactional
open class SearchControllerIntegrationTest : DescribeSpec() {

    override fun extensions() = listOf(SpringExtension)

    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var songRepository: SongRepository
    @Autowired lateinit var tagRepository: TagRepository
    @Autowired lateinit var trackTagRepository: TrackTagRepository

    init {
        describe("GET /api/v1/search/songs/{userId}") {
            it("should find by title and by tag name") {
                val song1 = songRepository.save(
                    Song(
                        artists = mapOf("default" to listOf("Some Artist")),
                        audioUrls = mapOf("default" to "http://audio.com/a.mp3"),
                        coverUrls = mapOf("default" to "http://cover.com/a.jpg"),
                        title = "Very Chill Track",
                        active = true,
                        createdBy = "system",
                        modifiedBy = "system",
                        type = "SONG"
                    )
                )

                val song2 = songRepository.save(
                    Song(
                        artists = mapOf("default" to listOf("Other Artist")),
                        audioUrls = mapOf("default" to "http://audio.com/b.mp3"),
                        coverUrls = mapOf("default" to "http://cover.com/b.jpg"),
                        title = "Random",
                        active = true,
                        createdBy = "system",
                        modifiedBy = "system",
                        type = "SONG"
                    )
                )

                val tag = tagRepository.save(Tag(id = UUID.randomUUID(), name = "Electronic", type = "GENRE"))
                trackTagRepository.save(
                    TrackTag(
                        trackId = song2.id,
                        tagId = tag.id,
                        weight = 0.8,
                        source = "MANUAL"
                    )
                )

                val userId = UUID.randomUUID()

                // title search
                mockMvc.perform(
                    get("/api/v1/search/songs/{userId}", userId)
                        .param("q", "chill")
                        .with(user("test").roles("USER"))
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.items").isArray)
                    .andExpect(jsonPath("$.items.length()").value(1))
                    .andExpect(jsonPath("$.items[0].id").value(song1.id.toString()))

                // tag search
                mockMvc.perform(
                    get("/api/v1/search/songs/{userId}", userId)
                        .param("q", "elect")
                        .with(user("test").roles("USER"))
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.items").isArray)
                    .andExpect(jsonPath("$.items.length()").value(1))
                    .andExpect(jsonPath("$.items[0].id").value(song2.id.toString()))
                    .andExpect(jsonPath("$.items[0].tags").isArray)
            }
        }
    }
}


