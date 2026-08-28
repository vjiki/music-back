package com.vjiki.music.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.vjiki.music.dto.ShortResponse;
import com.vjiki.music.dto.TagResponse;
import com.vjiki.music.entity.Song;
import com.vjiki.music.mapper.ShortMapper;
import com.vjiki.music.pagination.CreatedAtIdCursorCodec;
import com.vjiki.music.pagination.CursorPageResponse;
import com.vjiki.music.repository.DislikeRepository;
import com.vjiki.music.repository.LikeRepository;
import com.vjiki.music.repository.SongRepository;

@Service
public class ShortServiceImpl implements ShortService {

    private final SongRepository songRepository;
    private final LikeRepository likeRepository;
    private final DislikeRepository dislikeRepository;
    private final TagLookupService tagLookupService;

    public ShortServiceImpl(SongRepository songRepository,
                            LikeRepository likeRepository,
                            DislikeRepository dislikeRepository,
                            TagLookupService tagLookupService) {
        this.songRepository = songRepository;
        this.likeRepository = likeRepository;
        this.dislikeRepository = dislikeRepository;
        this.tagLookupService = tagLookupService;
    }

    @Override
    public List<ShortResponse> getShorts(UUID userId) {
        List<Song> songs = songRepository.findAllShorts();
        if (songs.isEmpty()) {
            return List.of();
        }
        return mapShorts(userId, songs);
    }

    @Override
    public CursorPageResponse<ShortResponse> getShortsPage(UUID userId, int limit, String cursor) {
        int safeLimit = Math.min(Math.max(limit, 1), 100);
        Pageable pageable = PageRequest.of(0, safeLimit + 1);

        CreatedAtIdCursorCodec.Cursor decoded = CreatedAtIdCursorCodec.decodeOrBadRequest(cursor);
        List<Song> songs = decoded == null
                ? songRepository.findActiveItemsPageFirst(pageable)
                : songRepository.findActiveItemsPageAfter(decoded.createdAt(), decoded.id(), pageable);

        boolean hasNext = songs.size() > safeLimit;
        List<Song> slice = hasNext ? songs.subList(0, safeLimit) : songs;

        List<ShortResponse> items = mapShorts(userId, slice);

        Song last = slice.isEmpty() ? null : slice.getLast();
        String nextCursor = (hasNext && last != null && last.getCreatedAt() != null)
                ? CreatedAtIdCursorCodec.encode(last.getCreatedAt(), last.getId())
                : null;

        return new CursorPageResponse<>(items, nextCursor, hasNext);
    }

    private List<ShortResponse> mapShorts(UUID userId, List<Song> songs) {
        List<UUID> songIds = songs.stream().map(Song::getId).toList();
        Map<UUID, List<TagResponse>> tagsBySongId = tagLookupService.getTagsByTrackIds(songIds);
        SongEnrichment enrichment = SongEnrichment.load(userId, songIds, likeRepository, dislikeRepository);

        List<ShortResponse> items = new ArrayList<>(songs.size());
        for (Song song : songs) {
            ShortResponse response = ShortMapper.toShortResponse(
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
