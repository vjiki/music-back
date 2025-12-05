package com.vjiki.music.service

import com.vjiki.music.dto.SongResponse
import com.vjiki.music.mapper.SongMapper.toResponse
import com.vjiki.music.repository.SongRepository
import org.springframework.stereotype.Service

@Service
class SongServiceImpl(
    private val songRepository: SongRepository
) : SongService {

    override fun getSongs(userId: String): List<SongResponse> {
        return songRepository.findAll().map { it.toResponse() }
    }
}

