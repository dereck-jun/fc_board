package com.fc_board.repository;

import com.fc_board.model.entity.FollowEntity;
import com.fc_board.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<FollowEntity, Long> {
    List<FollowEntity> findByFollower(UserEntity follower);

    List<FollowEntity> findByFollowing(UserEntity following);

    //
    Optional<FollowEntity> findByFollowerAndFollowing(UserEntity follower, UserEntity following);
}
