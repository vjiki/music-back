package com.vjiki.music.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.vjiki.music.dto.SongResponse;
import com.vjiki.music.dto.TagResponse;
import com.vjiki.music.entity.Song;
import com.vjiki.music.mapper.SongMapper;
import com.vjiki.music.pagination.CreatedAtIdCursorCodec;
import com.vjiki.music.pagination.CursorPageResponse;
import com.vjiki.music.repository.DislikeRepository;
import com.vjiki.music.repository.LikeRepository;
import com.vjiki.music.repository.SongRepository;

@Service
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;
    private final LikeRepository likeRepository;
    private final DislikeRepository dislikeRepository;
    private final TagLookupService tagLookupService;

    public SongServiceImpl(SongRepository songRepository,
                           LikeRepository likeRepository,
                           DislikeRepository dislikeRepository,
                           TagLookupService tagLookupService) {
        this.songRepository = songRepository;
        this.likeRepository = likeRepository;
        this.dislikeRepository = dislikeRepository;
        this.tagLookupService = tagLookupService;
    }

    @Override
    public List<SongResponse> getSongs(UUID userId) {
        List<Song> songs = songRepository.findAllActive();
        if (songs.isEmpty()) {
            return List.of();
        }
        return mapSongs(userId, songs);
    }

    @Override
    public CursorPageResponse<SongResponse> getSongsPage(UUID userId, int limit, String cursor) {
        int safeLimit = Math.min(Math.max(limit, 1), 100);
        Pageable pageable = PageRequest.of(0, safeLimit + 1);

        CreatedAtIdCursorCodec.Cursor decoded = CreatedAtIdCursorCodec.decodeOrBadRequest(cursor);
        List<Song> songs = decoded == null
                ? songRepository.findActiveSongsPageFirst(pageable)
                : songRepository.findActiveSongsPageAfter(decoded.createdAt(), decoded.id(), pageable);

        boolean hasNext = songs.size() > safeLimit;
        List<Song> slice = hasNext ? songs.subList(0, safeLimit) : songs;

        List<SongResponse> items = mapSongs(userId, slice);

        Song last = slice.isEmpty() ? null : slice.getLast();
        String nextCursor = (hasNext && last != null && last.getCreatedAt() != null)
                ? CreatedAtIdCursorCodec.encode(last.getCreatedAt(), last.getId())
                : null;

        return new CursorPageResponse<>(items, nextCursor, hasNext);
    }

    private List<SongResponse> mapSongs(UUID userId, List<Song> songs) {
        List<UUID> songIds = songs.stream().map(Song::getId).toList();
        Map<UUID, List<TagResponse>> tagsBySongId = tagLookupService.getTagsByTrackIds(songIds);
        SongEnrichment enrichment = SongEnrichment.load(userId, songIds, likeRepository, dislikeRepository);

        List<SongResponse> items = new ArrayList<>(songs.size());
        for (Song song : songs) {
            SongResponse response = SongMapper.toResponse(
                    song,
                    enrichment.isLiked(song),
                    enrichment.isDisliked(song),
                    enrichment.likesCount(song),
                    enrichment.dislikesCount(song));
            response.setTags(tagsBySongId.getOrDefault(song.getId(), List.of()));
            items.add(response);
        }
        return items;
    }
}
