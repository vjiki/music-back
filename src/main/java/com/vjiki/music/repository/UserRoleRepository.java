package com.vjiki.music.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vjiki.music.entity.UserRole;
import com.vjiki.music.entity.UserRoleId;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {

    @Modifying
    @Query(value = "INSERT INTO music.user_roles (user_id, role) VALUES (:userId, :role) ON CONFLICT DO NOTHING",
            nativeQuery = true)
    void insertRoleIfMissing(
            @Param("userId") UUID userId,
            @Param("role") String role);

    @Query(value = "SELECT role FROM music.user_roles WHERE user_id = :userId", nativeQuery = true)
    List<String> findRolesByUserId(@Param("userId") UUID userId);
}
