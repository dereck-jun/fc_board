package com.fc_board.repository;

import com.fc_board.model.entity.PostEntity;
import com.fc_board.model.entity.ReplyEntity;
import com.fc_board.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepository extends JpaRepository<ReplyEntity, Long> {

    List<ReplyEntity> findByUser(UserEntity user);

    List<ReplyEntity> findByPost(PostEntity post);
}
