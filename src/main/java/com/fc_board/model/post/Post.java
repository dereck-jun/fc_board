package com.fc_board.model.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fc_board.model.entity.PostEntity;

import java.time.ZonedDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Post(
        Long postId,
        String body,
        ZonedDateTime createdDateTime,
        ZonedDateTime updatedDateTime
) {
    public static Post from(PostEntity entity) {
        return new Post(
                entity.getId(),
                entity.getBody(),
                entity.getCreatedDateTime(),
                entity.getUpdatedDateTime()
        );
    }
}
