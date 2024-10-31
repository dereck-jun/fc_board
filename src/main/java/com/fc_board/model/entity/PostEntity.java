package com.fc_board.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.ZonedDateTime;

@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@SQLDelete(sql = "update post set deleted_date_time = current_timestamp where post_id = ?")
@SQLRestriction("deleted_date_time is null")
@Table(name = "post", indexes = {
        @Index(name = "post_user_id_idx", columnList = "user_id")
})
@EqualsAndHashCode
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Setter(value = AccessLevel.PUBLIC)
    @Column(columnDefinition = "TEXT")
    private String body;

    @Column
    @Setter(AccessLevel.PUBLIC)
    private Long repliesCount = 0L;

    @Column
    @Setter(AccessLevel.PUBLIC)
    private Long likesCount = 0L;

    @Column(nullable = false, updatable = false)
    private ZonedDateTime createdDateTime;

    @Column
    private ZonedDateTime updatedDateTime;

    @Column
    private ZonedDateTime deletedDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    public static PostEntity of(String body, UserEntity currentUser) {
        PostEntity post = new PostEntity();
        post.setBody(body);
        post.setUser(currentUser);
        return post;
    }

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
