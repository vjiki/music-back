package com.vjiki.music.service;

import com.vjiki.music.dto.SongResponse;
import com.vjiki.music.mapper.SongMapper;
import com.vjiki.music.repository.SongRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;
    private final SongMapper songMapper;

    public SongServiceImpl(SongRepository songRepository, SongMapper songMapper) {
        this.songRepository = songRepository;
        this.songMapper = songMapper;
    }

    @Override
    public List<SongResponse> getSongs(String userId) {
        return songRepository.findAll().stream()
                .map(songMapper::toResponse)
                .toList();
    }
}

