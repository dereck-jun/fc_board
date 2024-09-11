package com.fc_board.model.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.ZonedDateTime;

@Entity
@Getter @Setter
@SQLDelete(sql = "update post set deleted_date_time = current_timestamp where post_id = ?")
@SQLRestriction("deleted_date_time is null")
@Table(name = "post")
@EqualsAndHashCode
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Column(nullable = false, updatable = false)
    private ZonedDateTime createdDateTime;

    @Column
    private ZonedDateTime updatedDateTime;

    @Column(nullable = true)
    private ZonedDateTime deletedDateTime;

    @PrePersist
    private void prePersist() {
        this.createdDateTime = ZonedDateTime.now();
        this.updatedDateTime = this.createdDateTime;
    }

    @PreUpdate
    private void preUpdate() {
        this.updatedDateTime = ZonedDateTime.now();
    }
}
