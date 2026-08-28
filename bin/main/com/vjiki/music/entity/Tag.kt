package com.vjiki.music.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "tag", schema = "music")
data class Tag(
    @Id
    @Column(name = "id")
    val id: UUID,

    @Column(name = "name", nullable = false)
    val name: String,

    @Column(name = "type", nullable = false)
    val type: String,

    @ManyToOne
    @JoinColumn(name = "parent_id")
    val parent: Tag? = null
)


