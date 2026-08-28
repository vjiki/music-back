package com.vjiki.music.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "user_roles", schema = "music")
@IdClass(UserRoleId::class)
data class UserRole(
    @Id
    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @Id
    @Column(name = "role", nullable = false)
    val role: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    val user: User? = null
)


