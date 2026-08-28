package com.vjiki.music.mapper;

import com.vjiki.music.dto.PlaylistSongResponse;
import com.vjiki.music.entity.PlaylistSong;

public final class PlaylistSongMapper {

    private PlaylistSongMapper() {
    }

    public static PlaylistSongResponse toResponse(PlaylistSong playlistSong) {
        return PlaylistSongResponse.builder()
                .id(playlistSong.getId())
                .playlistId(playlistSong.getPlaylist() == null ? null : playlistSong.getPlaylist().getId())
                .songId(playlistSong.getSong() == null ? null : playlistSong.getSong().getId())
                .songTitle(playlistSong.getSong() == null ? null : playlistSong.getSong().getTitle())
                .songArtist(playlistSong.getSong() == null
                        ? null
                        : SongMapper.extractArtist(playlistSong.getSong().getArtists()))
                .songAudioUrl(playlistSong.getSong() == null
                        ? null
                        : SongMapper.extractUrl(playlistSong.getSong().getAudioUrls()))
                .songCoverUrl(playlistSong.getSong() == null
                        ? null
                        : SongMapper.extractUrl(playlistSong.getSong().getCoverUrls()))
                .position(playlistSong.getPosition())
                .addedAt(playlistSong.getAddedAt())
                .addedBy(playlistSong.getAddedBy() == null ? null : playlistSong.getAddedBy().getId())
                .build();
    }
}
