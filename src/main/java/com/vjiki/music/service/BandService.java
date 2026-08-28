package com.vjiki.music.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.vjiki.music.dto.BandResponse;
import com.vjiki.music.dto.SongResponse;
import com.vjiki.music.entity.Artist;
import com.vjiki.music.pagination.CursorPageResponse;
import com.vjiki.music.pagination.LocalDateTimeIdCursorCodec;
import com.vjiki.music.repository.ArtistRepository;

@Service
public class BandService {

    /** Upper bound on search pages walked per band, mirroring the Kotlin implementation. */
    private static final int MAX_SEARCH_PAGES = 50;

    private final ArtistRepository artistRepository;
    private final SearchService searchService;

    public BandService(ArtistRepository artistRepository, SearchService searchService) {
        this.artistRepository = artistRepository;
        this.searchService = searchService;
    }

    public List<BandResponse> getBands(int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return artistRepository.findBands(PageRequest.of(0, safeLimit)).stream()
                .map(band -> toBandResponse(band, List.of()))
                .toList();
    }

    public CursorPageResponse<BandResponse> getBandsPage(UUID userId, String name, int limit, String cursor) {
        String q = name == null ? "" : name.trim();
        if (q.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is required");
        }

        int safeLimit = Math.min(Math.max(limit, 1), 200);
        Pageable pageable = PageRequest.of(0, safeLimit + 1);

        LocalDateTimeIdCursorCodec.Cursor decoded = LocalDateTimeIdCursorCodec.decodeOrBadRequest(cursor);
        List<Artist> bands = decoded == null
                ? artistRepository.findBandsPageFirst(q, pageable)
                : artistRepository.findBandsPageAfter(q, decoded.createdAt(), decoded.id(), pageable);

        boolean hasNext = bands.size() > safeLimit;
        List<Artist> slice = hasNext ? bands.subList(0, safeLimit) : bands;

        List<BandResponse> items = new ArrayList<>(slice.size());
        for (Artist band : slice) {
            items.add(toBandResponse(band, fetchAllSongsForBand(userId, band.getName())));
        }

        Artist last = slice.isEmpty() ? null : slice.getLast();
        String nextCursor = (hasNext && last != null && last.getCreatedAt() != null)
                ? LocalDateTimeIdCursorCodec.encode(last.getCreatedAt(), last.getId())
                : null;

        return new CursorPageResponse<>(items, nextCursor, hasNext);
    }

    public BandResponse getBand(UUID id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Band not found: " + id));
        if (artist.getIsBand() == null || !artist.getIsBand()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Band not found: " + id);
        }
        return toBandResponse(artist, List.of());
    }

    private List<SongResponse> fetchAllSongsForBand(UUID userId, String bandName) {
        String q = bandName == null ? "" : bandName.trim();
        if (q.isBlank()) {
            return List.of();
        }

        List<SongResponse> out = new ArrayList<>();
        String cursor = null;
        int guard = 0;
        do {
            CursorPageResponse<SongResponse> page = searchService.searchSongs(userId, q, 100, cursor);
            out.addAll(page.items());
            cursor = page.nextCursor();
            guard++;
            if (guard > MAX_SEARCH_PAGES) {
                break;
            }
        } while (cursor != null);
        return out;
    }

    private static BandResponse toBandResponse(Artist artist, List<SongResponse> songs) {
        return BandResponse.builder()
                .id(artist.getId())
                .name(artist.getName())
                .sortName(artist.getSortName())
                .countryCode(artist.getCountryCode())
                .isBand(artist.getIsBand())
                .debutYear(artist.getDebutYear())
                .popularity(artist.getPopularity())
                .createdAt(artist.getCreatedAt())
                .updatedAt(artist.getUpdatedAt())
                .coverUrl(artist.getCoverUrl() == null ? null : artist.getCoverUrl().get("default"))
                .songs(songs)
                .build();
    }
}
