package com.vjiki.music.mapper;

import com.vjiki.music.dto.PlaylistSongResponse;
import com.vjiki.music.entity.PlaylistSong;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class PlaylistSongMapperImpl implements PlaylistSongMapper {

    @Override
    public PlaylistSongResponse toResponse(PlaylistSong playlistSong) {
        return new PlaylistSongResponse(
                playlistSong.getId(),
                Optional.ofNullable(playlistSong.getPlaylist())
                        .map(playlist -> playlist.getId())
                        .orElse(null),
                Optional.ofNullable(playlistSong.getSong())
                        .map(song -> song.getId())
                        .orElse(null),
                Optional.ofNullable(playlistSong.getSong())
                        .map(song -> song.getTitle())
                        .orElse(null),
                extractArtist(playlistSong.getSong()),
                extractUrl(playlistSong.getSong(), true),
                extractUrl(playlistSong.getSong(), false),
                playlistSong.getPosition(),
                playlistSong.getAddedAt(),
                Optional.ofNullable(playlistSong.getAddedBy())
                        .map(user -> user.getId())
                        .orElse(null)
        );
    }

    private String extractArtist(com.vjiki.music.entity.Song song) {
        if (song == null) {
            return null;
        }
        return Optional.ofNullable(song.getArtists())
                .map(map -> map.get("default"))
                .filter(list -> !list.isEmpty())
                .map(List::getFirst)
                .orElse(null);
    }

    private String extractUrl(com.vjiki.music.entity.Song song, boolean isAudio) {
        if (song == null) {
            return null;
        }
        if (isAudio) {
            return Optional.ofNullable(song.getAudioUrls())
                    .map(map -> map.get("default"))
                    .orElse(null);
        } else {
            return Optional.ofNullable(song.getCoverUrls())
                    .map(map -> map.get("default"))
                    .orElse(null);
        }
    }
}

