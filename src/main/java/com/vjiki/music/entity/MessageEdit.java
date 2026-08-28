package com.vjiki.music.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "message_edits", schema = "music")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageEdit {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @Column(name = "message_id", nullable = false)
    private UUID messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", insertable = false, updatable = false)
    private Message message;

    @Column(name = "old_content", columnDefinition = "TEXT")
    private String oldContent;

    @Column(name = "new_content", columnDefinition = "TEXT")
    private String newContent;

    @Column(name = "edited_by")
    private UUID editedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "edited_by", insertable = false, updatable = false)
    private User editor;

    @CreationTimestamp
    @Column(name = "edited_at", nullable = false, updatable = false)
    private OffsetDateTime editedAt;
}
