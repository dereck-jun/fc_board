package com.fc_board.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Entity
@Getter
@Setter(value = AccessLevel.PRIVATE)
@Table(
        name = "\"like\"", indexes = {
        @Index(name = "like_user_id_post_id_idx", columnList = "user_id, post_id", unique = true),
})
@EqualsAndHashCode
public class LikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long id;

    @Column(nullable = false, updatable = false)
    private ZonedDateTime createdDateTime;  // 알림 기능에 필요할 수도 있기 때문에 따로 남겨둠

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private PostEntity post;

    public static LikeEntity of(UserEntity user, PostEntity post) {
        LikeEntity likeEntity = new LikeEntity();
        likeEntity.setUser(user);
        likeEntity.setPost(post);
        return likeEntity;
    }

    @PrePersist
    private void prePersist() {
        this.createdDateTime = ZonedDateTime.now();
    }
}
