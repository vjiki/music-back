package com.vjiki.music.service

import com.vjiki.music.dto.FollowerResponse
import com.vjiki.music.mapper.FollowerMapper.toResponse
import com.vjiki.music.repository.UserFollowRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class FollowerServiceImpl(
    private val userFollowRepository: UserFollowRepository
) : FollowerService {

    override fun getFollowersByUserId(userId: UUID): List<FollowerResponse> {
        return userFollowRepository.findByFollowedId(userId).map { it.toResponse() }
    }
}

