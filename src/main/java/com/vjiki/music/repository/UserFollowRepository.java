package com.vjiki.music.repository;

import com.vjiki.music.entity.UserFollow;
import com.vjiki.music.entity.UserFollowId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserFollowRepository extends JpaRepository<UserFollow, UserFollowId> {
    
    @Query("SELECT uf FROM UserFollow uf JOIN FETCH uf.follower WHERE uf.followedId = :userId")
    List<UserFollow> findByFollowedId(@Param("userId") UUID userId);
    
    @Query("SELECT uf FROM UserFollow uf JOIN FETCH uf.followed WHERE uf.followerId = :userId")
    List<UserFollow> findByFollowerId(@Param("userId") UUID userId);
}

