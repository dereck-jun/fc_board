package com.fc_board.service;

import com.fc_board.exception.post.PostNotFoundException;
import com.fc_board.model.post.Post;
import com.fc_board.model.post.PostPatchRequestBody;
import com.fc_board.model.post.PostRequestBody;
import com.fc_board.model.entity.PostEntity;
import com.fc_board.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public List<Post> getPosts() {
        List<PostEntity> posts = postRepository.findAll();
        return posts.stream().map(Post::from).toList();
    }

    public Post getPostByPostId(Long postId) {
        var optionalPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
        return Post.from(optionalPost);
    }

    public Post createPost(PostRequestBody postRequestBody) {
        var entity = new PostEntity();
        entity.setBody(postRequestBody.body());

        var savedPost = postRepository.save(entity);

        return Post.from(savedPost);
    }

    public Post updatePost(Long postId, PostPatchRequestBody patchRequestBody) {
        var entity = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
        entity.setBody(patchRequestBody.body());
        var updateEntity = postRepository.save(entity);
        return Post.from(updateEntity);
    }

    public void deletePost(Long postId) {
        var entity = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
        postRepository.delete(entity);
    }
}
