package com.vjiki.music.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.domain.Persistable
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(name = "users", schema = "music")
data class User(
    @Id
    @GeneratedValue
    @Column(name = "id")
    val id: UUID = UUID.randomUUID(),

    @Column(name = "email", nullable = false, unique = true)
    val email: String,

    @Column(name = "password_hash")
    var passwordHash: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    val provider: AuthProvider = AuthProvider.LOCAL,

    @Column(name = "provider_id")
    val providerId: String? = null,

    @Column(name = "nickname", nullable = false)
    val nickname: String,

    @Column(name = "avatar_url")
    var avatarUrl: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "access_level", nullable = false)
    val accessLevel: AccessLevel = AccessLevel.USER,

    @Column(name = "is_active")
    var isActive: Boolean = true,

    @Column(name = "is_verified")
    var isVerified: Boolean = false,

    @Column(name = "last_login_at")
    var lastLoginAt: OffsetDateTime? = null,

    @Column(name = "last_login_ip")
    var lastLoginIp: String? = null,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: OffsetDateTime? = null,

    @Column(name = "created_by", nullable = false, updatable = false)
    val createdBy: String,

    @UpdateTimestamp
    @Column(name = "modified_at", nullable = false)
    var modifiedAt: OffsetDateTime? = null,

    @Column(name = "modified_by", nullable = false)
    var modifiedBy: String,

    @Version
    @Column(name = "version", nullable = false)
    var version: Int = 0
)

