package com.vjiki.music.mapper;

import com.vjiki.music.dto.SongResponse;
import com.vjiki.music.entity.Song;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class SongMapperImpl implements SongMapper {

    @Override
    public SongResponse toResponse(Song song) {
        return new SongResponse(
                Optional.ofNullable(song.getId())
                        .map(UUID::toString)
                        .orElse(null),
                extractArtist(song.getArtists()),
                extractUrl(song.getAudioUrls()),
                extractUrl(song.getCoverUrls()),
                song.getTitle()
        );
    }

    private String extractArtist(Map<String, List<String>> artists) {
        return Optional.ofNullable(artists)
                .map(map -> map.get("default"))
                .filter(list -> !list.isEmpty())
                .map(list -> list.getFirst())
                .orElse(null);
    }

    private String extractUrl(Map<String, String> urls) {
        return Optional.ofNullable(urls)
                .map(map -> map.get("default"))
                .orElse(null);
    }
}

