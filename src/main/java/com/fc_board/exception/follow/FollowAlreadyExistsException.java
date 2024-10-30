package com.fc_board.exception.follow;

import com.fc_board.exception.ClientErrorException;
import com.fc_board.model.entity.UserEntity;
import org.springframework.http.HttpStatus;

public class FollowAlreadyExistsException extends ClientErrorException {

    public FollowAlreadyExistsException() {
        super(HttpStatus.CONFLICT, "Follow already exists.");
    }

    public FollowAlreadyExistsException(UserEntity follower, UserEntity following) {
        super(HttpStatus.CONFLICT,
                "Follow with follower "
                        + follower.getUsername()
                        + " and following "
                        + following.getUsername()
                        + " already exists.");
    }
}
