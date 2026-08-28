package com.vjiki.music.pagination;

import java.util.List;

public record CursorPageResponse<T>(List<T> items, String nextCursor, boolean hasNext) {

    public static <T> CursorPageResponse<T> of(List<T> items, String nextCursor, boolean hasNext) {
        return new CursorPageResponse<>(items, nextCursor, hasNext);
    }

    public static <T> CursorPageResponse<T> empty() {
        return new CursorPageResponse<>(List.of(), null, false);
    }
}
