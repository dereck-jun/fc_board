package com.fc_board.model.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fc_board.model.entity.PostEntity;
import com.fc_board.model.user.User;

import java.time.ZonedDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Post(
        Long postId,
        String body,
        Long repliesCount,
        Long likesCount,
        User user,
        ZonedDateTime createdDateTime,
        ZonedDateTime updatedDateTime,
        ZonedDateTime deletedDateTime,
        Boolean isLiking
) {
    public static Post from(PostEntity entity) {
        return new Post(
                entity.getId(),
                entity.getBody(),
                entity.getRepliesCount(),
                entity.getLikesCount(),
                User.from(entity.getUser()),
                entity.getCreatedDateTime(),
                entity.getUpdatedDateTime(),
                entity.getDeletedDateTime(),
                null
        );
    }

    public static Post from(PostEntity entity, boolean isLiking) {
        return new Post(
                entity.getId(),
                entity.getBody(),
                entity.getRepliesCount(),
                entity.getLikesCount(),
                User.from(entity.getUser()),
                entity.getCreatedDateTime(),
                entity.getUpdatedDateTime(),
                entity.getDeletedDateTime(),
                isLiking
        );
    }
}
