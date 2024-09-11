package com.fc_board.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Post {
    private Long postId;
    private String body;
    private ZonedDateTime createdDateTime;

    public Post(Long postId, String body, ZonedDateTime createdDateTime) {
        this.postId = postId;
        this.body = body;
        this.createdDateTime = createdDateTime;
    }
}
