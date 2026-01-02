package com.vjiki.music.repository

import com.vjiki.music.entity.Tag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface TagRepository : JpaRepository<Tag, UUID> {

    @Query(
        value = """
            SELECT *
            FROM music.tag
            WHERE name = :name
              AND type = :type
            LIMIT 1
        """,
        nativeQuery = true
    )
    fun findOneByNameAndType(@Param("name") name: String, @Param("type") type: String): Tag?
}


