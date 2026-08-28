package com.vjiki.music.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "artist", schema = "music")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Artist {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "sort_name", nullable = false)
    private String sortName;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "is_band", nullable = false)
    @Builder.Default
    private Boolean isBand = false;

    @Column(name = "debut_year")
    private Short debutYear;

    @Column(name = "popularity", nullable = false)
    @Builder.Default
    private Integer popularity = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "cover_url", nullable = false, columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, String> coverUrl = defaultCoverUrl();

    private static Map<String, String> defaultCoverUrl() {
        Map<String, String> map = new HashMap<>();
        map.put("default", null);
        return map;
    }
}
