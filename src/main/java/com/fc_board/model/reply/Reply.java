package com.fc_board.model.reply;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fc_board.model.entity.ReplyEntity;
import com.fc_board.model.post.Post;
import com.fc_board.model.user.User;

import java.time.ZonedDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Reply(
        Long replyId,
        String body,
        User user,
        Post post,
        ZonedDateTime createdDateTime,
        ZonedDateTime updatedDateTime
) {
    public static Reply from(ReplyEntity entity) {
        return new Reply(
                entity.getId(),
                entity.getBody(),
                User.from(entity.getUser()),
                Post.from(entity.getPost()),
                entity.getCreatedDateTime(),
                entity.getUpdatedDateTime()
        );
    }
}
