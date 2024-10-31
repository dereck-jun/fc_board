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
        name = "\"follow\"", indexes = {
        @Index(name = "follow_follower_following_idx", columnList = "follower, following", unique = true),
})
@EqualsAndHashCode
public class FollowEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_id")
    private Long id;

    @Column(nullable = false, updatable = false)
    private ZonedDateTime createdDateTime;  // 알림 기능에 필요할 수도 있기 때문에 따로 남겨둠

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower")
    private UserEntity follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following")
    private UserEntity following;

    public static FollowEntity of(UserEntity follower, UserEntity following) {
        FollowEntity followEntity = new FollowEntity();
        followEntity.setFollower(follower);
        followEntity.setFollowing(following);
        return followEntity;
    }

    @PrePersist
    private void prePersist() {
        this.createdDateTime = ZonedDateTime.now();
    }
}
