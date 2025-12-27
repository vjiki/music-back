package com.vjiki.music.repository

import com.vjiki.music.entity.UserRole
import com.vjiki.music.entity.UserRoleId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRoleRepository : JpaRepository<UserRole, UserRoleId> {

    @Modifying
    @Query(
        value = "INSERT INTO music.user_roles (user_id, role) VALUES (:userId, :role) ON CONFLICT DO NOTHING",
        nativeQuery = true
    )
    fun insertRoleIfMissing(
        @Param("userId") userId: UUID,
        @Param("role") role: String
    )
}


