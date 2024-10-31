package com.fc_board.service;

import com.fc_board.exception.post.PostNotFoundException;
import com.fc_board.exception.user.UserNotAllowedException;
import com.fc_board.exception.user.UserNotFoundException;
import com.fc_board.model.entity.LikeEntity;
import com.fc_board.model.entity.UserEntity;
import com.fc_board.model.post.Post;
import com.fc_board.model.post.PostPatchRequestBody;
import com.fc_board.model.post.PostRequestBody;
import com.fc_board.model.entity.PostEntity;
import com.fc_board.repository.LikeRepository;
import com.fc_board.repository.PostRepository;
import com.fc_board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

    public List<Post> getPosts() {
        List<PostEntity> posts = postRepository.findAll();
        return posts.stream().map(Post::from).toList();
    }

    public Post getPostByPostId(Long postId) {
        var optionalPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
        return Post.from(optionalPost);
    }

    public Post createPost(PostRequestBody postRequestBody, UserEntity currentUser) {
        var postEntity = postRepository.save(
                PostEntity.of(postRequestBody.body(), currentUser)
        );
        return Post.from(postEntity);
    }

    public Post updatePost(Long postId, PostPatchRequestBody patchRequestBody, UserEntity currentUser) {
        var postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        if (!postEntity.getUser().equals(currentUser)) {
            throw new UserNotAllowedException();
        }

        postEntity.setBody(patchRequestBody.body());
        var updateEntity = postRepository.save(postEntity);
        return Post.from(updateEntity);
    }

    public void deletePost(Long postId, UserEntity currentUser) {
        var postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        if (!postEntity.getUser().equals(currentUser)) {
            throw new UserNotAllowedException();
        }

        postRepository.delete(postEntity);
    }

    public List<Post> getPostByUsername(String username) {
        var userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        var postEntities = postRepository.findByUser(userEntity);
        return postEntities.stream().map(Post::from).toList();
    }

    @Transactional
    public Post toggleLike(Long postId, UserEntity currentUser) {
        var postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        Optional<LikeEntity> likeEntity = likeRepository.findByUserAndPost(currentUser, postEntity);

        if (likeEntity.isPresent()) {
            likeRepository.delete(likeEntity.get());
            postEntity.setLikesCount(Math.max(0, postEntity.getLikesCount() - 1));
        } else {
            likeRepository.save(LikeEntity.of(currentUser, postEntity));
            postEntity.setLikesCount(postEntity.getLikesCount() + 1);
        }

        return Post.from(postRepository.save(postEntity));
    }
}
