package com.fc_board.model.user;

import com.fc_board.model.entity.UserEntity;

import java.time.ZonedDateTime;

public record User(Long userId,
                   String username,
                   String profile,
                   String description,
                   ZonedDateTime createdDateTime,
                   ZonedDateTime updatedDateTime,
                   ZonedDateTime deletedDateTime) {

    public static User from(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getProfile(),
                entity.getDescription(),
                entity.getCreatedDateTime(),
                entity.getUpdatedDateTime(),
                entity.getDeletedDateTime()
        );
    }
}
