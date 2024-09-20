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
@SQLDelete(sql = "update reply set deleted_date_time = current_timestamp where post_id = ?")
@SQLRestriction("deleted_date_time is null")
@Table(name = "reply", indexes = {
        @Index(name = "reply_user_id_idx", columnList = "user_id"),
        @Index(name = "reply_post_id_idx", columnList = "post_id")
})
@EqualsAndHashCode
public class ReplyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_id")
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Column(nullable = false, updatable = false)
    private ZonedDateTime createdDateTime;

    @Column
    private ZonedDateTime updatedDateTime;

    @Column(nullable = true)
    private ZonedDateTime deletedDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private PostEntity post;

    public void addUser(UserEntity user) {
        this.user = user;
    }

    public void addPost(PostEntity post) {
        this.post = post;
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

    public static ReplyEntity of(String body, UserEntity user, PostEntity post) {
        ReplyEntity replyEntity = new ReplyEntity();
        replyEntity.setBody(body);
        replyEntity.addUser(user);
        replyEntity.addPost(post);
        return replyEntity;
    }
}
