package com.vjiki.music.repository

import com.vjiki.music.entity.UserFollow
import com.vjiki.music.entity.UserFollowId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserFollowRepository : JpaRepository<UserFollow, UserFollowId> {

    @Query("SELECT uf FROM UserFollow uf JOIN FETCH uf.follower WHERE uf.followedId = :userId")
    fun findByFollowedId(@Param("userId") userId: UUID): List<UserFollow>

    @Query("SELECT uf FROM UserFollow uf JOIN FETCH uf.followed WHERE uf.followerId = :userId")
    fun findByFollowerId(@Param("userId") userId: UUID): List<UserFollow>
}

