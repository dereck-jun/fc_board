package com.fc_board.repository;

import com.fc_board.model.entity.LikeEntity;
import com.fc_board.model.entity.PostEntity;
import com.fc_board.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<LikeEntity, Long> {
    List<LikeEntity> findByUser(UserEntity user);

    List<LikeEntity> findByPost(PostEntity post);

    // LikeEntity에서 index로 UserEntity와 PostEntity를 하나로 묶어놨기 때문에 결과 값은 항상 1 or 0 이다.
    Optional<LikeEntity> findByUserAndPost(UserEntity user, PostEntity post);
}
