package com.vjiki.music.service

import com.vjiki.music.dto.FollowerResponse
import java.util.UUID

interface FollowerService {
    fun getFollowersByUserId(userId: UUID): List<FollowerResponse>
}

