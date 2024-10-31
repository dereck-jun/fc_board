package com.fc_board.service;

import com.fc_board.exception.post.PostNotFoundException;
import com.fc_board.exception.reply.ReplyNotFoundException;
import com.fc_board.exception.user.UserNotAllowedException;
import com.fc_board.exception.user.UserNotFoundException;
import com.fc_board.model.entity.PostEntity;
import com.fc_board.model.entity.ReplyEntity;
import com.fc_board.model.entity.UserEntity;
import com.fc_board.model.reply.Reply;
import com.fc_board.model.reply.ReplyPatchRequestBody;
import com.fc_board.model.reply.ReplyRequestBody;
import com.fc_board.repository.PostRepository;
import com.fc_board.repository.ReplyRepository;
import com.fc_board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public List<Reply> getRepliesByPostId(Long postId) {
        PostEntity postEntity = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));
        List<ReplyEntity> replyEntities = replyRepository.findByPost(postEntity);
        return replyEntities.stream().map(Reply::from).toList();
    }

    @Transactional
    public Reply createReply(Long postId, ReplyRequestBody replyRequestBody, UserEntity currentUser) {
        var postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        var replyEntity = replyRepository.save(ReplyEntity.of(replyRequestBody.body(), currentUser, postEntity));

        postEntity.setRepliesCount(postEntity.getRepliesCount() + 1);
        postRepository.save(postEntity);

        return Reply.from(replyEntity);
    }

    public Reply updateReply(Long postId, Long replyId, ReplyPatchRequestBody replyPatchRequestBody, UserEntity currentUser) {
        postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));
        var replyEntity = replyRepository.findById(replyId).orElseThrow(() -> new ReplyNotFoundException(replyId));

        if (!replyEntity.getUser().equals(currentUser)) {
            throw new UserNotAllowedException();
        }

        replyEntity.setBody(replyPatchRequestBody.body());
        return Reply.from(replyRepository.save(replyEntity));
    }

    @Transactional
    public void deleteReply(Long postId, Long replyId, UserEntity currentUser) {
        var postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        var replyEntity = replyRepository.findById(replyId)
                .orElseThrow(() -> new ReplyNotFoundException(replyId));

        if (!replyEntity.getUser().equals(currentUser)) {
            throw new UserNotAllowedException();
        }

        replyRepository.delete(replyEntity);

        postEntity.setRepliesCount(Math.max(0, postEntity.getRepliesCount() - 1));
        postRepository.save(postEntity);
    }

    public List<Reply> getRepliesByUser(String username) {
        var userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        var replyEntities = replyRepository.findByUser(userEntity);
        return replyEntities.stream().map(Reply::from).toList();
    }
}
