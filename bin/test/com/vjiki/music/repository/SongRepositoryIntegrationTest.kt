package com.vjiki.music.repository

import com.vjiki.music.config.TestContainersConfig
import com.vjiki.music.entity.Song
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration
import java.util.*

@DataJpaTest
@ContextConfiguration(classes = [TestContainersConfig::class])
open class SongRepositoryIntegrationTest : DescribeSpec() {

    override fun extensions() = listOf(SpringExtension)

    @Autowired
    lateinit var songRepository: SongRepository

    init {
        describe("SongRepository") {
        it("should save and find song by id") {
            val song = Song(
                artists = mapOf("default" to listOf("Artist Name")),
                audioUrls = mapOf("default" to "http://audio.com/song.mp3"),
                coverUrls = mapOf("default" to "http://cover.com/cover.jpg"),
                title = "Test Song",
                active = true,
                createdBy = "system",
                modifiedBy = "system"
            )

            val saved = songRepository.save(song)
            val found = songRepository.findById(saved.id)

            found.isPresent shouldBe true
            found.get().title shouldBe "Test Song"
            found.get().active shouldBe true
        }

        it("should find only active songs") {
            val activeSong = Song(
                artists = mapOf("default" to listOf("Active Artist")),
                audioUrls = mapOf("default" to "http://audio.com/active.mp3"),
                coverUrls = mapOf("default" to "http://cover.com/active.jpg"),
                title = "Active Song",
                active = true,
                createdBy = "system",
                modifiedBy = "system"
            )
            val inactiveSong = Song(
                artists = mapOf("default" to listOf("Inactive Artist")),
                audioUrls = mapOf("default" to "http://audio.com/inactive.mp3"),
                coverUrls = mapOf("default" to "http://cover.com/inactive.jpg"),
                title = "Inactive Song",
                active = false,
                createdBy = "system",
                modifiedBy = "system"
            )

            songRepository.save(activeSong)
            songRepository.save(inactiveSong)

            val activeSongs = songRepository.findAllActive()

            activeSongs.size shouldBe 1
            activeSongs[0].title shouldBe "Active Song"
            activeSongs[0].active shouldBe true
        }

        it("should update song") {
            val song = Song(
                artists = mapOf("default" to listOf("Artist")),
                audioUrls = mapOf("default" to "http://audio.com/song.mp3"),
                coverUrls = mapOf("default" to "http://cover.com/cover.jpg"),
                title = "Original Title",
                active = true,
                createdBy = "system",
                modifiedBy = "system"
            )

            val saved = songRepository.save(song)
            saved.likesCount = 10L
            saved.active = false
            val updated = songRepository.save(saved)

            updated.likesCount shouldBe 10L
            updated.active shouldBe false
        }

        it("should delete song") {
            val song = Song(
                artists = mapOf("default" to listOf("Artist")),
                audioUrls = mapOf("default" to "http://audio.com/song.mp3"),
                coverUrls = mapOf("default" to "http://cover.com/cover.jpg"),
                title = "To Delete",
                active = true,
                createdBy = "system",
                modifiedBy = "system"
            )

            val saved = songRepository.save(song)
            songRepository.deleteById(saved.id)

            val found = songRepository.findById(saved.id)
            found.isPresent shouldBe false
        }
    }
    }
}

