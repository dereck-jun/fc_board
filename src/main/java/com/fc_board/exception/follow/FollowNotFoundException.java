package com.fc_board.exception.follow;

import com.fc_board.exception.ClientErrorException;
import com.fc_board.model.entity.UserEntity;
import org.springframework.http.HttpStatus;

public class FollowNotFoundException extends ClientErrorException {

    public FollowNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Follow not found");
    }

    public FollowNotFoundException(UserEntity follower, UserEntity following) {
        super(HttpStatus.NOT_FOUND,
                "Follow with follower "
                        + follower.getUsername()
                        + " and following "
                        + following.getUsername()
                        + " not found.");
    }
}
