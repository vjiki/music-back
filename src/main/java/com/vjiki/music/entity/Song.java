package com.vjiki.music.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "songs", schema = "music")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Song {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "artists", nullable = false, columnDefinition = "jsonb")
    private Map<String, List<String>> artists;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "audio_urls", nullable = false, columnDefinition = "jsonb")
    private Map<String, String> audioUrls;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "cover_urls", nullable = false, columnDefinition = "jsonb")
    private Map<String, String> coverUrls;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "is_favourite")
    private Boolean isFavourite;

    @Column(name = "is_disliked")
    private Boolean isDisliked;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;

    @UpdateTimestamp
    @Column(name = "modified_at", nullable = false)
    private OffsetDateTime modifiedAt;

    @Column(name = "modified_by", nullable = false)
    private String modifiedBy;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;
}

