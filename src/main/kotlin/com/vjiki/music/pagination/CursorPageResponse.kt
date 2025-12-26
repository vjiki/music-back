package com.vjiki.music.pagination

data class CursorPageResponse<T>(
    val items: List<T>,
    val nextCursor: String? = null,
    val hasNext: Boolean = false
)


