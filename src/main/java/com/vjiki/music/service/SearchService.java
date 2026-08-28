package com.vjiki.music.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
public class SearchService {

    private final SongRepository songRepository;
    private final LikeRepository likeRepository;
    private final DislikeRepository dislikeRepository;
    private final TagLookupService tagLookupService;

    public SearchService(SongRepository songRepository,
                         LikeRepository likeRepository,
                         DislikeRepository dislikeRepository,
                         TagLookupService tagLookupService) {
        this.songRepository = songRepository;
        this.likeRepository = likeRepository;
        this.dislikeRepository = dislikeRepository;
        this.tagLookupService = tagLookupService;
    }

    public CursorPageResponse<SongResponse> searchSongs(UUID userId, String q, int limit, String cursor) {
        String query = q == null ? "" : q.trim();
        if (query.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "q is required");
        }

        int safeLimit = Math.min(Math.max(limit, 1), 100);
        Pageable pageable = PageRequest.of(0, safeLimit + 1);

        CreatedAtIdCursorCodec.Cursor decoded = CreatedAtIdCursorCodec.decodeOrBadRequest(cursor);
        List<Song> songs = decoded == null
                ? songRepository.searchSongsFirst(query, pageable)
                : songRepository.searchSongsAfter(query, decoded.createdAt(), decoded.id(), pageable);

        boolean hasNext = songs.size() > safeLimit;
        List<Song> slice = hasNext ? songs.subList(0, safeLimit) : songs;

        List<UUID> songIds = slice.stream().map(Song::getId).toList();
        Map<UUID, List<TagResponse>> tagsBySongId = tagLookupService.getTagsByTrackIds(songIds);
        SongEnrichment enrichment = SongEnrichment.load(userId, songIds, likeRepository, dislikeRepository);

        List<SongResponse> items = new ArrayList<>(slice.size());
        for (Song song : slice) {
            SongResponse response = SongMapper.toResponse(
                    song,
                    enrichment.isLiked(song),
                    enrichment.isDisliked(song),
                    enrichment.likesCount(song),
                    enrichment.dislikesCount(song));
            response.setTags(tagsBySongId.getOrDefault(song.getId(), List.of()));
            items.add(response);
        }

        Song last = slice.isEmpty() ? null : slice.getLast();
        String nextCursor = (hasNext && last != null && last.getCreatedAt() != null)
                ? CreatedAtIdCursorCodec.encode(last.getCreatedAt(), last.getId())
                : null;

        return new CursorPageResponse<>(items, nextCursor, hasNext);
    }
}
