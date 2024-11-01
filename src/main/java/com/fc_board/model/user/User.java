package com.fc_board.model.user;

import com.fc_board.model.entity.UserEntity;

import java.time.ZonedDateTime;

public record User(Long userId,
                   String username,
                   String profile,
                   String description,
                   Long followersCount,
                   Long followingsCount,
                   ZonedDateTime createdDateTime,
                   ZonedDateTime updatedDateTime,
                   Boolean isFollowing
) {

    public static User from(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getProfile(),
                entity.getDescription(),
                entity.getFollowersCount(),
                entity.getFollowingsCount(),
                entity.getCreatedDateTime(),
                entity.getUpdatedDateTime(),
                null
        );
    }

    public static User from(UserEntity entity, boolean isFollowing) {
        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getProfile(),
                entity.getDescription(),
                entity.getFollowersCount(),
                entity.getFollowingsCount(),
                entity.getCreatedDateTime(),
                entity.getUpdatedDateTime(),
                isFollowing
        );
    }
}
